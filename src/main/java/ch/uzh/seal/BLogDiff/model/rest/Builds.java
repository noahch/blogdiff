package ch.uzh.seal.BLogDiff.model.rest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class Builds {
    @Getter
    @Setter
    private List<Build> builds;
}
