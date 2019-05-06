package ch.uzh.seal.BLogDiff.model.parsing;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
public class Goal {
    private String plugin;
    private String goal;
    private List<LogLine> logLines;
}
