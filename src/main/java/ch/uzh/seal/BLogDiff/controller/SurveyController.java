package ch.uzh.seal.BLogDiff.controller;

import ch.uzh.seal.BLogDiff.model.survey.MiniSurveyResult;
import ch.uzh.seal.BLogDiff.model.survey.SurveyResult;
import ch.uzh.seal.BLogDiff.model.tracking.ContactEmail;
import ch.uzh.seal.BLogDiff.repository.ContactEmailRepository;
import ch.uzh.seal.BLogDiff.repository.MiniSurveyRepository;
import ch.uzh.seal.BLogDiff.repository.SurveyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@Slf4j
@CrossOrigin(origins =  {"${GUI.HOST}"})
public class SurveyController {


    @Autowired
    private SurveyRepository surveyRepository;

    @Autowired
    private MiniSurveyRepository miniSurveyRepository;

    @Autowired
    private ContactEmailRepository contactEmailRepository;

    @PostMapping("/survey/{source}/{usedTool}")
    public void survey(@RequestBody SurveyResult survey, @PathVariable("source") int source,@PathVariable("usedTool") boolean usedTool)  {
        survey.setSource(source);
        survey.setUsedTool(usedTool);
        survey.setTimestamp(new Date());
        this.surveyRepository.save(survey);
        log.info(survey.toString());
    }

    @PostMapping("/miniSurvey")
    public void miniSurvey(@RequestBody MiniSurveyResult survey)  {
        survey.setTimestamp(new Date());
        this.miniSurveyRepository.save(survey);
        log.info(survey.toString());
    }

    @PostMapping("/contact")
    public void contactEmail(@RequestBody ContactEmail email)  {
        email.setTimestamp(new Date());
        this.contactEmailRepository.save(email);
        log.info(email.toString());
    }
}
