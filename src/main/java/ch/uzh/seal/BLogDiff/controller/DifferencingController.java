package ch.uzh.seal.BLogDiff.controller;

import ch.uzh.seal.BLogDiff.client.TravisRestClient;
import ch.uzh.seal.BLogDiff.differencing.LineDifferencer;
import ch.uzh.seal.BLogDiff.mapping.NodeLevelMapper;
import ch.uzh.seal.BLogDiff.model.DifferencingResult;
import ch.uzh.seal.BLogDiff.model.parsing.BuildLogTree;
import ch.uzh.seal.BLogDiff.model.rest.Job;
import ch.uzh.seal.BLogDiff.service.TravisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@CrossOrigin(origins = "http://localhost:4200")
public class DifferencingController {

    @Autowired
    private NodeLevelMapper nodeLevelMapper;

    @Autowired
    private TravisService travisService;

    @RequestMapping("/differencing/{jobId}")
    public DifferencingResult differencingSingle(@PathVariable("jobId") String id) {
        try{
            String id2 = String.valueOf(travisService.getPreviousSuccessfulJog(id).getId());
            BuildLogTree tree1 = travisService.getBuildLogTree(id2);
            BuildLogTree tree2 = travisService.getBuildLogTree(id);

            return DifferencingResult.builder()
                    .jobIdBefore(id2)
                    .jobIdAfter(id)
                    .treeBefore(tree1)
                    .treeAfter(tree2)
                    .editTree(nodeLevelMapper.map(tree1, tree2, new LineDifferencer())).build();
        }catch (Exception e){
            //TODO: error handling
            log.error(e.getMessage());
        }
       return null;
    }

    @RequestMapping("/differencing/{jobId1}/{jobId2}")
    public DifferencingResult differencingDouble(@PathVariable("jobId1") String id1, @PathVariable("jobId2") String id2) {
        try{
            BuildLogTree tree1 = travisService.getBuildLogTree(id1);
            BuildLogTree tree2 = travisService.getBuildLogTree(id2);

            return DifferencingResult.builder()
                    .jobIdBefore(id1)
                    .jobIdAfter(id2)
                    .treeBefore(tree1)
                    .treeAfter(tree2)
                    .editTree(nodeLevelMapper.map(tree1, tree2, new LineDifferencer())).build();
        }catch (Exception e){
            //TODO: error handling
            log.error(e.getMessage());
        }
        return null;
    }

    @RequestMapping("/jobs/{jobId}")
    public List<Job> closeJobs(@PathVariable("jobId") String id) {
         List<Job> jobs = travisService.getJobList(id);
         travisService.cacheJobs(jobs);
         return jobs;
    }

    @RequestMapping("/testjobs/{jobId}")
    public BuildLogTree closeJobsx(@PathVariable("jobId") String id)  {
        try{
            BuildLogTree bt = travisService.getBuildLogTree(id);
            return bt;
        }catch (Exception e){

        }
        return null;
    }



}
