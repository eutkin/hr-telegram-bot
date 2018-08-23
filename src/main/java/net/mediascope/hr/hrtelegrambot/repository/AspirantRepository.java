package net.mediascope.hr.hrtelegrambot.repository;

import net.mediascope.hr.hrtelegrambot.model.Aspirant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * @author Евгений Уткин (evgeny.utkin@mediascope.net)
 */
public interface AspirantRepository extends JpaRepository<Aspirant, UUID> {
}
