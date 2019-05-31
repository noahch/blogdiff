package ch.uzh.seal.BLogDiff.differencing;

import ch.uzh.seal.BLogDiff.model.parsing.LineAction;
import ch.uzh.seal.BLogDiff.model.parsing.LogLine;

import java.util.ArrayList;
import java.util.List;

public interface Differencer {
    List<LineAction> diffLogLines(List<LogLine> lines1, List<LogLine> lines2);
}
