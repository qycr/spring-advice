package com.qycr.framework.aop.support.engine.filter;

import com.qycr.framework.aop.support.engine.Advice;

//TODO...
public interface AdviceFilter {


    public void init(AdviceConfig adviceConfig);

    public void doAdviceFilter(Advice advice, AdviceExecutionChain adviceChain);

    public void destroy();

}
