package com.qycr.framework.aop.support.annotation;

import com.qycr.framework.aop.support.config.AdviceTypeSelector;
import org.springframework.aop.Pointcut;

import java.lang.annotation.Annotation;


public class AdviceExecutionConfiguration extends AbstractAdviceConfiguration{


    protected Pointcut buildPointcut() {
        return AdviceTypeSelector.INSTANCE.buildPointcuts((Class<? extends Annotation>[])this.attributes.getClassArray("annotationTypes"));
    }


    @Override
    protected Pointcut[] pointcut() {
        return new Pointcut[]{buildPointcut()};
    }


}
