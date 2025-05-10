package pastryhaven.finalproject.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import pastryhaven.finalproject.model.Customer;
import pastryhaven.finalproject.model.CustomerDto;
import pastryhaven.finalproject.model.Product;
import pastryhaven.finalproject.repository.CustomerRepository;
import pastryhaven.finalproject.repository.ProductsRepository;
import pastryhaven.finalproject.service.CustomerService;

import java.util.List;

@Controller
@SessionAttributes("customer")
public class AuthController {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private ProductsRepository productsRepository;

    // This ensures the customer attribute is available in the session
    @ModelAttribute("customer")
    public Customer getCustomer() {
        return new Customer();
    }

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("customer", new Customer()); // Add empty customer
        return "login";
    }

    @PostMapping("/login")
    public String loginUser(@ModelAttribute("customer") Customer customer,
                            Model model, HttpSession session) {

        try {
            customer = customerService.authenticateCustomer(
                    customer.getEmailAddress(), customer.getPassword()

            );
            List<Product> products = productsRepository.findAll();
            model.addAttribute("products", products);

            model.addAttribute("customer", customer);  // now auto-saved to session

            // Store username for all future requests
//            session.setAttribute("username", customer.getFirstName());// for this view
//            model.addAttribute("username", customer.getFirstName());

            String username = customer.getFirstName();
            model.addAttribute("username", username);

            return "user/product";

        } catch (Exception ex) {
            model.addAttribute("emailError", "Invalid email or password");
            model.addAttribute("passwordError", "Invalid email or password");
            return "login";

        }
    }

    @GetMapping("/register")
    public String showRegistrationFrom(Model model) {
        CustomerDto customerDto = new CustomerDto();
        model.addAttribute("customerDto", customerDto);
        return "register";
    }

    @PostMapping("/register")
    public String registerAccount(@Valid @ModelAttribute("customerDto") CustomerDto customerDto,
                                  BindingResult result, Model model) {

        if (!customerDto.isPasswordMatching()) {
            result.rejectValue("confirmPassword", "error.confirmPassword", "Passwords do not match");
        }

        if (result.hasErrors()) {
            return "register";
        }

        try {
            // 2. Check for duplicate email
            if (customerService.isEmailRegistered(customerDto.getEmailAddress())) {
                // Attach an error to the 'emailAddress' field
                result.rejectValue(
                        "emailAddress",
                        "error.customerDto",
                        "An account already exists for this email.");
                return "register";
            }



        } catch (Exception ex) {
            System.out.println("Exception: " + ex.getMessage());
        }

        // 3. No errors: proceed to create the account
        customerService.registerNewCustomer(customerDto);
        return "redirect:/login";

    }
	
}
