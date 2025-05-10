package pastryhaven.finalproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pastryhaven.finalproject.model.Customer;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByEmailAddress(String emailAddress);
    boolean existsByEmailAddress(String emailAddress);

}