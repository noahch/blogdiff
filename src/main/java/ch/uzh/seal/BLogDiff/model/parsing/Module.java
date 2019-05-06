package ch.uzh.seal.BLogDiff.model.parsing;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
public class Module {
    private String name;
    private List<LogLine> linesBeforeGoals;
    private List<Goal> goals;
}
