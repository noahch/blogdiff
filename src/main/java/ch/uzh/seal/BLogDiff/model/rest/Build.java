package ch.uzh.seal.BLogDiff.model.rest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class Build {
    @Getter
    @Setter
    private Long id;

    @Getter
    @Setter
    private String state;

    @Getter
    @Setter
    private String previous_state;

    @Getter
    @Setter
    private List<Job> jobs;

    @Getter
    @Setter
    private Branch branch;

    @Getter
    @Setter
    private String number;
}
