package pastryhaven.finalproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pastryhaven.finalproject.model.Customer;
import pastryhaven.finalproject.model.Payment;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findAllByOrderByPaymentDateDesc();
    List<Payment> getPaymentsByCustomer(Customer customer);
    List<Payment> findByCustomer(Customer customer);

}