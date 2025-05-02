package pastryhaven.finalproject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pastryhaven.finalproject.model.CartItem;
import pastryhaven.finalproject.model.Product;
import pastryhaven.finalproject.repository.CartItemRepository;
import pastryhaven.finalproject.repository.ProductsRepository;
import pastryhaven.finalproject.service.CartService;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/user/cart")
public class CartController {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductsRepository productRepo;

    @Autowired
    private CartService cartService;


    // 1. List all items in the cart
    @GetMapping({"", "/"})
    public String listCartItems(Model model) {
        List<CartItem> items = cartItemRepository.findAll();
        model.addAttribute("items", items);

        BigDecimal total = cartService.getCartTotal();
        model.addAttribute("total", total);

        // Option 2: If you don't have a cart ID but need to use item IDs for payment
        // This would work if you need to pay for all items at once
        if (!items.isEmpty()) {
            // Either pass the first item's ID or some combined ID depending on your requirements
            model.addAttribute("paymentItemId", items.get(0).getId());
        }

        return "user/cart-list";           // Renders cart-list.html
    }

    @PostMapping("/add/{productId}")
    public String addToCart(@PathVariable Long productId) {
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product ID: " + productId));

        // Check if all the item already exists -> increment quantity
        CartItem existing = cartItemRepository.findByProductId(productId).orElse(null);
        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + 1);
            cartItemRepository.save(existing);
        } else {
            CartItem newItem = new CartItem();
            newItem.setProduct(product);
            newItem.setQuantity(1);
            cartItemRepository.save(newItem);
        }
        return "redirect:/user/cart";

    }

    // 3. Show form to edit an existing cart item
    @GetMapping("/update/{id}")
    public String showUpdateForm(@PathVariable Long id, Model model) {
        CartItem item = cartItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid cart item ID: " + id));
        model.addAttribute("item", item);
        return "user/cart-update";
    }

    // 4. Save changes from the edit form (or handle new items via ModelAttribute)
    @PostMapping("/save")
    public String saveCartItem(@RequestParam("id") Long id,
                               @RequestParam("quantity") Integer quantity ) {

        // 1. Load the existing CartItem (with its Product already set)
        CartItem item = cartItemRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Invalid cart item ID: " + id)
                );

        // 2. If quantity is less than 1, delete; otherwise update
        if (quantity == null || quantity < 1) {
            cartItemRepository.delete(item);
        } else {
            item.setQuantity(quantity);
            cartItemRepository.save(item);
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
        cartItemRepository.deleteById(id);
        return "redirect:/user/cart";
    }


}
