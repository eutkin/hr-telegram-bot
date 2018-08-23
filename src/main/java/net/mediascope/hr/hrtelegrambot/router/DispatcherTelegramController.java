package net.mediascope.hr.hrtelegrambot.router;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Update;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;

import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.ok;

/**
 * @author Евгений Уткин (evgeny.utkin@mediascope.net)
 */
@Component
public class DispatcherTelegramController {

    @PostMapping("/api/rest/update")
    public ResponseEntity update(Update update) {
        CallbackQuery callbackQuery = update.callbackQuery();
        return ok().build();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity handleException(Exception ex) {
        return badRequest().body(ex.getMessage());
    }
}
