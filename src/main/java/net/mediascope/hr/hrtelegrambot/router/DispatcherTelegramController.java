package net.mediascope.hr.hrtelegrambot.router;

import com.pengrad.telegrambot.Callback;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.MessageEntity;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import static com.pengrad.telegrambot.model.MessageEntity.Type.bot_command;
import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.ok;

/**
 * @author Евгений Уткин (evgeny.utkin@mediascope.net)
 */
@RestController
@Slf4j
public class DispatcherTelegramController implements ApplicationContextAware {

    private ListableBeanFactory beanFactory;

    private Map<String, MethodHolder> botCommandHandlers = new ConcurrentHashMap<>();
    private Map<String, MethodHolder> botCallbackQueryHandlers = new ConcurrentHashMap<>();

    @Autowired
    private TelegramBot bot;


    @PostConstruct
    private void initHandlers() {
        Map<String, Object> beans = beanFactory.getBeansWithAnnotation(TelegramController.class);
        for (Object controller : beans.values()) {
            Class<?> controllerClass = controller.getClass();
            for (Method method : controllerClass.getDeclaredMethods()) {
                CommandMapping commandMapping = method.getAnnotation(CommandMapping.class);
                if (commandMapping != null) {
                    String[] commands = commandMapping.value();
                    for (String command : commands) {
                        botCommandHandlers.put(command, new MethodHolder(controller, method));
                    }
                }
                CallbackQueryMapping callbackQueryMapping = method.getAnnotation(CallbackQueryMapping.class);
                if (callbackQueryMapping != null) {
                    String[] callbackQueries = callbackQueryMapping.value();
                    for (String callbackQuery : callbackQueries) {
                        botCallbackQueryHandlers.put(callbackQuery, new MethodHolder(controller, method));
                    }
                }
            }
        }
    }

    /*

     */
    @PostMapping("/api/rest/update")
    public ResponseEntity update(@RequestBody Update update) {
        log.info(update.toString());
        SendMessage response =  null;
        Message message = update.message();
        if (message != null) {
            for (MessageEntity messageEntity : message.entities()) {
                if (messageEntity.type() == bot_command) {
                    String command = message.text().substring(messageEntity.offset(), messageEntity.length());
                    log.info("Receive command: {}", command);
                    response = botCommandHandlers.get(command).invoke(update.message().chat().id(), update);
                }
            }
        }
        CallbackQuery callbackQuery = update.callbackQuery();
        if (callbackQuery != null) {
            String data = callbackQuery.data();
            log.info("Receive callback data: {}", data);
            response = botCallbackQueryHandlers.get(data).invoke(callbackQuery.message().chat().id(), update);

        }
        log.info("Send message: {}", Objects.toString(response));
        bot.execute(response, new Callback<SendMessage, SendResponse>() {
            @Override
            public void onResponse(SendMessage request, SendResponse response) {
                log.info("Response: {}, error code {}", response.description(), response.errorCode());
            }

            @Override
            public void onFailure(SendMessage request, IOException e) {
                log.error(e.getMessage(), e);
            }
        });
        return ok().build();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity handleException(Exception ex) {
        log.error(ex.getMessage(), ex);
        return badRequest().body(ex.getMessage());
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.beanFactory = applicationContext;
    }

    private final class MethodHolder {
        private final Object object;
        private final Method method;

        private MethodHolder(Object object, Method method) {
            this.object = object;
            this.method = method;
        }

        private SendMessage invoke(Object chatId, Object... args) {
            try {
                View response = (View) method.invoke(object, args);
                SendMessage sendMessage = new SendMessage(response.chat().orElse(chatId), response.message());
                response.keyboard().ifPresent(sendMessage::replyMarkup);
                return sendMessage;
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
    }
}
