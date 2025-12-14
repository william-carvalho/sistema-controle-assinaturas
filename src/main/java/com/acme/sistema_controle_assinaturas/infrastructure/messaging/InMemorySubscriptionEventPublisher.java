package com.acme.sistema_controle_assinaturas.infrastructure.messaging;

import com.acme.sistema_controle_assinaturas.domain.messaging.SubscriptionEventPublisher;
import com.acme.sistema_controle_assinaturas.domain.model.events.SubscriptionEventMessage;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@ConditionalOnProperty(name = "app.messaging.mock", havingValue = "true")
public class InMemorySubscriptionEventPublisher implements SubscriptionEventPublisher {

    private final List<SubscriptionEventMessage> published = new CopyOnWriteArrayList<>();

    @Override
    public void publish(SubscriptionEventMessage event) {
        published.add(event);
    }

    public List<SubscriptionEventMessage> publishedEvents() {
        return Collections.unmodifiableList(published);
    }

    public void clear() {
        published.clear();
    }
}
