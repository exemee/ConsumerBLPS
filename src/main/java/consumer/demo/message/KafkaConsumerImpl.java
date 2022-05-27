package consumer.demo.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import consumer.demo.message.model.ChangeOrderStatusMessage;
import consumer.demo.message.services.ChangeStatusAndEmailService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Properties;

@Service
public class KafkaConsumerImpl {
    private final ChangeStatusAndEmailService service;

    @Autowired
    public KafkaConsumerImpl(ChangeStatusAndEmailService service){
        this.service = service;
      //  runConsumer();
    }

    private static Consumer<Long, String> createConsumer() {
        final Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("group.id", "blps");
        props.put("enable.auto.commit", "true");
        props.put("auto.commit.interval.ms", "1000");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.IntegerDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        // Create the consumer using props.
        final Consumer<Long, String> consumer =
                new KafkaConsumer<>(props);

        // Subscribe to the topic.
        consumer.subscribe(Collections.singletonList("products-appeared"));
        consumer.subscribe(Collections.singletonList("status-changed"));

        return consumer;
    }

    void runConsumer() {
            final Consumer<Long, String> consumer = createConsumer();

            while (true) {
                final ConsumerRecords<Long, String> consumerRecords =
                        consumer.poll(1000);
                consumerRecords.forEach(record -> {
                });
                consumer.commitAsync();
            }
    }

    }
