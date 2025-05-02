package pastryhaven.finalproject.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pastryhaven.finalproject.model.CartItem;
import pastryhaven.finalproject.model.Customer;
import pastryhaven.finalproject.model.Product;
import pastryhaven.finalproject.repository.CartItemRepository;
import pastryhaven.finalproject.repository.CustomerRepository;
import pastryhaven.finalproject.repository.ProductsRepository;


import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    @Autowired
    private ProductsRepository productRepo;

    @Autowired
    private CartItemRepository cartItemRepository;

    public CartService(CartItemRepository cartItemRepo,
                       ProductsRepository productRepo) {
        this.cartItemRepository = cartItemRepo;
        this.productRepo = productRepo;
    }

    /** Add 1 (or qty) of the given product to the cart */
    @Transactional
    public void addToCart(Long productId, int qty) {
        Product product = productRepo.findById(productId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Invalid product ID: " + productId)
                );

        Optional<CartItem> existing = cartItemRepository.findByProductId(productId);
        if (existing.isPresent()) {
            CartItem item = existing.get();
            item.setQuantity(item.getQuantity() + qty);
            cartItemRepository.save(item);
        } else {
            CartItem newItem = new CartItem(product, qty);
            cartItemRepository.save(newItem);
        }
    }

    /** Return all items in the (single-table) cart */
    @Transactional
    public List<CartItem> listCartItems() {
        return cartItemRepository.findAll();
    }

    /** Delete a single cart item */
    @Transactional
    public void removeItem(Long itemId) {
        cartItemRepository.deleteById(itemId);
    }

    @Transactional
    public BigDecimal getCartTotal() {
        return cartItemRepository.findAll().stream()
                .map(item ->
                        item.getProduct().getPrice()
                                .multiply(BigDecimal.valueOf(item.getQuantity()))
                )
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
