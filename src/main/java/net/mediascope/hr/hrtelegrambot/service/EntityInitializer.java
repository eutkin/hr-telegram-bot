package net.mediascope.hr.hrtelegrambot.service;

import com.pengrad.telegrambot.model.Update;

/**
 * @author Евгений Уткин (evgeny.utkin@mediascope.net)
 */
public interface EntityInitializer<T> {

    T init(Update update);
}
