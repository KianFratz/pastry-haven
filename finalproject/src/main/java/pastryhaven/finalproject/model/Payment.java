package pastryhaven.finalproject.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "paymongo_id")
    private Long paymongo_id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CART_ITEM_ID", nullable = false)
    private CartItem cart;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "payment_id")
    private String payment_id; // Paymongo payment ID

    @Column(name = "reference_number")
    private String referenceNumber;

    @Column(name = "status")
    private String status;

    @Column(name = "checkout_url")
    private String checkoutUrl;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Long getId() {
        return paymongo_id;
    }

    public void setId(Long id) {
        this.paymongo_id = id;
    }

    public CartItem getCart() {
        return cart;
    }

    public void setCart(CartItem cart) {
        this.cart = cart;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getPaymentId() {
        return payment_id;
    }

    public void setPaymentId(String paymentId) {
        this.payment_id = paymentId;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCheckoutUrl() {
        return checkoutUrl;
    }

    public void setCheckoutUrl(String checkoutUrl) {
        this.checkoutUrl = checkoutUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
