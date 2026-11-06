package com.platform.webservice.listener;

import com.platform.webservice.config.RabbitMQConfig;
import com.platform.webservice.dto.UserRegisteredEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class UserRegisteredEventListener {

    private static final Logger log = LoggerFactory.getLogger(UserRegisteredEventListener.class);
    private final List<UserRegisteredEvent> events = new CopyOnWriteArrayList<>();

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void handleUserRegistered(UserRegisteredEvent event) {
        log.info("Received user.registered event for user: {}, email: {}", event.getUsername(), event.getEmail());
        events.add(0, event); // Most recent first
        while (events.size() > 50) {
            events.remove(events.size() - 1);
        }
    }

    public List<UserRegisteredEvent> getRecentEvents() {
        return Collections.unmodifiableList(new ArrayList<>(events));
    }
}

