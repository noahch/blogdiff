package ch.uzh.seal.BLogDiff.parsing;

import ch.uzh.seal.BLogDiff.model.parsing.BuildLog;

public interface ParsingHandler {
    BuildLog parse(String buildLog);
}
