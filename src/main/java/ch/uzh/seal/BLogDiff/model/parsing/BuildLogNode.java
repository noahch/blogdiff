package ch.uzh.seal.BLogDiff.model.parsing;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
public class BuildLogNode {
    private String nodeName;
    private List<LogLine> linesBefore;
    private List<BuildLogNode> logNodes;
    private List<LogLine> linesAfter;
}
