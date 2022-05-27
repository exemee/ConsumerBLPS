package consumer.demo.message.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import consumer.demo.entities.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

@Data
public class ChangeOrderStatusMessage implements Serializable {
    @JsonProperty("username")
    private String username;
    @JsonProperty("orderId")
    private long orderId;
    @JsonProperty("email")
    private String email;
    @JsonProperty("newStatus")
    private OrderStatus newStatus;


    public ChangeOrderStatusMessage(String username, long orderId, String email, OrderStatus newStatus) {
        this.username = username;
        this.orderId = orderId;
        this.email = email;
        this.newStatus = newStatus;
    }
    public ChangeOrderStatusMessage(){

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChangeOrderStatusMessage)) return false;
        ChangeOrderStatusMessage that = (ChangeOrderStatusMessage) o;
        return getOrderId() == that.getOrderId() && getUsername().equals(that.getUsername()) && getEmail().equals(that.getEmail()) && getNewStatus() == that.getNewStatus();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUsername(), getOrderId(), getEmail(), getNewStatus());
    }
}
