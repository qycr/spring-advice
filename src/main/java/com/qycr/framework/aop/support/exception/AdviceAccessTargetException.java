package com.qycr.framework.aop.support.exception;

public class AdviceAccessTargetException extends AdviceExecutionHandlerException{


    public AdviceAccessTargetException(String message) {
        super(message);
    }

    public AdviceAccessTargetException(String message, Throwable cause) {
        super(message, cause);
    }

    public AdviceAccessTargetException(Throwable cause) {
        super(cause);
    }
}
