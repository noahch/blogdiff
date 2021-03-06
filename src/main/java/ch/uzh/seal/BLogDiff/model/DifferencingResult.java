package ch.uzh.seal.BLogDiff.model;

import ch.uzh.seal.BLogDiff.model.parsing.BuildLogTree;
import ch.uzh.seal.BLogDiff.model.parsing.EditTree;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
public class DifferencingResult {
    private String jobIdBefore;
    private String jobIdAfter;
    private String repoSlug;
    private BuildLogTree treeBefore;
    private BuildLogTree treeAfter;
    private EditTree editTree;
    private long additions;
    private long deletions;
    private long moves;
    private long updates;
    private List<Message> messageList;
}
