package ch.uzh.seal.BLogDiff.controller;

import ch.uzh.seal.BLogDiff.analysis.PreprocessingAnalysis;
import ch.uzh.seal.BLogDiff.client.TravisRestClient;
import ch.uzh.seal.BLogDiff.model.DifferencingResult;
import ch.uzh.seal.BLogDiff.model.parsing.BuildLogTree;
import ch.uzh.seal.BLogDiff.model.parsing.EditAction;
import ch.uzh.seal.BLogDiff.model.parsing.EditTree;
import ch.uzh.seal.BLogDiff.model.rest.Build;
import ch.uzh.seal.BLogDiff.parsing.TravisMavenParsingHandler;
import ch.uzh.seal.BLogDiff.preprocessing.PreprocessorConfig;
import ch.uzh.seal.BLogDiff.preprocessing.PreprocessorHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
@EnableConfigurationProperties(value = PreprocessorConfig.class)
@CrossOrigin(origins = "http://localhost:4200")
public class DifferencingController {

    private final TravisRestClient travisRestClient = new TravisRestClient();

    @Autowired
    private PreprocessorHandler preprocessorHandler;

    @Autowired
    private TravisMavenParsingHandler travisMavenParsingHandler;


    @RequestMapping("/differencing/{logId}")
    public String differencing(@PathVariable("logId") String id) {
        Build build = travisRestClient.getBuild(id);
        if (build != null){
            if(build.getJobs().get(0) != null){
                String buildLog = travisRestClient.getLog(build.getJobs().get(0).getId().toString()).getContent();
                return preprocessorHandler.preprocessLog(buildLog);

            }
        }
        return "";
    }

    @RequestMapping("/{logId1}/{logId2}")
    public DifferencingResult all(@PathVariable("logId1") String id1, @PathVariable("logId2") String id2) {
        Build build = travisRestClient.getBuild(id1);
        if (build != null){
            if(build.getJobs().get(0) != null){
                String log = travisRestClient.getLog(build.getJobs().get(0).getId().toString()).getContent();
                String filteredLog = preprocessorHandler.preprocessLog(log);

                Build build2 = travisRestClient.getBuild(id2);
                if (build2 != null) {
                    if (build2.getJobs().get(0) != null) {
                        String log2 = travisRestClient.getLog(build2.getJobs().get(0).getId().toString()).getContent();
                        String filteredLog2 = preprocessorHandler.preprocessLog(log2);


                        return DifferencingResult.builder()
                                .treeBefore(travisMavenParsingHandler.parse(filteredLog))
                                .treeAfter(travisMavenParsingHandler.parse(filteredLog2))
                                .editTree(EditTree.builder().build()).build();

                    }
                }
            }
        }
        return null;
    }


    @RequestMapping("/analysis/{logId1}/{logId2}")
    public void analysePreprocessing(@PathVariable("logId1") String logId1, @PathVariable("logId2")String logId2){
        Build build = travisRestClient.getBuild(logId1);
        Build build2 = travisRestClient.getBuild(logId2);
        if (build != null && build2 != null){
            if(build.getJobs().get(0) != null && build2.getJobs().get(0) != null){
                String buildLog1 = travisRestClient.getLog(build.getJobs().get(0).getId().toString()).getContent();
                String buildLog2 = travisRestClient.getLog(build2.getJobs().get(0).getId().toString()).getContent();
                PreprocessingAnalysis.analysePreprocessing(
                                                            buildLog1,
                                                            buildLog2,
                                                            preprocessorHandler.preprocessLog(buildLog1),
                                                            preprocessorHandler.preprocessLog(buildLog2));

            }
        }

    }
    @RequestMapping("/analysis")
    public void analysis() {
        PreprocessingAnalysis.analyseInputDir();
    }

    @RequestMapping("/reanalysis")
    public void reAnalysis() {
        PreprocessingAnalysis.reAnalyseDir();
    }
}
