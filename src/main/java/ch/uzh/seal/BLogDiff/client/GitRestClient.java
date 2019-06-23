package ch.uzh.seal.BLogDiff.client;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.stereotype.Component;


/**
 * TravisRestClient handles requests to the travis-api/v3/
 */
@Slf4j
@Component
public class GitRestClient extends AbstractUnirestClient {

    private final String githubBaseUrl = "https://github.com/";

    /**
     * Constructor, handles setup of the RESTclient
     */
    public GitRestClient() {
        setupUnirest();
    }

    /**
     * Checks if the project has a pom.xml file in the base directory.
     * @param repositorySlug path of the github repository
     * @return true if a pom.xml exists, false otherwise
     */
    public boolean checkIfPomExists(String repositorySlug) {
        HttpResponse<Object> response = Unirest.get(githubBaseUrl + repositorySlug + "/blob/master/pom.xml").asObject(Object.class);
        if (response.getStatus() == HttpStatus.SC_OK) {
            return true;
        } else {
            return false;
        }
    }

}