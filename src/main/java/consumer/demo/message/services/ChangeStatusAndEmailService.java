package consumer.demo.message.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import consumer.demo.entities.*;
import consumer.demo.message.model.ChangeOrderStatusMessage;
import consumer.demo.repositories.OrderProductRepository;
import consumer.demo.repositories.OrderRepository;
import consumer.demo.repositories.StorageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

@Slf4j
@Service
public class ChangeStatusAndEmailService {
    private final JavaMailSender javaMailSender;
    private final OrderRepository orderRepository;
    private final ObjectMapper objectMapper;
    private final TransactionTemplate transactionTemplate;
    private final OrderProductRepository orderProductRepository;
    private final StorageRepository storageRepository;


    @Autowired
    public ChangeStatusAndEmailService(JavaMailSender javaMailSender,
                                       OrderRepository orderRepository,
                                       TransactionTemplate transactionTemplate,
                                       OrderProductRepository orderProductRepository,
                                       StorageRepository storageRepository) {
        this.javaMailSender = javaMailSender;
        this.objectMapper = new ObjectMapper();
        this.orderRepository = orderRepository;
        this.transactionTemplate = transactionTemplate;
        this.orderProductRepository = orderProductRepository;
        this.storageRepository = storageRepository;
    }


    public boolean productsAppeared(ChangeOrderStatusMessage mes) {
        try {
            Order order = orderRepository.findById(mes.getOrderId()).orElseThrow(() -> new IllegalArgumentException("Wrong id"));
            transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                @Override
                public void doInTransactionWithoutResult(TransactionStatus status) {
                    List<OrderProduct> products = orderProductRepository.findAllByKey_Order(order);
                    for (OrderProduct p : products) {
                        Product prod = p.getKey().getProduct();
                        Storage stor = storageRepository.findByProduct(prod).orElseThrow(() -> new RuntimeException("Wrong storage"));
                        stor.setCount(stor.getCount() - p.getCount());
                        storageRepository.save(stor);
                    }
                    order.setStatus(OrderStatus.ACCEPTED);
                    orderRepository.save(order);
                }
            });
            if(order.getStatus()==OrderStatus.ACCEPTED) {
                log.info("order id {} working", mes.getOrderId());
                SimpleMailMessage msg = new SimpleMailMessage();
                msg.setTo(mes.getEmail());
                msg.setSubject("Подтверждение заказа");
                String text = String.format("Здравствуйте, %s!\n\nВаш заказ № %d подтвержден.\n" +
                                "Изменение статуса: %s ---> %s.\n\n" +
                                "С уважением, команда разработчков лабораторной №3.",
                        mes.getUsername(), mes.getOrderId(), order.getStatus().name(), OrderStatus.ACCEPTED.name());
                msg.setText(text);
                javaMailSender.send(msg);
                log.info("Message to {} sent.", mes.getEmail());
                return true;
            }
            else{
                return false;
            }
        } catch (Exception ignored) {
            log.error("Sending message error");
            return false;
        }
    }

    public boolean changeStatus(ChangeOrderStatusMessage mes) {
        try {
            log.info("try start " + mes);
            //  ChangeOrderStatusMessage message = objectMapper.readValue(mes, ChangeOrderStatusMessage.class);
            log.info("order id {} working", mes.getOrderId());
            Order order = orderRepository.findById(mes.getOrderId()).orElseThrow(() -> new IllegalArgumentException("Wrong id"));
            OrderStatus oldStatus = order.getStatus();
            if(mes.getNewStatus()==OrderStatus.ACCEPTED) {
                transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                    @Override
                    public void doInTransactionWithoutResult(TransactionStatus status) {
                        List<OrderProduct> products = orderProductRepository.findAllByKey_Order(order);
                        if (order.getStatus() == OrderStatus.WAITING) {
                            for (OrderProduct p : products) {
                                Storage stor = storageRepository.findById(p.getKey().getProduct().getId()).orElseThrow(() -> new IllegalArgumentException("Wrong id"));
                                if (stor.getCount() < p.getCount()) {
                                    order.setStatus(OrderStatus.NO_PRODUCTS);
                                    orderRepository.save(order);
                                    return;
                                }
                            }
                            for (OrderProduct p : products) {
                                Storage stor = storageRepository.findById(p.getKey().getProduct().getId()).orElseThrow(() -> new IllegalArgumentException("Wrong id"));
                                stor.setCount(stor.getCount() - p.getCount());
                            }
                            order.setStatus(OrderStatus.ACCEPTED);
                            orderRepository.save(order);
                        } else {
                            throw new RuntimeException("Wrong status");
                        }
                    }
                });
            }
            else{
                if(order.getStatus()==OrderStatus.ACCEPTED){
                    order.setStatus(OrderStatus.SENT);
                    orderRepository.save(order);
                }
                else{
                    throw new RuntimeException("Wrong status");
                }
            }
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(mes.getEmail());
            msg.setSubject("Изменение статуса заказа");
            String text = String.format("Здравствуйте, %s!\n\nВаш заказ № %d изменил статус.\n" +
                            "%s ---> %s.\n\n" +
                            "С уважением, команда разработчков лабораторной №3.",
                    mes.getUsername(), mes.getOrderId(), oldStatus.name(), order.getStatus().name());
            msg.setText(text);
            javaMailSender.send(msg);
            return true;
        } catch (Exception ignored) {
            log.error("Sending message error");
            ignored.printStackTrace();
            return false;
        }
    }
}
