package ch.uzh.seal.BLogDiff.parsing;

import ch.uzh.seal.BLogDiff.model.parsing.Component;
import ch.uzh.seal.BLogDiff.model.parsing.LogLine;

import java.util.List;

public interface Parser {
    Component parse(LogLine[] input);
}
