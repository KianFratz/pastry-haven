package pastryhaven.finalproject.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import pastryhaven.finalproject.model.Customer;

@ControllerAdvice
public class GlobalModelAttributes {

    // This ensures the customer attribute is available in the session
    @ModelAttribute("customer")
    public Customer getCustomer() {
        return new Customer();
    }

    @ModelAttribute
    public void addUsernameToModel(HttpSession session, Model model) {
        Object uname = session.getAttribute("username");
        if (uname != null) {
            model.addAttribute("username", uname);
        }
    }
}
