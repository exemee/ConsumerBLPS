package consumer.demo.repositories;

import consumer.demo.entities.Category;
import consumer.demo.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findAllByCategory(Category category);
    List<Product> findAllByPriceBetween(float min, float max);
}
