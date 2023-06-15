package io.cartin.backend.CartinUser;


import io.cartin.backend.Cart.CartItem;
import io.cartin.backend.CartinUser.Admin.Admin;
import io.cartin.backend.CartinUser.Customer.Customer;
import io.cartin.backend.CartinUser.Manager.Manager;
import io.cartin.backend.Order.Order;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "cartin_users", uniqueConstraints = {
        @UniqueConstraint(name = "unique_email", columnNames = {"email"}),
})
public class CartinUser {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(
                name = "user_id",
                nullable = false,
                updatable = false
        )
        private Long userId;

        @Column(
                name = "password",
                nullable = false
        )
        private String password;

        @Column(
                name = "login_status",
                nullable = false
        )
        private boolean loginStatus;

        @Column(
                name = "first_name",
                nullable = false
        )
        private String firstName;

        @Column(
                name = "middle_name"
        )
        private String middleName;

        @Column(
                name = "last_name",
                nullable = false
        )
        private String lastName;

        @Column(
                name = "dob",
                nullable = false
        )
        private LocalDate dob;

        @Column(
                name = "email",
                nullable = false
        )
        private String email;

        @Column(
                name = "phone_number",
                nullable = false
        )
        private String phoneNumber;

        @Column(
                name = "address",
                nullable = false
        )
        private String address;

        @Column(
                name = "role",
                nullable = false
        )
        @Enumerated(EnumType.STRING)
        private Role role;


        @Builder.Default
        private boolean enabled = false;

        @Embedded
        private Customer customer;

        @Embedded
        private Manager manager;

        @Embedded
        private Admin admin;

        // one-to-many relationship with cart
        @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
        @JsonIgnore
        private List<CartItem> cartItems;

        // one-to-many relationship with orders
        @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
        @JsonIgnore
        private List<Order> orders;
}
