package com.qycr.framework.aop.support.exception;

public class AdviceExecutionProxyException extends AdviceExecutionHandlerException{


    public AdviceExecutionProxyException(String message){
        super(message);
    }

    public AdviceExecutionProxyException(String record,Throwable e){
        super(record,e);
    }


}
