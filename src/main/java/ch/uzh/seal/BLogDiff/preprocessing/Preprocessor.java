package ch.uzh.seal.BLogDiff.preprocessing;

public interface Preprocessor {

    String process(String buildLog, String replacement);
}
