package net.mediascope.hr.hrtelegrambot.controller;

import lombok.extern.slf4j.Slf4j;
import net.mediascope.hr.hrtelegrambot.router.CommandMapping;
import net.mediascope.hr.hrtelegrambot.router.TelegramController;

/**
 * @author Евгений Уткин (evgeny.utkin@mediascope.net)
 */
@TelegramController
@Slf4j
public class C {

    @CommandMapping("/start")
    public void method() {
        log.info("Handle of command");
    }
}
