package net.mediascope.hr.hrtelegrambot.router;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Update;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.ok;

/**
 * @author Евгений Уткин (evgeny.utkin@mediascope.net)
 */
@RestController
@Slf4j
public class DispatcherTelegramController {

    @PostMapping("/api/rest/update")
    public ResponseEntity update(Update update) {
        log.info(update.toString());
        CallbackQuery callbackQuery = update.callbackQuery();
        return ok().build();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity handleException(Exception ex) {
        return badRequest().body(ex.getMessage());
    }
}
