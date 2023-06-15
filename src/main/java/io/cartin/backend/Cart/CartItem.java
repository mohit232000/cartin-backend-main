package io.cartin.backend.Cart;

import io.cartin.backend.CartinUser.CartinUser;
import io.cartin.backend.Product.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;

@AllArgsConstructor
@RequiredArgsConstructor
@Builder
@Data
@Entity
@Table(
        name = "cartin_cart_items"
)

public class CartItem {
    @Id
    @SequenceGenerator(
            name="cart_sequence",
            sequenceName = "cart_sequence",
            allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cart_sequence")
    @Column(
            name = "entry_id"
    )
    private Long entryId;

    @ManyToOne
    @JoinColumn(
            name = "user_id"
    )
    private CartinUser customer;


    @ManyToOne
    @JoinColumn(
            name = "product_id"
    )
    private Product product;

    @Column(
            name = "quantity",
            nullable = false
    )
    private Integer quantity;

}
