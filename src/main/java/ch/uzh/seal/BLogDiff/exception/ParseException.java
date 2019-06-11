package ch.uzh.seal.BLogDiff.exception;

public class ParseException extends Exception {
    public ParseException(String errorMsg, Throwable err){
        super(errorMsg, err);
    }
    public ParseException(String errorMsg){
        super(errorMsg);
    }
}
