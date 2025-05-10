package pastryhaven.finalproject.model;


import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "payment_date", nullable = false)
    private LocalDateTime paymentDate;

    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    @Column(name = "payment_method", nullable = false)
    private String paymentMethod;

    @OneToMany(mappedBy = "payment",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<PaymentItem> paymentItems = new ArrayList<>();



    // Constructors
    public Payment() {
        this.paymentDate = LocalDateTime.now();
    }

    public Payment(BigDecimal totalAmount, String paymentMethod) {
        this.paymentDate = LocalDateTime.now();
        this.totalAmount = totalAmount;
        this.paymentMethod = paymentMethod;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public List<PaymentItem> getPaymentItems() {
        return paymentItems;
    }

    public void setPaymentItems(List<PaymentItem> paymentItems) {
        this.paymentItems = paymentItems;
    }
    public void addPaymentItem(PaymentItem item) {
        paymentItems.add(item);
        item.setPayment(this);
    }

}
