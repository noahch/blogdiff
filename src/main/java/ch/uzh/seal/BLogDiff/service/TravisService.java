package ch.uzh.seal.BLogDiff.service;

import ch.uzh.seal.BLogDiff.client.TravisRestClient;
import ch.uzh.seal.BLogDiff.exception.PreviousJobNotFoundException;
import ch.uzh.seal.BLogDiff.model.parsing.BuildLogTree;
import ch.uzh.seal.BLogDiff.model.rest.Build;
import ch.uzh.seal.BLogDiff.model.rest.Job;
import ch.uzh.seal.BLogDiff.model.rest.Log;
import ch.uzh.seal.BLogDiff.parsing.TravisMavenParsingHandler;
import ch.uzh.seal.BLogDiff.preprocessing.PreprocessorConfig;
import ch.uzh.seal.BLogDiff.preprocessing.PreprocessorHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
@EnableConfigurationProperties(value = PreprocessorConfig.class)
public class TravisService {

    @Autowired
    private TravisRestClient travisRestClient;

    @Autowired
    private PreprocessorHandler preprocessorHandler;

    @Autowired
    private TravisMavenParsingHandler travisMavenParsingHandler;

    public Log getLog(String jobId){
        return travisRestClient.getLog(jobId);
    }

    public Job getPreviousSuccessfulJog(String jobId) throws PreviousJobNotFoundException {
        try {
            Job job = travisRestClient.getJob(jobId);
            String repositoryIdentifier = job.getRepository().getSlug();
            List<Build> builds = travisRestClient.getBuilds(repositoryIdentifier).getBuilds();
            List<Build> prevSuccessful = builds.stream().filter(
                    build -> build.getState() != null && build.getState().equals("passed") && Integer.valueOf(build.getNumber()) < Integer.valueOf(job.getBuild().getNumber())
            ).collect(Collectors.toList());

            Build prevBuild = getBuildByNumber(prevSuccessful, job.getBuild().getNumberAsInt()-1);
            return getJobByNumber(prevBuild.getJobs(),job.getNumberAsInt());
        }catch (Exception e) {
            throw new PreviousJobNotFoundException("Previous Job not found!", e.getCause());
        }

    }

    public List<Job> getJobList(String jobId) {
        try {
            Job job = travisRestClient.getJob(jobId);
            String repositoryIdentifier = job.getRepository().getSlug();
            List<Build> builds = travisRestClient.getBuilds(repositoryIdentifier).getBuilds();
            List<Job> jobs = new ArrayList<>();
           for(Build b : builds){
               jobs.add(getJobByNumber(b.getJobs(),job.getNumberAsInt()));
           }
           return jobs;
        }catch (Exception e) {
            log.error("Something went wrong while loading jobs");
            return new ArrayList<>();
        }

    }

    public String getRepoSlugByJobId(String jobId){
        Job job = travisRestClient.getJob(jobId);
        return job.getRepository().getSlug();
    }

    public boolean checkRepoExists(String repoSlug){
        return travisRestClient.checkRepoExists(repoSlug);

    }

    public String getFirstJobIdByRepo(String repoSlug) {
        List<Build> builds =  this.travisRestClient.getBuilds(repoSlug).getBuilds();
        if(builds.size() > 0) {
            if(builds.get(0).getJobs().size() > 0){
                return builds.get(0).getJobs().get(0).getId().toString();
            }
        }
        return null;
    }

    @Cacheable("buildLogTree")
    public BuildLogTree getBuildLogTree(String jobId) {
        String log = getLog(jobId).getContent();
        String filteredLog = preprocessorHandler.preprocessLog(log);
        BuildLogTree tree = travisMavenParsingHandler.parse(filteredLog);
        return tree;
    }

    @CacheEvict(allEntries = true, value = "buildLogTree")
    @Scheduled(fixedDelay = 3 * 60 * 60 * 1000)
    public void reportCacheEvict() {
        log.info("Flush Cache");
    }

    @Async
    public void cacheJobs(List<Job> jobs) {
        for (Job j : jobs){
                log.info("caching BuildLogTree of "+ j.getId());
                getBuildLogTree(String.valueOf(j.getId()));
        }
    }


    private Build getBuildByNumber(List<Build> builds, int number){
        for(Build b: builds){
            if(b.getNumberAsInt() == number){
                return b;
            }
            number--;
        }
        return null;
    }

    private Job getJobByNumber(List<Job> jobs, int number){
        for(Job j: jobs){
            if(j.getNumberAsInt() == number){
                return j;
            }
        }
        return null;
    }
}
