package net.mediascope.hr.hrtelegrambot.router;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.MessageEntity;
import com.pengrad.telegrambot.model.Update;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static com.pengrad.telegrambot.model.MessageEntity.Type.bot_command;
import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.ok;

/**
 * @author Евгений Уткин (evgeny.utkin@mediascope.net)
 */
@RestController
@Slf4j
public class DispatcherTelegramController {
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
}
