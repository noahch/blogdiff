package ch.uzh.seal.BLogDiff.model.parsing;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
public class EditAction {

    private String nodeName;
    private List<LineAction> linesBeforeActions;
    private List<LineAction> linesAfterActions;
    private List<NodeAction> nodeActions;
    private List<EditAction> childrenActions;

}
