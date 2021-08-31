package com.qycr.framework.aop.support.annotation;

import org.springframework.aop.Pointcut;
import org.springframework.aop.support.JdkRegexpMethodPointcut;
import java.util.stream.Stream;

public class RegexpMethodAdviceConfiguration extends AbstractAdviceConfiguration{


    @Override
    protected Pointcut[] pointcut() {
        final JdkRegexpMethodPointcut pointcut = new JdkRegexpMethodPointcut();
        pointcut.setPatterns(this.expression);
        return Stream.of(pointcut).toArray(Pointcut[]::new);
    }

}
