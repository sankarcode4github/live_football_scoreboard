package org.example.exception;

public class ScoreBoardException extends RuntimeException {
    public ScoreBoardException(String message, Throwable th) {
        super(message, th);
    }
}
