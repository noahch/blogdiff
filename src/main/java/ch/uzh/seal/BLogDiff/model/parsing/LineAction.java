package ch.uzh.seal.BLogDiff.model.parsing;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class LineAction {
    private String contentBefore;
    private String contentAfter;
    private int positionBefore;
    private int positionAfter;
    private LineActionType type;

}
