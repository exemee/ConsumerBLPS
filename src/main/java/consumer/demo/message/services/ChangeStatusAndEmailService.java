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
    public ChangeStatusAndEmailService(JavaMailSender javaMailSender, OrderRepository orderRepository) {
        this.javaMailSender = javaMailSender;
        this.objectMapper = new ObjectMapper();
        this.orderRepository = orderRepository;
    }


    public boolean productsAppeared(String mes) {
        try {
            ProductsAppearedMessage message = objectMapper.readValue(mes, ProductsAppearedMessage.class);
            Order order = orderRepository.findById(message.getOrderId()).orElseThrow(() -> new IllegalArgumentException("Wrong id"));
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(message.getEmail());
            msg.setSubject("Подтверждение заказа");
            String text = String.format("Здравствуйте, %s!\n\nВаш заказ №%d подтвержден.\n" +
                            "Изменение статуса: %s ---> %s.\n\n" +
                            "С уважением, команда разработчков лабораторной №3.",
                    message.getUsername(), message.getOrderId(), order.getStatus(), OrderStatus.ACCEPTED);
            msg.setText(text);
            javaMailSender.send(msg);
            log.info("Message to {} sent.", message.getEmail());
            order.setStatus(OrderStatus.ACCEPTED);
            orderRepository.save(order);
            return true;
        } catch (Exception ignored) {
            log.error("Sending message error");
            return false;
        }
    }

    public boolean changeStatus(String mes) {
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
            log.info("Message to {} sent.", message.getEmail());
            order.setStatus(message.getNewStatus());
            orderRepository.save(order);
            return true;
        } catch (Exception ignored) {
            log.error("Sending message error");
            return false;
        }
    }
}