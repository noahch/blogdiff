package ch.uzh.seal.BLogDiff.parsing;

import ch.uzh.seal.BLogDiff.model.parsing.BuildLogTree;

public interface ParsingHandler {
    BuildLogTree parse(String buildLog);
}
