package ch.uzh.seal.BLogDiff.client;

import ch.uzh.seal.BLogDiff.model.rest.*;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


/**
 * TravisRestClient handles requests to the travis-api/v3/
 */
@Slf4j
@Component
public class TravisRestClient extends AbstractUnirestClient {

    private final String travisApiBaseUrl = "https://api.travis-ci.org/v3/";

    /**
     * Constructor, handles setup of the RESTclient
     */
    public TravisRestClient() {
        setupUnirest();
    }


    /**
     * Get information of all builds in the repository form the travis api
     * @param repositoryIdentifier repositoryId oder repositorySlug
     * @return Build
     */
    public boolean checkRepoExists(String repositoryIdentifier){
        // TODO: handle paging if there are more than 20 builds
        HttpResponse<Repository> response = Unirest.get(travisApiBaseUrl + "repo/" + encodeRepositoryIdentifier(repositoryIdentifier)).asObject(Repository.class);
        Repository repo = response.getBody();
        if(repo.getId() != null){
            return true;
        }
        return false;
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
     * Get information of all builds in the repository form the travis api
     * @param repositoryIdentifier repositoryId oder repositorySlug
     * @return Build
     */
    public Builds getBuilds(String repositoryIdentifier){
        // TODO: handle paging if there are more than 20 builds
        HttpResponse<Builds> response = Unirest.get(travisApiBaseUrl + "repo/" + encodeRepositoryIdentifier(repositoryIdentifier)+ "/builds?include=build.jobs" ).asObject(Builds.class);
        Builds builds = response.getBody();
        log.info("Builds retrieved: " + builds.toString());
        return builds;
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

    /**
     * Returns an encoded repositoryIdentifier if it is a slug, else it return the repositoryId as is.
     * @param repositoryIdentifier repositoryId oder repositorySlug
     * @return repositoryIdentifier in a format that can be used for the travis api
     */
    private String encodeRepositoryIdentifier(String repositoryIdentifier) {
        if (!StringUtils.isNumeric(repositoryIdentifier)){
            try {
                return URLEncoder.encode(repositoryIdentifier, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            return repositoryIdentifier;
        }
    }


}
