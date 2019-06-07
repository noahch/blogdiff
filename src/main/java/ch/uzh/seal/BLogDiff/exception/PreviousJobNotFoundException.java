package ch.uzh.seal.BLogDiff.exception;

public class PreviousJobNotFoundException extends Exception {
    public PreviousJobNotFoundException(String errorMsg, Throwable err){
        super(errorMsg, err);
    }
}
