package ch.uzh.seal.BLogDiff.service;


import ch.uzh.seal.BLogDiff.client.GitRestClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class GitService {
    @Autowired
    private GitRestClient gitRestClient;

    public boolean checkIfMavenProject(String repoSlug){
        return gitRestClient.checkIfPomExists(repoSlug);
    }
}
