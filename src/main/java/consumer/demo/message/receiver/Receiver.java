package consumer.demo.message.receiver;

import consumer.demo.message.model.ChangeOrderStatusMessage;
import consumer.demo.message.services.ChangeStatusAndEmailService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class Receiver {
    private static final Logger LOG = LoggerFactory.getLogger(Receiver.class);
    private final ChangeStatusAndEmailService service;

    @KafkaListener(topics = "products-appeared")
    public void listenProducts(@Payload(required = false)  ChangeOrderStatusMessage message) {
        service.productsAppeared(message);
    }

    @KafkaListener(topics = "status-changed")
    public void listenChanges(@Payload(required = false) ChangeOrderStatusMessage message) {
        service.changeStatus(message);
    }
}
