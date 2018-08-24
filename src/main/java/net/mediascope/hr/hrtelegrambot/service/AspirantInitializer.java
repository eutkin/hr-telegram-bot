package net.mediascope.hr.hrtelegrambot.service;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import net.mediascope.hr.hrtelegrambot.model.Aspirant;
import net.mediascope.hr.hrtelegrambot.repository.AspirantRepository;
import org.springframework.stereotype.Service;

/**
 * @author Евгений Уткин (evgeny.utkin@mediascope.net)
 */
@Service
public class AspirantInitializer implements EntityInitializer<Aspirant> {

    private final AspirantRepository aspirantRepository;

    public AspirantInitializer(AspirantRepository aspirantRepository) {
        this.aspirantRepository = aspirantRepository;
    }

    @Override
    public Aspirant init(Update update) {
        User user = update.message().from();
        Long chatId = update.message().chat().id();
        Aspirant aspirant = new Aspirant()
                .setChatId(chatId)
                .setFirstName(user.firstName())
                .setLastName(user.lastName())
                ;
        return aspirantRepository.save(aspirant);
    }
}
