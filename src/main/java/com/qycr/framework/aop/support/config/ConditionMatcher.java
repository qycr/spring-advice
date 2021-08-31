package com.qycr.framework.aop.support.config;

public class ConditionMatcher {


    private boolean match;

    private String message;

   private  ConditionMatcher(boolean match,String message){
        this.match=match;
        this.message=message;
    }

    public static ConditionMatcher matcher(){
        return matcher(null);
    }

    public static ConditionMatcher matcher(String message){
        return new ConditionMatcher(true,message);
    }

    public static ConditionMatcher nonMatcher(){
        return nonMatch(null);
    }

    public static ConditionMatcher nonMatch(String message){
        return new ConditionMatcher(false,message);
    }

    public ConditionMatcher toMatch(){
        this.match=Boolean.TRUE;
        return this;
    }

    public ConditionMatcher toNonMatch(){
        this.match=Boolean.FALSE;
        return this;
    }

    public ConditionMatcher matcherMessage(String message){
        this.message=message;
        return this;
    }

    public String message() {
        return message;
    }

    public boolean isMatch(){
       return this.match;
    }
}
