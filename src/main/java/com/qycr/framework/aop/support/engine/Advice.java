package com.qycr.framework.aop.support.engine;

//TODO...
public interface Advice<P,S> {

   S advice(P param);


  void  resolver(P param);

}
