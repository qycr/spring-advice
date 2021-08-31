package com.qycr.framework.aop.support.engine;


import java.util.Collection;

//TODO...
public interface AdviceManager {


    Advice getAdvice(String name);

    Collection<String> getAdviceNames();


}
