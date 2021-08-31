package com.qycr.framework.aop.support.engine.filter;

import com.qycr.framework.aop.support.engine.Advice;

//TODO...
public interface AdviceExecutionChain {


    public <P,S> void doAdviceFilter(Advice<P,S> advice);

}
