package net.mediascope.hr.hrtelegrambot.router;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.MessageEntity;
import com.pengrad.telegrambot.model.Update;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.pengrad.telegrambot.model.MessageEntity.Type.bot_command;
import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.ok;

/**
 * @author Евгений Уткин (evgeny.utkin@mediascope.net)
 */
@RestController
@Slf4j
public class DispatcherTelegramController implements BeanFactoryAware {

    private ListableBeanFactory beanFactory;

    private Map<String, MethodHolder> botCommandHandlers = new ConcurrentHashMap<>();

    private void init() {
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
    public ResponseEntity update(@RequestBody Update update) {
        log.info(update.toString());
        Message message = update.message();
        if (message != null) {
            for (MessageEntity messageEntity : message.entities()) {
                if (messageEntity.type() == bot_command) {
                    String command = message.text().substring(messageEntity.offset(), messageEntity.length());
                    log.info("Receive command: {}", command);
                    botCommandHandlers.get(command).invoke();
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
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (ListableBeanFactory) beanFactory;
    }

    private final class MethodHolder {
        private final Object object;
        private final Method method;

        private MethodHolder(Object object, Method method) {
            this.object = object;
            this.method = method;
        }

        private Object invoke(Object... args) {
            try {
                return method.invoke(object, args);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
    }
}
