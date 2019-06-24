package ch.uzh.seal.BLogDiff.controller;

import ch.uzh.seal.BLogDiff.model.tracking.ContactEmail;
import ch.uzh.seal.BLogDiff.model.tracking.TrackingEntry;
import ch.uzh.seal.BLogDiff.service.TrackingService;
import ch.uzh.seal.BLogDiff.service.TravisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@Slf4j
@CrossOrigin(origins =  {"${GUI.HOST}"})
public class TrackingController {

    @Autowired
    private TrackingService trackingService;


    @Autowired
    private TravisService travisService;

    @PostMapping("/tracking")
    public void track(@RequestBody TrackingEntry trackingEntry)  {
        trackingEntry.setTimestamp(new Date());
        trackingEntry.setRepository(travisService.getRepoSlugByJobId(trackingEntry.getLogId2()));
        this.trackingService.saveTracking(trackingEntry);
    }
}
