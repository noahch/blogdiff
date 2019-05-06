package ch.uzh.seal.BLogDiff.model.rest;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ToString
@Builder
public class FailPassPair {
    @Getter
    @Setter
    private Build failedBuild;

    @Getter
    @Setter
    private Build previousPassedBuild;

    public String getDirectoryString(){
        return failedBuild.getId().toString() + "_" + previousPassedBuild.getId().toString();
    }
}
