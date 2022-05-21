package consumer.demo.repositories;

import consumer.demo.entities.Order;
import consumer.demo.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    public List<Order> findAllByUser(User user);
}