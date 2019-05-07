package ch.uzh.seal.BLogDiff.model.parsing;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class NodeAction {
    private String nodeName;
    private int positionBefore;
    private int positionAfter;
    private NodeActionType type;
}
