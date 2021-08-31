package com.qycr.framework.aop.support.exception;

public class AdviceExpressionException extends AdviceExecutionHandlerException{


    public AdviceExpressionException(String message) {
        super(message);
    }

    public AdviceExpressionException(String message, Throwable cause) {
        super(message, cause);
    }

    public AdviceExpressionException(Throwable cause) {
        super(cause);
    }
}
