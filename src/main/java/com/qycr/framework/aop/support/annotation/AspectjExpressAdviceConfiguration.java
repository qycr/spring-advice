package com.qycr.framework.aop.support.annotation;

import com.qycr.framework.aop.support.exception.AdviceExpressionException;
import org.springframework.aop.Pointcut;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;

import java.util.stream.Stream;


public class AspectjExpressAdviceConfiguration extends AbstractAdviceConfiguration{

    @Override
    protected Pointcut[] pointcut() {
        if(this.expression.length == 0){
            throw new AdviceExpressionException("expression must not be empty");
        }
        final AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression(this.expression[0]);
        return  Stream.of(pointcut).toArray(Pointcut[]::new);
    }
}
