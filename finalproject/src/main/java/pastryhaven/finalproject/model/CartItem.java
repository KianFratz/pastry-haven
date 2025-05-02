package pastryhaven.finalproject.model;

import jakarta.persistence.*;

import java.util.Optional;

@Entity
@Table(name = "cart_items")
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    // Constructors
    public CartItem() {}
    public CartItem(Product product, Integer quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    // Getters & setters
    public Long getId() { return id; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}
