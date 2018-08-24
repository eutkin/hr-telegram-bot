package net.mediascope.hr.hrtelegrambot.controller;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import lombok.extern.slf4j.Slf4j;
import net.mediascope.hr.hrtelegrambot.model.Aspirant;
import net.mediascope.hr.hrtelegrambot.router.CallbackQueryMapping;
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
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup(new InlineKeyboardButton[][]{
                {new InlineKeyboardButton("О компании").callbackData("/about-company")},
                {new InlineKeyboardButton("О вакансиях").callbackData("/about-vacancy")},
                {new InlineKeyboardButton("О 'плюшках'").callbackData("/about-rewards")},
                {new InlineKeyboardButton("Связаться с нами").callbackData("/call-me")}
        });
        return new View("Привет " + aspirant.getName()).keyboard(keyboard);
    }

    @CallbackQueryMapping({"/about-company","/about-vacancy","/about-rewards","/call-me"})
    public View aboutCompany(Update update) {
        return new View("Пока ничего нет, но вы держитесь");
    }
}
