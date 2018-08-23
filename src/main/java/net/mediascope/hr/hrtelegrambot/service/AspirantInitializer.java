package net.mediascope.hr.hrtelegrambot.service;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Update;
import net.mediascope.hr.hrtelegrambot.model.Aspirant;
import net.mediascope.hr.hrtelegrambot.repository.AspirantRepository;

/**
 * @author Евгений Уткин (evgeny.utkin@mediascope.net)
 */
public class AspirantInitializer {

    private final AspirantRepository aspirantRepository;

    public AspirantInitializer(AspirantRepository aspirantRepository) {
        this.aspirantRepository = aspirantRepository;
    }

    public void init(Update update) {
        Chat chat = update.message().chat();
        Long id = chat.id();
        Aspirant aspirant = new Aspirant()
                .setChatId(id)
                .setFirstName(chat.firstName())
                .setLastName(chat.lastName())
                ;
        aspirantRepository.save(aspirant);
    }
}
