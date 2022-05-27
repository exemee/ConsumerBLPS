package consumer.demo.repositories;

import consumer.demo.entities.Order;
import consumer.demo.entities.OrderProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderProductRepository extends JpaRepository<OrderProduct, Long> {
    public List<OrderProduct> findAllByKey_Order(Order order);
}
