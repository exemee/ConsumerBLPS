package consumer.demo.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import consumer.demo.message.model.ChangeOrderStatusMessage;
import consumer.demo.message.services.ChangeStatusAndEmailService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Properties;

@Service
@RequiredArgsConstructor
public class KafkaConsumerImpl {
    private final ChangeStatusAndEmailService service;


    private static Consumer<Long, String> createConsumer() {
        final Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("acks", "all");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.IntSerializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringSerializer");

        // Create the consumer using props.
        final Consumer<Long, String> consumer =
                new KafkaConsumer<>(props);

        // Subscribe to the topic.
        consumer.subscribe(Collections.singletonList("products-appeared"));
        consumer.subscribe(Collections.singletonList("status-changed"));

        return consumer;
    }

    void runConsumer() throws InterruptedException {
        final Consumer<Long, String> consumer = createConsumer();

        final int giveUp = 100;   int noRecordsCount = 0;

        while (true) {
            final ConsumerRecords<Long, String> consumerRecords =
                    consumer.poll(1000);

            if (consumerRecords.count()==0) {
                noRecordsCount++;
                if (noRecordsCount > giveUp) break;
                else continue;
            }

            consumerRecords.forEach(record -> {
                System.out.printf("Consumer Record:(%d, %s, %d, %d)\n",
                        record.key(), record.value(),
                        record.partition(), record.topic());
                if(record.topic()=="status-changed")
                service.changeStatus(record.value());
                else service.productsAppeared(record.value());
            });

            consumer.commitAsync();
        }
        consumer.close();
        System.out.println("DONE");
    }

    }