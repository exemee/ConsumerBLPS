package consumer.demo.entities;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Min;

@Data
@Entity
@Table(name="storage")
public class Storage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn (name = "product_id", referencedColumnName = "product_id")
    private Product product;

    @Min(0)
    @Column(name="count")
    private long count;
}
