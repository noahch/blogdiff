package ch.uzh.seal.BLogDiff.repository;

import ch.uzh.seal.BLogDiff.model.tracking.BugReport;
import ch.uzh.seal.BLogDiff.model.tracking.TrackingEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BugRepository extends JpaRepository<BugReport, Long> {

}
