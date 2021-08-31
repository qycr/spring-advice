package com.qycr.framework.aop.support.exception;

public class AdviceLoadClassException extends AdviceExecutionHandlerException{


    private final  Integer code;


    public AdviceLoadClassException(String message) {
        super(message);
        this.code=null;
    }

    public AdviceLoadClassException(String message,Integer code){
        super(message);
        this.code=code;
    }

    public AdviceLoadClassException(Throwable e,Integer code){
        super(e);
        this.code=code;
    }

    public AdviceLoadClassException(Throwable e,String record){
        super(record,e);
        this.code=null;
    }

    public Integer getCode() {
        return code;
    }
}
