package io.cartin.backend.CartinUser;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartinUserRepository extends JpaRepository<CartinUser, Long> {
    List<CartinUser> findByManagerIsNotNull();
    List<CartinUser> findByRole(Role role);
    Optional<CartinUser> findByEmail(String email);
}
