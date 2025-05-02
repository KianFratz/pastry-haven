package pastryhaven.finalproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pastryhaven.finalproject.model.CartItem;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByProductId(Long productId);
}

