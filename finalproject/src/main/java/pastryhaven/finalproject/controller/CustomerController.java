package pastryhaven.finalproject.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import org.springframework.web.bind.support.SessionStatus;
import pastryhaven.finalproject.model.Customer;
import pastryhaven.finalproject.repository.CustomerRepository;


@Controller
@SessionAttributes("customer")
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    private CustomerRepository repository;

    // This ensures the customer attribute is available in the session
    @ModelAttribute("customer")
    public Customer getCustomer() {
        return new Customer();
    }


    @GetMapping({""})
    public String listCustomer(Model model) {
        model.addAttribute("customer", repository.findAll());
        return "customer-list";
    }
    
    @GetMapping("/aboutus")
    public String aboutUspage(@ModelAttribute("customer") Customer customer, Model model,
                              HttpSession session) {

        // Store username for all future requests
//        session.setAttribute("username", customer.getFirstName());// for this view
//        model.addAttribute("username", customer.getFirstName());

        String username = customer.getFirstName();
        model.addAttribute("username", username);

        return "about-us";

    }
    
    @PostMapping("/saveCustomer")
    public String saveCustomer(@ModelAttribute Customer customer) {
        // Check if customer is being created or updated
        if (customer.getId() == null) { // New customer
            // If no password is provided, set a default password
            if (customer.getPassword() == null || customer.getPassword().isEmpty()) {
                customer.setPassword("defaultPassword123");  // Set default password
            }
        } else { // Existing customer, update
            // If no new password is provided, keep the existing password
            Customer existingCustomer = repository.findById(customer.getId())
                                                  .orElseThrow(() -> new IllegalArgumentException("Invalid Customer ID: " + customer.getId()));
            if (customer.getPassword() == null || customer.getPassword().isEmpty()) {
                customer.setPassword(existingCustomer.getPassword()); // Keep old password
            }
        }
        
        // Save the customer entity
        repository.save(customer);

        return "redirect:/customer";
    }


    
    // Show edit form
    @GetMapping("/update/{id}")
    public String showUpdateForm(@PathVariable Long id, Model model) {
        Customer customer = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid Customer ID: " + id));
        model.addAttribute("customer", customer);
        return "customer-update";  // Renders customer-update.html
    }
    
    @GetMapping("/delete/{id}")
    public String deleteCustomer(@PathVariable Long id) {
        repository.deleteById(id);
        return "redirect:/customer";  // Redirect ensures page refreshes
    }

    @GetMapping("/logout")
    public String logout(SessionStatus status) {
        status.setComplete(); // Clear the session
        return "redirect:/login";
    }
}

