package pastryhaven.finalproject.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pastryhaven.finalproject.model.Customer;
import pastryhaven.finalproject.model.Product;
import pastryhaven.finalproject.model.ProductDto;
import pastryhaven.finalproject.repository.ProductsRepository;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.*;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Controller
@SessionAttributes("customer")
@RequestMapping("/products")
public class ProductsController {

    @Autowired
    private ProductsRepository productsRepository;



    @GetMapping({"", "/"})
    public String showProductList(Model model, @ModelAttribute("customer") Customer customer,
                                  HttpSession session) {
        List<Product> products = productsRepository.findAll();
        model.addAttribute("products", products);

        // Store username for all future requests
//        session.setAttribute("username", customer.getFirstName());// for this view
//        model.addAttribute("username", customer.getFirstName());

        String username = customer.getFirstName();
        model.addAttribute("username", username);

        return "products/manage-pastries";
    }

    @GetMapping("/create")
    public String showCreatePage(Model model) {
        ProductDto productDto = new ProductDto();
        model.addAttribute("productDto", productDto);
        return "products/CreateProduct";
    }

    @PostMapping("/create")
    public String createProduct(@Valid @ModelAttribute ProductDto productDto,
                                BindingResult result) {

        if (productDto.getImageFile().isEmpty()) {
            result.addError(new FieldError("productDto", "imageFile", "The image file is required"));
        }

        if (result.hasErrors()) {
            return "products/CreateProduct";
        }

        // save image file
        MultipartFile image = productDto.getImageFile();
        String storageFileName = "_" + image.getOriginalFilename();

        try {
            String uploadDir = "public/images/";
            Path uploadPath = Paths.get(uploadDir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            try (InputStream inputStream = image.getInputStream()) {
                Files.copy(inputStream, Paths.get(uploadDir + storageFileName),
                        StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Exception ex) {
            System.out.println("Exception: " + ex.getMessage());
        }

        Product product = new Product();
        product.setName(productDto.getName());
        product.setImageFileName(storageFileName);
        product.setStock(productDto.getStock());
        product.setPrice(productDto.getPrice());
        product.setDescription(productDto.getDescription());

        productsRepository.save(product);

        return "redirect:/products";
    }

    @GetMapping("/edit")
    public String showEditPage(Model model, @RequestParam Long id) {

        try {
            Product product = productsRepository.findById(id).get();
            model.addAttribute("product", product);

            ProductDto productDto = new ProductDto();

            productDto.setName(product.getName());
            productDto.setStock(product.getStock());
            productDto.setPrice(product.getPrice());
            productDto.setDescription(product.getDescription());

            model.addAttribute("productDto", productDto);

        } catch (Exception ex) {
            System.out.println("Exception: " + ex.getMessage());
            return "redirect:/products";
        }

        return "products/EditProduct";

    }

    @PostMapping("/edit")
    public String updateProduct(Model model, @RequestParam Long id,
                                @Valid @ModelAttribute ProductDto productDto,
                                BindingResult result) {

        try {
            Product product = productsRepository.findById(id).get();
            model.addAttribute("product", product);

            if (result.hasErrors()) {
                return "products/EditProduct";
            }

            if (!productDto.getImageFile().isEmpty()) {
                // delete old image
                String uploadDir = "public/images/";
                Path oldImagePath = Paths.get(uploadDir + product.getImageFileName());

                try {
                    Files.delete(oldImagePath);
                } catch (Exception ex) {
                    System.out.println("Exception: " + ex.getMessage());
                }

                // save new image file
                MultipartFile image = productDto.getImageFile();
                String storageFileName = "_" + image.getOriginalFilename();

                try (InputStream inputStream = image.getInputStream()) {
                    Files.copy(inputStream, Paths.get(uploadDir + storageFileName),
                            StandardCopyOption.REPLACE_EXISTING);
                } catch (Exception ex) {
                    System.out.println("Exception: " + ex.getMessage());
                }

                product.setImageFileName(storageFileName);
            }

            product.setName(productDto.getName());
            product.setStock(productDto.getStock());
            product.setPrice(productDto.getPrice());
            product.setDescription(productDto.getDescription());

            productsRepository.save(product);

        } catch (Exception ex) {
            System.out.println("Exception: " + ex.getMessage());
        }

        return "redirect:/products";
    }

    @GetMapping("/delete")
    public String deleteProduct(@RequestParam Long id) {

        try {
            Product product = productsRepository.findById(id).get();

            // delete product image
            Path imagePath = Paths.get("public/images/" + product.getImageFileName());

            try {
                Files.delete(imagePath);
            } catch (Exception ex) {
                System.out.println("Exception: " + ex.getMessage());

            }

            // delete the product in repository
            productsRepository.delete(product);

        } catch (Exception ex) {
            System.out.println("Exception: " + ex.getMessage());
        }
        return "redirect:/products";
    }
}
