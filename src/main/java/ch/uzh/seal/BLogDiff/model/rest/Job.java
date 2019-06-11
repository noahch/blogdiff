package ch.uzh.seal.BLogDiff.model.rest;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class Job {
    @Getter
    @Setter
    private Long id;

    @Getter
    @Setter
    private String state;

    @Getter
    @Setter
    private Repository repository;

    @Getter
    @Setter
    private Build build;

    @Getter
    @Setter
    private String number;

    @Getter
    @Setter
    @JsonProperty("finished_at")
    private String finishedAt;

    @JsonIgnore
    public int getNumberAsInt(){
        if(number != null){
            String[] nbrs = number.split("\\.");
            return Integer.valueOf(nbrs[nbrs.length-1]);
        }else{
            return -1;
        }
    }

    @Getter
    @Setter
    private boolean parsedSuccessfully;
}
