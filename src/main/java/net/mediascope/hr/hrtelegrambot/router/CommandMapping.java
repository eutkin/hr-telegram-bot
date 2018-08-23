package net.mediascope.hr.hrtelegrambot.router;

import java.lang.annotation.*;

/**
 * @author Евгений Уткин (evgeny.utkin@mediascope.net)
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CommandMapping {

    String[] value();
}
