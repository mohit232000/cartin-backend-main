package io.cartin.backend.Cart;

import io.cartin.backend.CartinUser.CartinUser;
import io.cartin.backend.Product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<CartItem, Long> {

    public List<CartItem> findByCustomer(CartinUser user);
    public Optional<CartItem> findByCustomerAndProduct(CartinUser user, Product product);
    
}
