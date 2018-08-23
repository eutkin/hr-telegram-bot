package net.mediascope.hr.hrtelegrambot.router;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Indexed;

import java.lang.annotation.*;

/**
 * @author Евгений Уткин (evgeny.utkin@mediascope.net)
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Indexed
@Component
public @interface TelegramController {
}
