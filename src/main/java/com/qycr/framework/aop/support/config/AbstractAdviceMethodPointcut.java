package com.qycr.framework.aop.support.config;

import com.qycr.framework.aop.support.exception.AdviceExecutionHandlerException;
import org.springframework.aop.Pointcut;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.JdkRegexpMethodPointcut;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import java.lang.annotation.Annotation;
import java.util.Objects;

public abstract class AbstractAdviceMethodPointcut implements InitializingBean , BeanFactoryAware , EnvironmentAware {

    private final String[] pointcutBeanName;

    private final Class<? extends Annotation>[] annotationTypes;

    private final AdviceType adviceType;

    private final String[] expression;

    protected Environment environment;

    protected BeanFactory beanFactory;

    protected Pointcut[] pointcut;

    AbstractAdviceMethodPointcut(String[] pointcutBeanName, Class<? extends Annotation>[] annotationTypes, AdviceType adviceType, String [] expression){
       this.pointcutBeanName = pointcutBeanName;
       this.annotationTypes = annotationTypes;
       this.adviceType = adviceType;
       this.expression = expression;
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        Pointcut[] pointcuts = null;
        if(Objects.nonNull(this.pointcutBeanName) && this.pointcutBeanName.length > 0){
            pointcuts= new Pointcut[pointcutBeanName.length];
            for(int i = 0; i < pointcutBeanName.length ; i++ ){
                pointcuts[i] = this.beanFactory.getBean(environment.resolvePlaceholders(pointcutBeanName[i]),Pointcut.class);
            }
        }
        if(Objects.isNull(pointcuts)) {
            pointcuts = new Pointcut[1];
            switch (this.adviceType) {

                case ANNOTATION:
                    pointcuts[0] = AdviceTypeSelector.INSTANCE.buildPointcuts(this.annotationTypes);
                    break;
                case ASPECTJ:
                    AspectJExpressionPointcut expressPointcut = new AspectJExpressionPointcut();
                    expressPointcut.setExpression(environment.resolvePlaceholders(this.expression[0]));
                    pointcuts[0]=expressPointcut;
                    break;
                case REGEX:
                    JdkRegexpMethodPointcut methodPointcut = new JdkRegexpMethodPointcut();
                    String[] localExpression = parseExpression();
                    methodPointcut.setPatterns(localExpression);
                    pointcuts[0]=methodPointcut;
                    break;
                case CONSUMER:
                    //No need to deal with
                    break;
                default:
                    throw new AdviceExecutionHandlerException(String.format("unknown  adviceType %s",adviceType));
            }
        }
                  this.pointcut = pointcuts;
    }

    private String[] parseExpression() {
        String[] localExpression = new String[this.expression.length];
        for( int i = 0 ; i < this.expression.length; i ++){
            localExpression[i] = environment.resolvePlaceholders(this.expression[i]);
        }
        return localExpression;

    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
          this.beanFactory = beanFactory;
    }

    @Override
    public void setEnvironment(Environment environment) {
          this.environment = environment;
    }
}
