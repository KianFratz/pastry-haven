package pastryhaven.finalproject.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pastryhaven.finalproject.model.CartItem;
import pastryhaven.finalproject.model.Customer;
import pastryhaven.finalproject.model.Payment;
import pastryhaven.finalproject.repository.CartItemRepository;
import pastryhaven.finalproject.service.CartService;
import pastryhaven.finalproject.service.CustomerService;

import java.math.BigDecimal;
import java.util.List;

@Controller
@SessionAttributes("customer")
@RequestMapping("/checkout")
public class PaymentController {

    @Autowired
    private CartService cartService;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private CustomerService customerService;

    // This ensures the customer attribute is available in the session
    @ModelAttribute("customer")
    public Customer getCustomer() {
        return new Customer();
    }

    @GetMapping({"", "/"})
    public String showCheckoutPage(Model model) {
        List<CartItem> cartItems = cartService.getAllCartItems();
        BigDecimal total = cartService.calculateCartTotal();

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("total", total);

        return "checkout/checkout";
    }

    @PostMapping("/process")
    public String processPayment(@RequestParam("paymentMethod") String paymentMethod,
                                 RedirectAttributes redirectAttributes,
                                 @ModelAttribute("customer") Customer customer) {

            try {
            Payment payment = cartService.processPayment(paymentMethod, customer);

            redirectAttributes.addFlashAttribute("successMessage",
                    "Payment successful! Your order confirmation number is: " +
                            payment.getId());

            // clear only this customer’s cart
            cartItemRepository.deleteByCustomer(customer);

            return "redirect:/checkout/confirmation/" + payment.getId();
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            System.out.println(e.getMessage());
            return "redirect:/checkout";
        }
    }

    @GetMapping("/confirmation/{paymentId}")
    public String showConfirmation(@PathVariable Long paymentId, Model model) {
        // In a real application, you would verify this payment belongs to the current user
        Payment payment = cartService.getAllPayments().stream()
                .filter(p -> p.getId().equals(paymentId))
                .findFirst()
                .orElse(null);

        if (payment == null) {
            return "redirect:/checkout";
        }

        model.addAttribute("payment", payment);
        return "user/confirmation";
    }

    @GetMapping("/history")
    public String showPaymentHistory(Model model, @ModelAttribute("customer") Customer customer, HttpSession session) {
        // Get only the payments for this specific customer
        List<Payment> payments = cartService.getPaymentsByCustomer(customer);        model.addAttribute("payments", payments);
        model.addAttribute("payments", payments);

        // Store username for all future requests
//        session.setAttribute("username", customer.getFirstName());// for this view
//        model.addAttribute("username", customer.getFirstName());

        String username = customer.getFirstName();
        model.addAttribute("username", username);

        return "user/payment-history";
    }
}
