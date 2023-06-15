package io.cartin.backend.Order;


import io.cartin.backend.CartinUser.CartinUser;
import io.cartin.backend.Product.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(
        name = "cartin_orders"
)
@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(
            name = "transaction_id",
            nullable = false
    )
    private Long orderTransactionId;

    @ManyToOne
    @JoinColumn(
            name = "product_id",
            nullable = false
    )
    private Product product;


    @Column(
            name = "quantity",
            nullable = false
    )
    private Integer quantity;

    @Column(
            name = "order_date"
    )
    private LocalDate orderDate;

    @ManyToOne
    @JoinColumn(
            name = "user_id"
    )
    private CartinUser customer;

}
