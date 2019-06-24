package ch.uzh.seal.BLogDiff.repository;

import ch.uzh.seal.BLogDiff.model.tracking.TrackingEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrackingRepository extends JpaRepository<TrackingEntry, Long> {

}
