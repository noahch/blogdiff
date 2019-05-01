package ch.uzh.seal.BLogDiff.controller;

import ch.uzh.seal.BLogDiff.analysis.PreprocessingAnalysis;
import ch.uzh.seal.BLogDiff.client.TravisRestClient;
import ch.uzh.seal.BLogDiff.model.Build;
import ch.uzh.seal.BLogDiff.model.Log;
import ch.uzh.seal.BLogDiff.preprocessing.Preprocessor;
import ch.uzh.seal.BLogDiff.preprocessing.PreprocessorConfig;
import ch.uzh.seal.BLogDiff.preprocessing.PreprocessorHandler;
import ch.uzh.seal.BLogDiff.preprocessing.PreprocessorMaven;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@EnableConfigurationProperties(value = PreprocessorConfig.class)
public class DifferencingController {

    private final TravisRestClient travisRestClient = new TravisRestClient();


    @Autowired
    private PreprocessorHandler preprocessorHandler;


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
