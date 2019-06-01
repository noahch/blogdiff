package ch.uzh.seal.BLogDiff.client;

import ch.uzh.seal.BLogDiff.model.rest.Build;
import ch.uzh.seal.BLogDiff.model.rest.Job;
import ch.uzh.seal.BLogDiff.model.rest.Log;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import lombok.extern.slf4j.Slf4j;



/**
 * TravisRestClient handles requests to the travis-api/v3/
 */
@Slf4j
public class TravisRestClient extends AbstractUnirestClient {

    private final String travisApiBaseUrl = "https://api.travis-ci.org/v3/";

    /**
     * Constructor, handles setup of the RESTclient
     */
    public TravisRestClient() {
        setupUnirest();
    }

    /**
     * Get build information form the travis api
     * @param buildIdentifier buildId
     * @return Build
     */
    public Build getBuild(String buildIdentifier){
        HttpResponse<Build> response = Unirest.get(travisApiBaseUrl + "build/" + buildIdentifier).asObject(Build.class);
        Build build = response.getBody();
        log.info("Build retrieved: " + build.toString());
        return build;
    }

    /**
     * Get the log of a job form the travis api
     * @param jobIdentifier jobId
     * @return Log
     */
    public Log getLog(String jobIdentifier){
        HttpResponse<Log> response = Unirest.get(travisApiBaseUrl + "job/" + jobIdentifier + "/log").asObject(Log.class);
        Log buildLog = response.getBody();
        log.info("Log retrieved: (buildLog is not printed)" + buildLog.getId());
        return buildLog;
    }

    /**
     * Get the job form the travis api
     * @param jobIdentifier jobId
     * @return Log
     */
    public Job getJob(String jobIdentifier){
        HttpResponse<Job> response = Unirest.get(travisApiBaseUrl + "job/" + jobIdentifier).asObject(Job.class);
        Job job = response.getBody();
        log.info("job retrieved:" + job.getId());
        return job;
    }


}
