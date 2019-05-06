package ch.uzh.seal.BLogDiff.model.parsing;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
public class MvnComponent extends Component {
    private List<LogLine> linesBeforeModules;
    private List<LogLine> linesAfterModules;
    private List<Module> modules;
}
