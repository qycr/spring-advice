package com.qycr.framework.aop.support.annotation;

import com.qycr.framework.aop.support.exception.AdviceExpressionException;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.JdkRegexpMethodPointcut;
import java.util.stream.Stream;

public class RegexpMethodAdviceConfiguration extends AbstractAdviceConfiguration{


    @Override
    protected Pointcut[] pointcut() {
        final JdkRegexpMethodPointcut pointcut = new JdkRegexpMethodPointcut();
        if(this.expression.length == 0){
            throw new AdviceExpressionException("expression must not be empty");
        }
        pointcut.setPatterns(expression());
        return Stream.of(pointcut).toArray(Pointcut[]::new);
    }

}
