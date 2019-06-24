package ch.uzh.seal.BLogDiff.controller;

import ch.uzh.seal.BLogDiff.differencing.ASTDifferencer;
import ch.uzh.seal.BLogDiff.differencing.LineDifferencer;
import ch.uzh.seal.BLogDiff.exception.PreviousJobNotFoundException;
import ch.uzh.seal.BLogDiff.mapping.NodeLevelMapper;
import ch.uzh.seal.BLogDiff.model.DifferencingResult;
import ch.uzh.seal.BLogDiff.model.Message;
import ch.uzh.seal.BLogDiff.model.MessageType;
import ch.uzh.seal.BLogDiff.model.parsing.BuildLogTree;
import ch.uzh.seal.BLogDiff.model.parsing.EditTree;
import ch.uzh.seal.BLogDiff.model.rest.Job;
import ch.uzh.seal.BLogDiff.model.survey.SurveyResult;
import ch.uzh.seal.BLogDiff.repository.SurveyRepository;
import ch.uzh.seal.BLogDiff.service.GitService;
import ch.uzh.seal.BLogDiff.service.TravisService;
import ch.uzh.seal.BLogDiff.utils.EditTreeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
@CrossOrigin(origins =  {"${GUI.HOST}"})
public class DifferencingController {

    @Autowired
    private NodeLevelMapper nodeLevelMapper;

    @Autowired
    private TravisService travisService;

    @Autowired
    private GitService gitService;




    @RequestMapping("/differencing/{jobId}")
    public DifferencingResult differencingSingle(@PathVariable("jobId") String id) {
        try{
            List<Message> messages = new ArrayList<>();

            if(!checkMavenProject(id, messages)){
                return DifferencingResult.builder().messageList(messages).build();
            }

            String id2 = String.valueOf(travisService.getPreviousSuccessfulJog(id).getId());
            BuildLogTree tree1 = travisService.getBuildLogTree(id2);
            BuildLogTree tree2 = travisService.getBuildLogTree(id);

            EditTree editTree = nodeLevelMapper.map(tree1, tree2, new LineDifferencer());

            return DifferencingResult.builder()
                    .jobIdBefore(id2)
                    .jobIdAfter(id)
                    .treeBefore(tree1)
                    .treeAfter(tree2)
                    .editTree(editTree)
                    .additions(EditTreeUtils.getAdditions(editTree))
                    .deletions(EditTreeUtils.getDeletions(editTree))
                    .moves(EditTreeUtils.getMoves(editTree))
                    .updates(EditTreeUtils.getUpdates(editTree))
                    .messageList(messages)
                    .build();
        }catch (PreviousJobNotFoundException e){
            List<Message> messages = new ArrayList<>();
            checkMavenProject(id, messages);
            messages.add(Message.builder().message("Previous Job could not be selected automatically").messageType(MessageType.INFO).build());
            BuildLogTree tree2 = travisService.getBuildLogTree(id);
            EditTree editTree = nodeLevelMapper.map(null, tree2, new LineDifferencer());
            return DifferencingResult.builder()
                    .jobIdAfter(id)
                    .treeBefore(null)
                    .treeAfter(tree2)
                    .editTree(editTree)
                    .additions(EditTreeUtils.getAdditions(editTree))
                    .deletions(EditTreeUtils.getDeletions(editTree))
                    .moves(EditTreeUtils.getMoves(editTree))
                    .updates(EditTreeUtils.getUpdates(editTree))
                    .messageList(messages)
                    .build();
        }
        catch (Exception e){
            List<Message> messages = new ArrayList<>();
            messages.add(Message.builder().message(e.getMessage()).messageType(MessageType.ERROR).build());
            return DifferencingResult.builder().messageList(messages).build();
        }
        catch (Error e){
            List<Message> messages = new ArrayList<>();
            messages.add(Message.builder().message(e.getMessage()).messageType(MessageType.ERROR).build());
            return DifferencingResult.builder().messageList(messages).build();
        }
    }

    @RequestMapping("/differencing/{jobId1}/{jobId2}")
    public DifferencingResult differencingDouble(@PathVariable("jobId1") String id1, @PathVariable("jobId2") String id2) {
        try{
            BuildLogTree tree1 = travisService.getBuildLogTree(id1);
            BuildLogTree tree2 = travisService.getBuildLogTree(id2);

            EditTree editTree = nodeLevelMapper.map(tree1, tree2, new LineDifferencer());
            return DifferencingResult.builder()
                    .jobIdBefore(id1)
                    .jobIdAfter(id2)
                    .treeBefore(tree1)
                    .treeAfter(tree2)
                    .editTree(editTree)
                    .additions(EditTreeUtils.getAdditions(editTree))
                    .deletions(EditTreeUtils.getDeletions(editTree))
                    .moves(EditTreeUtils.getMoves(editTree))
                    .updates(EditTreeUtils.getUpdates(editTree))
                    .build();
        }catch (Exception e){
            //TODO: error handling
            log.error(e.getMessage());
        }
        return null;
    }

    @RequestMapping("/repo/{user}/{repository}")
    public List<Job> getJobsForRepo(@PathVariable("user") String user, @PathVariable("repository") String repository) {
        if(travisService.checkRepoExists(user + "/" + repository)){
            // TODO: Complete
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


    private boolean checkMavenProject(String jobId, final List<Message> messages){
        boolean mavenProject = this.gitService.checkIfMavenProject(travisService.getRepoSlugByJobId(jobId));
        if(!mavenProject){
            messages.add(Message.builder().message("The project you have selected is not a Maven/Java Project").messageType(MessageType.ERROR).build());
        }
        return mavenProject;
    }



}
