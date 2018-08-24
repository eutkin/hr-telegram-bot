package net.mediascope.hr.hrtelegrambot.controller;

import com.pengrad.telegrambot.model.Update;
import lombok.extern.slf4j.Slf4j;
import net.mediascope.hr.hrtelegrambot.model.Aspirant;
import net.mediascope.hr.hrtelegrambot.router.CommandMapping;
import net.mediascope.hr.hrtelegrambot.router.TelegramController;
import net.mediascope.hr.hrtelegrambot.router.View;
import net.mediascope.hr.hrtelegrambot.service.EntityInitializer;

/**
 * @author Евгений Уткин (evgeny.utkin@mediascope.net)
 */
@TelegramController
@Slf4j
public class AspirantController {

    private final EntityInitializer<Aspirant> entityInitializer;

    public AspirantController(EntityInitializer<Aspirant> entityInitializer) {
        this.entityInitializer = entityInitializer;
    }

    @CommandMapping("/start")
    public View saveAspirant(Update update) {
        Aspirant aspirant = entityInitializer.init(update);
        return new View("Привет " + aspirant.getName());
    }
}
