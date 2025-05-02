package pastryhaven.finalproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pastryhaven.finalproject.model.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Payment findByReferenceNumber(String referenceNumber);
}
