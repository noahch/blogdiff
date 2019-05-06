package ch.uzh.seal.BLogDiff.model.parsing;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class BuildLog {
    private Component ciComponent;
    private Component buildComponent;
}
