package ch.uzh.seal.BLogDiff.repository;

import ch.uzh.seal.BLogDiff.model.tracking.ContactEmail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactEmailRepository extends JpaRepository<ContactEmail, Long> {

}
