package pastryhaven.finalproject.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import pastryhaven.finalproject.model.Payment;
import pastryhaven.finalproject.repository.PaymentRepository;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    public Payment savePayment(Payment payment) {
        return paymentRepository.save(payment);
    }

    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id).orElse(null);
    }

    public Payment getPaymentByReferenceNumber(String referenceNumber) {
        return paymentRepository.findByReferenceNumber(referenceNumber);
    }
}
