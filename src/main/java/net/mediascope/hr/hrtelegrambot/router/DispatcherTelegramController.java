package net.mediascope.hr.hrtelegrambot.router;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.MessageEntity;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
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
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.pengrad.telegrambot.model.MessageEntity.Type.bot_command;
import static com.pengrad.telegrambot.model.request.ParseMode.HTML;
import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.ok;

/**
 * @author Евгений Уткин (evgeny.utkin@mediascope.net)
 */
@RestController
@Slf4j
public class DispatcherTelegramController extends DispatcherServlet implements ApplicationContextAware {

    private ListableBeanFactory beanFactory;

    private ApplicationContext applicationContext;

    private Map<String, MethodHolder> botCommandHandlers = new ConcurrentHashMap<>();

    @Autowired
    private TelegramBot bot;

    @PostConstruct
    private void initHandlers() {
        super.onRefresh(applicationContext);
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
            }
        }
    }

    /*

     */
    @PostMapping("/api/rest/update")
    public ResponseEntity update(@RequestBody Update update, HttpServletRequest request) throws Exception {
        log.info(update.toString());
        Message message = update.message();
        if (message != null) {
            for (MessageEntity messageEntity : message.entities()) {
                if (messageEntity.type() == bot_command) {
                    String command = message.text().substring(messageEntity.offset(), messageEntity.length());
                    log.info("Receive command: {}", command);
                    ModelAndView modelAndView = botCommandHandlers.get(command).invoke(update);
                    TelegramFakeHttpResponse response = new TelegramFakeHttpResponse();
                    render(modelAndView, request, response);

                    bot.execute(new SendMessage(update.message().chat().id(), new String(response.getBody())).parseMode(HTML));
                }
            }
        }
        return ok().build();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity handleException(Exception ex) {
        log.error(ex.getMessage(), ex);
        return badRequest().body(ex.getMessage());
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        this.beanFactory = applicationContext;
    }

    private final class MethodHolder {
        private final Object object;
        private final Method method;

        private MethodHolder(Object object, Method method) {
            this.object = object;
            this.method = method;
        }

        private ModelAndView invoke(Object... args) {
            try {
                return (ModelAndView) method.invoke(object, args);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
    }
}
