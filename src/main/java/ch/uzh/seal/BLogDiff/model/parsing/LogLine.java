package ch.uzh.seal.BLogDiff.model.parsing;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class LogLine {
    private String content;
    private int lineIndex;
    private int internalLineIndex;
}
