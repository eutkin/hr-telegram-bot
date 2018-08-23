package net.mediascope.hr.hrtelegrambot.model;

import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.UUID;

/**
 * @author Евгений Уткин (evgeny.utkin@mediascope.net)
 */
@Entity
@Data
@Accessors(chain = true)
public class Aspirant {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private UUID id;
    private Long chatId;
    private String firstName;
    private String lastName;

    public String getName() {
        return String.join(" ", firstName, lastName);
    }
}