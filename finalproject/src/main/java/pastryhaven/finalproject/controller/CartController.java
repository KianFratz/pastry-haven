package pastryhaven.finalproject.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pastryhaven.finalproject.model.CartItem;
import pastryhaven.finalproject.model.Customer;
import pastryhaven.finalproject.model.Product;
import pastryhaven.finalproject.repository.CartItemRepository;
import pastryhaven.finalproject.repository.ProductsRepository;
import pastryhaven.finalproject.service.CartService;

import java.math.BigDecimal;
import java.util.List;

@Controller
@SessionAttributes("customer")
@RequestMapping("/user/cart")
public class CartController {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductsRepository productRepo;

    @Autowired
    private CartService cartService;

    // This ensures the customer attribute is available in the session
    @ModelAttribute("customer")
    public Customer getCustomer() {
        return new Customer();
    }

    // 1. List all items in the cart
    @GetMapping({"", "/"})
    public String listCartItems(Model model,
                                @ModelAttribute("customer") Customer customer) {

        // 1) load only this customer's items
        List<CartItem> items = cartItemRepository.findByCustomer(customer);
        model.addAttribute("items", items);

        BigDecimal total = cartService.getCartTotal();
        model.addAttribute("total", total);

        // Store username for all future requests
//        session.setAttribute("username", customer.getFirstName());// for this view
//        model.addAttribute("username", customer.getFirstName());

        // 3) username for the header
        String username = customer.getFirstName();
        model.addAttribute("username", username);

        // Option 2: If you don't have a cart ID but need to use item IDs for payment
        // This would work if you need to pay for all items at once
        if (!items.isEmpty()) {
            // Either pass the first item's ID or some combined ID depending on your requirements
            model.addAttribute("paymentItemId", items.get(0).getId());
        }

        return "/user/cart-list";           // Renders cart-list.html
    }

    @PostMapping("/add/{productId}")
    public String addToCart(@PathVariable Long productId,
                            @ModelAttribute("customer") Customer customer,
                            RedirectAttributes flash,
                            @RequestParam("quantity") int quantity) {

//        Product product = productRepo.findById(productId)
//                .orElseThrow(() -> new IllegalArgumentException("Invalid product ID: " + productId));

        // 2) Try to find an existing CartItem for THIS customer & THIS product
//        CartItem existing = cartItemRepository
//                .findByCustomerIdAndProductId(customer.getId(), productId)
//                .orElse(null);

//        if (existing != null) {
//            existing.setQuantity(existing.getQuantity() + 1);
//            cartItemRepository.save(existing);
//        } else {
//            CartItem newItem = new CartItem();
//            newItem.setProduct(product);
//            newItem.setCustomer(customer);
//            newItem.setQuantity(1);
//            cartItemRepository.save(newItem);
//            cartService.addToCart(customer.getId(), productId, 1);
//
//        }

        try {
            cartService.addToCart(productId, customer.getId(), quantity);
        } catch (IllegalArgumentException ex) {
            // log if you want: log.warn("Bad product id {}", productId);
            flash.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/user/products";   // or wherever makes sense
        }

        return "redirect:/user/cart";

    }

    // 3. Show form to edit an existing cart item
    @GetMapping("/update/{id}")
    public String showUpdateForm(@PathVariable Long id, Model model) {
        CartItem item = cartItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid cart item ID: " + id));
        model.addAttribute("item", item);
        return "/user/cart-update";
    }

    // 4. Save changes from the edit form (or handle new items via ModelAttribute)
    @PostMapping("/save")
    public String saveCartItem(@RequestParam("id") Long id,
                               @RequestParam("quantity") Integer quantity,
                               RedirectAttributes flash) {

        // 1. Load the existing CartItem (with its Product already set)
        CartItem item = cartItemRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Invalid cart item ID: " + id)
                );

        Product product = item.getProduct();
        int oldQty = item.getQuantity();  // original quantity in the cart

        // 2. If quantity is less than 1, delete the item and restore all qty to stock
        if (quantity == null || quantity < 1) {
            product.setStock(product.getStock() + oldQty); // return all to stock
            productRepo.save(product);
            cartItemRepository.delete(item);
        } else {
            // 3. If user decreased quantity, return the difference to stock
            if (quantity < oldQty) {
                int diff = oldQty - quantity;
                product.setStock(product.getStock() + diff);
            }

            // 4. If user increased quantity, check for stock availability
            if (quantity > oldQty) {
                int diff = quantity - oldQty;
                if (product.getStock() < diff) {
                    flash.addFlashAttribute("errorMessage", "Not enough stock available for " + product.getName());
                    return "redirect:/user/cart";
                }
                product.setStock(product.getStock() - diff);
            }

            // Save updated cart and product
            item.setQuantity(quantity);
            cartItemRepository.save(item);
            productRepo.save(product);

        }

        return "redirect:/user/cart";

//        // If quantity ≤ 0, delete instead of saving
//        if (item.getQuantity() == 0 || item.getQuantity() < 1) {
//            cartItemRepository.deleteById(item.getId());
//        } else {
//            // 1. Load the Product entity
//            Product product = productRepo.findById(productId)
//                    .orElseThrow(() -> new IllegalArgumentException("Invalid product ID: " + productId));
//
//            // 2. Ensure we have the existing CartItem (so Hibernate does UPDATE, not INSERT)
//            CartItem existing = cartItemRepository.findById(item.getId())
//                    .orElseThrow(() -> new IllegalArgumentException("Invalid cart item ID: " + item.getId()));
//            // 3. Update only the quantity (and leave product untouched)
//            existing.setQuantity(item.getQuantity());
//
//            cartItemRepository.save(item);
//        }
//        return "redirect:/user/cart";
    }

    // 5. Delete a cart item
    @GetMapping("/delete/{id}")
    public String deleteCartItem(@PathVariable Long id) {

        // 1. Load the cart item (with product info)
        CartItem item = cartItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid cart item ID: " + id));

        Product product = item.getProduct();

        // 2. Return the quantity back to stock
        int quantity = item.getQuantity();
        product.setStock(product.getStock() + quantity);
        productRepo.save(product);


        cartItemRepository.deleteById(id);
        return "redirect:/user/cart";
    }


}
