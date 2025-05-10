package pastryhaven.finalproject.controller;


import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import pastryhaven.finalproject.model.Customer;
import pastryhaven.finalproject.model.Product;
import pastryhaven.finalproject.repository.ProductsUserRepository;

import java.util.List;

@Controller
@SessionAttributes("customer")
@RequestMapping("/user")
public class ProductsUserController {

    @Autowired
    private ProductsUserRepository productsUserRepository;

    // This ensures the customer attribute is available in the session
    @ModelAttribute("customer")
    public Customer getCustomer() {
        return new Customer();
    }

    @GetMapping("/products")
    public String showProductList(Model model, Customer customer,
                                  HttpSession session) {
        List<Product> products = productsUserRepository.findAll();
        model.addAttribute("products", products);

        // Store username for all future requests
//        session.setAttribute("username", customer.getFirstName());// for this view
//        model.addAttribute("username", customer.getFirstName());

        String username = customer.getFirstName();
        model.addAttribute("username", username);

        return "user/product";
    }
}
