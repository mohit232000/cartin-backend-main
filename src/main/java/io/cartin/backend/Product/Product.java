package io.cartin.backend.Product;

import io.cartin.backend.Cart.CartItem;
import io.cartin.backend.Order.Order;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(
        name = "cartin_products",
        indexes = {
                @Index(name = "search_idx", columnList = "product_name"),
                @Index(name = "", columnList = "product_description")
        }
)
@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class Product {
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    @Column(
            name = "product_id",
            nullable = false
    )
    private Long productId;

    @Column(
            name = "product_name",
            nullable = false
    )
    private String name;

    @Column(
            name = "product_description"
    )
    private String description;

    @Column(
            name = "product_price",
            nullable = false
    )
    private double price;

    @Column(
            name = "product_quantity",
            nullable = false
    )
    private Integer quantity;


    @Column(
            name = "product_image",
            columnDefinition = "TEXT"
    )
//     @Lob
        private String image; 

    @Column(
            name = "product_category",
            nullable = false
    )
    private Category category;

    @Column(
            name = "offer"
    )
    private Double offer;

    @Column(
        name = "delivery_time",
        nullable = false
    )
    // in days
    private Integer deliveryTime;

    // one-to-many relationship with cart
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<CartItem> cartItems;

    // one-to-many relationship with orders
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Order> orders;



}
