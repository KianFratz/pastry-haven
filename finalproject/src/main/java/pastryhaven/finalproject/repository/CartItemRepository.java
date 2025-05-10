package pastryhaven.finalproject.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import pastryhaven.finalproject.model.CartItem;
import pastryhaven.finalproject.model.Customer;
import pastryhaven.finalproject.model.Payment;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByCustomer(Customer customer);
    List<CartItem> findByDeletedFalse();
    Optional<CartItem> findByCustomerIdAndProductId(Long customerId, Long productId);

    @Transactional
    void deleteByCustomer(Customer customer);

}

