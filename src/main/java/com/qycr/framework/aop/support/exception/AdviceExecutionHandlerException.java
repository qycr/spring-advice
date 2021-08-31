package com.qycr.framework.aop.support.exception;

public class AdviceExecutionHandlerException extends RuntimeException{

    private final String record;


   public AdviceExecutionHandlerException(String message){
         super(message);
         this.record=message;

   }

    public AdviceExecutionHandlerException(String message, Throwable cause) {
        super(message, cause);
        this.record=message;
    }

    public AdviceExecutionHandlerException(Throwable cause) {
        super(cause);
        this.record=cause.getMessage();
    }

    public String getRecord() {
        return record;
    }
}
