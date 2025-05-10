package pastryhaven.finalproject.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pastryhaven.finalproject.model.*;
import pastryhaven.finalproject.repository.CartItemRepository;
import pastryhaven.finalproject.repository.CustomerRepository;
import pastryhaven.finalproject.repository.PaymentRepository;
import pastryhaven.finalproject.repository.ProductsRepository;


import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    @Autowired
    private ProductsRepository productRepo;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private CustomerRepository customerRepository;


    public CartService(CartItemRepository cartItemRepo,
                       ProductsRepository productRepo,
                       PaymentRepository paymentRepository) {
        this.cartItemRepository = cartItemRepo;
        this.productRepo = productRepo;
        this.paymentRepository = paymentRepository;

    }

    /** Add 1 (or qty) of the given product to the cart */
    @Transactional
    public void addToCart(Long productId, Long customerId, int qty) {

        // 1) Load the product
        Product product = productRepo.findById(productId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Invalid product ID: " + productId)
                );

        // Check if there's enough stock
        if (product.getStock() < qty) {
            throw new IllegalArgumentException("Not enough stock for product: " + product.getName());
        }


        // 2) Load the customer (so we can link new items)
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Invalid customer ID: " + customerId)
                );

        // 3) Try to find an existing CartItem for this customer & product
        Optional<CartItem> existing = cartItemRepository
                .findByCustomerIdAndProductId(customerId, productId);


        if (existing.isPresent()) {
            CartItem item = existing.get();
            item.setQuantity(item.getQuantity() + qty);
            cartItemRepository.save(item);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCustomer(customer);
            newItem.setProduct(product);
            newItem.setQuantity(qty);
            cartItemRepository.save(newItem);
        }

        // 4) Subtract the quantity from product stock
        product.setStock(product.getStock() - qty);
        productRepo.save(product); // Save the updated stock
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

    @Transactional
    public Payment processPayment(String paymentMethod, Customer customer) {
        List<CartItem> items = cartItemRepository.findByCustomer(customer);

        if (items.isEmpty()) {
            throw new IllegalStateException("Cannot process payment for an empty cart");
        }

        BigDecimal total = calculateCartTotal();
        Payment payment = new Payment(total, paymentMethod);
        payment.setCustomer(customer);

        // Create payment items from cart items
        for (CartItem cartItem : items) {
            PaymentItem paymentItem = new PaymentItem(
                    cartItem.getProduct().getName(),
                    cartItem.getProduct().getPrice(),
                    cartItem.getQuantity()
            );
            payment.addPaymentItem(paymentItem);

            // Mark cart item as deleted
            cartItem.setDeleted(true);
            cartItemRepository.save(cartItem);
        }

        // Save the payment
        return paymentRepository.save(payment);
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAllByOrderByPaymentDateDesc();
    }
    public List<CartItem> getAllCartItems() {
        return cartItemRepository.findByDeletedFalse();
    }

    public BigDecimal calculateCartTotal() {
        List<CartItem> cartItems = getAllCartItems();
        return cartItems.stream()
                .map(item -> item.getProduct().getPrice().multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public List<Payment> getPaymentsByCustomer(Customer customer) {
        return paymentRepository.findByCustomer(customer);
    }


}
