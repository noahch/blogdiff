package ch.uzh.seal.BLogDiff.model;

import ch.uzh.seal.BLogDiff.model.parsing.BuildLogTree;
import ch.uzh.seal.BLogDiff.model.parsing.EditTree;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class DifferencingResult {
    private String jobIdBefore;
    private String jobIdAfter;
    private BuildLogTree treeBefore;
    private BuildLogTree treeAfter;
    private EditTree editTree;
}
