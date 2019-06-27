package ch.uzh.seal.BLogDiff.service;

import ch.uzh.seal.BLogDiff.model.tracking.BugReport;
import ch.uzh.seal.BLogDiff.model.tracking.TrackingEntry;
import ch.uzh.seal.BLogDiff.repository.BugRepository;
import ch.uzh.seal.BLogDiff.repository.TrackingRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TrackingService {

    @Autowired
    private TrackingRepository trackingRepository;

    @Autowired
    private BugRepository bugRepository;


    public void saveTracking(TrackingEntry trackingEntry) {
        this.trackingRepository.save(trackingEntry);
    }

    public void saveBug(BugReport bugReport) {
        this.bugRepository.save(bugReport);
    }
}
