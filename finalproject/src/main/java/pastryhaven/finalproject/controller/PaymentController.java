package pastryhaven.finalproject.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pastryhaven.finalproject.model.CartItem;
import pastryhaven.finalproject.model.Payment;
import pastryhaven.finalproject.repository.CartItemRepository;
import pastryhaven.finalproject.service.CartService;
import pastryhaven.finalproject.service.PaymentService;
import pastryhaven.finalproject.service.PaymongoService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/user/cart/payment")
public class PaymentController {

    @Autowired
    private PaymongoService paymongoService;

    @Autowired
    private CartService cartService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private CartItemRepository cartItemRepository;

    @PostMapping("/pay/{id}")
    public String pay(@PathVariable Long id) {
        // Get cart information
        Optional<CartItem> cartOptional = cartItemRepository.findById(id);
        if (!cartOptional.isPresent()) {
            // Handle the case where the cart item is not found
            return "redirect:/user/cart"; // or an appropriate error page
        }
        CartItem cart = cartOptional.get();

        BigDecimal totalAmount = cartService.getCartTotal();

        // Generate a unique reference number
        String reference = "ORDER-" + UUID.randomUUID().toString().substring(0, 8);

        // Create a payment link with Paymongo
        Map<String, Object> paymongoResponse = paymongoService.createPaymentLink(
                totalAmount,
                "PastryHaven Order",
                reference
        );

        // Extract payment info from response
        Map<String, Object> data = (Map<String, Object>) paymongoResponse.get("data");
        String paymentId = (String) data.get("id");
        Map<String, Object> attributes = (Map<String, Object>) data.get("attributes");
        String checkoutUrl = (String) attributes.get("checkout_url");
        String referenceNumber = (String) attributes.get("reference_number");
        String status = (String) attributes.get("status");

        // Create and save payment record to database
        Payment payment = new Payment();
        payment.setCart(cart);
        payment.setAmount(totalAmount);
        payment.setPaymentId(paymentId);
        payment.setReferenceNumber(referenceNumber);
        payment.setStatus(status);
        payment.setCheckoutUrl(checkoutUrl);
        payment.setCreatedAt(LocalDateTime.now());

        // Save to database
        paymentService.savePayment(payment);

        // Redirect to the payment URL
        return "redirect:" + checkoutUrl;
    }


}
