package ch.uzh.seal.BLogDiff.model.parsing;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
public class TravisComponent extends Component {
//    private List<Phase> phases;
    private List<LogLine> logLines;
}
