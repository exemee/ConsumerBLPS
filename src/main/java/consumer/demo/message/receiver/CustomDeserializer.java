package consumer.demo.message.receiver;

import com.fasterxml.jackson.databind.ObjectMapper;
import consumer.demo.message.model.ChangeOrderStatusMessage;
import lombok.NoArgsConstructor;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
@NoArgsConstructor
public class CustomDeserializer<T> implements Deserializer<ChangeOrderStatusMessage> {

    private Logger logger = LogManager.getLogger(this.getClass());


    @Override
    public void configure(Map map, boolean b) {

    }

    @Override
    public ChangeOrderStatusMessage deserialize(String s, byte[] bytes) {
        ObjectMapper mapper = new ObjectMapper();
        ChangeOrderStatusMessage obj = null;
        try {
            obj = mapper.readValue(bytes, ChangeOrderStatusMessage.class);
        } catch (Exception e) {

            logger.error(e.getMessage());
        }
        return obj;
    }

    @Override
    public void close() {

    }
}
