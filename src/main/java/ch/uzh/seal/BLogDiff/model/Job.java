package ch.uzh.seal.BLogDiff.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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

}
