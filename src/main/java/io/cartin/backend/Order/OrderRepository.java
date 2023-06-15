package io.cartin.backend.Order;

import io.cartin.backend.CartinUser.CartinUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByCustomer(CartinUser user);

    List<Order> findByCustomerAndOrderDate(CartinUser user, LocalDate date);

    List<Order> findByCustomerAndOrderDateBetween(CartinUser user, LocalDate startDate, LocalDate endDate);

}
