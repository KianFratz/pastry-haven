package pastryhaven.finalproject.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "payment_items")
public class PaymentItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "product_price", nullable = false)
    private BigDecimal productPrice;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "subtotal", nullable = false)
    private BigDecimal subtotal;

    @ManyToOne(optional = false)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;


    // Constructors
    public PaymentItem() {}

    public PaymentItem(String productName, BigDecimal productPrice, Integer quantity) {
        this.productName = productName;
        this.productPrice = productPrice;
        this.quantity = quantity;
        this.subtotal = productPrice.multiply(new BigDecimal(quantity));
    }

    public PaymentItem(String name, BigDecimal price, Integer quantity, Long customerId, Object o) {
    }

    public PaymentItem(String name, BigDecimal price, Integer quantity, Long customerId) {
    }

    // add getters/setters for `payment`:
    public Payment getPayment() {
        return payment;
    }
    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public BigDecimal getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(BigDecimal productPrice) {
        this.productPrice = productPrice;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
}
