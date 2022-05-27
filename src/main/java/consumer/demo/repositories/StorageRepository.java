package consumer.demo.repositories;

import consumer.demo.entities.Product;
import consumer.demo.entities.Storage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StorageRepository extends JpaRepository<Storage, Long> {
    Optional<Storage> findByProduct(Product product);
}
