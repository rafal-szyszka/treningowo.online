package com.prodactivv.app.core.events;

import com.prodactivv.app.core.exceptions.NotFoundException;
import com.prodactivv.app.core.utils.HashGenerator;
import com.prodactivv.app.user.model.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final HashGenerator hashGenerator;
    private final EventRepository eventRepository;

    public enum EventType {
        CHANGE_PASSWORD("change_password");

        @Getter
        private final String type;

        EventType(String type) {
            this.type = type;
        }
    }

    public Event createUserBasedEvent(EventType eventType, User user, LocalDate validUntil) {
        List<String> hashParts = Arrays.asList(
                user.getEmail(),
                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        );

        return eventRepository.save(
                Event.builder()
                        .user(user)
                        .type(eventType.type)
                        .code(hashGenerator.generateSha384Hash(hashParts))
                        .validUntil(validUntil)
                        .build()
        );
    }

    public Event getEventByHash(String eventHash) throws NotFoundException {
        return eventRepository.findEventByCode(eventHash).orElseThrow(new NotFoundException(String.format("Event %s not found!", eventHash)));
    }

    public void deleteEvent(Event eventByHash) {
        eventRepository.delete(eventByHash);
    }

}
