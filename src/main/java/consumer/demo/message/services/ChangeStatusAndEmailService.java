package consumer.demo.message.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import consumer.demo.entities.Order;
import consumer.demo.entities.OrderStatus;
import consumer.demo.message.model.ChangeOrderStatusMessage;
import consumer.demo.message.model.ProductsAppearedMessage;
import consumer.demo.repositories.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ChangeStatusAndEmailService {
    private final JavaMailSender javaMailSender;
    private final OrderRepository orderRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    public EmailService(JavaMailSender javaMailSender, OrderRepository orderRepository) {
        this.javaMailSender = javaMailSender;
        this.objectMapper = new ObjectMapper();
        this.orderRepository = orderRepository;
    }

    public ChangeStatusAndEmailService() {
    }

    public void productsAppeared(String mes) {
        try {
            ProductsAppearedMessage message = objectMapper.readValue(mes, ProductsAppearedMessage.class);
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(message.getEmail());
            msg.setSubject("");
            String text = String.format("Здравствуйте, %s!\n\nВаш отзыв №%d на машину %s успешно добавлен.\n" +
                            "Он будет виден другим пользователям после того, как пройдёт модерацию.\n\n" +
                            "С уважением, команда Wroom.ru-feldme.",
                    reviewMessage.getName(), reviewMessage.getReviewId(), reviewMessage.getCarModel());
            msg.setText(text);
            javaMailSender.send(msg);
            log.info("Message to {} sent.", reviewMessage.getEmail());
        } catch (Exception ignored) {
            log.error("Sending message error");
        }
    }

    public boolean changeStatus(String mes){
        try {
            ChangeOrderStatusMessage message = objectMapper.readValue(mes, ChangeOrderStatusMessage.class);
            Order order = orderRepository.findById(message.getOrderId()).orElseThrow(() -> new IllegalArgumentException("Wrong id"));
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(message.getEmail());
            msg.setSubject("Изменение статуса заказа");
            String text = String.format("Здравствуйте, %s!\n\nВаш заказ №%d изменил статус.\n" +
                            "%s ---> %s.\n\n" +
                            "С уважением, команда разработчков лабораторной №3.",
                    message.getUsername(), message.getOrderId(), order.getStatus(), message.getNewStatus());
            msg.setText(text);
            javaMailSender.send(msg);
            log.info("Message to {} sent.", reviewMessage.getEmail());
            order.setStatus(message.getNewStatus());
            orderRepository.save(order);
        }
        catch(Exception e){
            return false;
        }
        catch (Exception ignored){
            log.error("Sending message error");
        }
    }
}