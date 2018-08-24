package net.mediascope.hr.hrtelegrambot.router;

import com.pengrad.telegrambot.model.request.Keyboard;

import java.util.Objects;
import java.util.Optional;

/**
 * @author Евгений Уткин (evgeny.utkin@mediascope.net)
 */
public class View {

    private final String message;

    private Keyboard keyboard;

    private Object chat;

    public View(String message) {
        this.message = message;
    }

    public String message() {
        return message;
    }

    public Optional<Keyboard> keyboard() {
        return Optional.ofNullable(keyboard);
    }

    public Optional<Object> chat() {
        return Optional.ofNullable(chat);
    }

    public View keyboard(Keyboard keyboard) {
        this.keyboard = Objects.requireNonNull(keyboard, "keyboard must not be null");
        return this;
    }

    public View chat(Object chat) {
        this.chat = Objects.requireNonNull(chat, "chat must not be null");
        return this;
    }
}
