package pastryhaven.finalproject.model;


import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

public class ProductDto {

    @NotBlank(message = "Name is required")
    private String name;

    @Min(value = 0, message = "Stock cannot be negative")
    private int stock = 0;

    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than zero")
    private BigDecimal price;

    @NotBlank(message = "Description is required")
    private String description;

    private MultipartFile imageFile;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public MultipartFile getImageFile() {
        return imageFile;
    }

    public void setImageFile(MultipartFile imageFile) {
        this.imageFile = imageFile;
    }
}
