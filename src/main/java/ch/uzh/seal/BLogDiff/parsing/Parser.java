package ch.uzh.seal.BLogDiff.parsing;

import ch.uzh.seal.BLogDiff.model.parsing.BuildLogNode;
import ch.uzh.seal.BLogDiff.model.parsing.LogLine;



public interface Parser {
    BuildLogNode parse(LogLine[] linesBefore, LogLine[] linesAfter);
}
