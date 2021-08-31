package com.qycr.framework.aop.support.annotation;

import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class ConsumerAdviceConfiguration extends AbstractAdviceConfiguration {

    @Override
    protected Pointcut[] pointcut() {
        final Class<? extends Pointcut>[] pointcuts = (Class<? extends Pointcut>[]) this.attributes.getClassArray("pointcut");
        List<Pointcut> result = new ArrayList<>();
        for (Class<? extends Pointcut> pointcut : pointcuts) {
            final AdvicePointcut instantiateClass = (AdvicePointcut) BeanUtils.instantiateClass(pointcut);
            instantiateClass.setExpression(expression);
            result.add(instantiateClass);
        }
        final String[] advicePointcuts = this.adviceFilter.getStringArray("pointcutBeanName");
        Stream.of(advicePointcuts).filter(StringUtils::hasText).forEach(advicePointcut -> result.add(this.beanFactory.getBean(advicePointcut, Pointcut.class)));
        return result.toArray(new Pointcut[result.size()]);
    }

    @Override
    protected Supplier<Advice> advice() {
        final String adviceBeanName = this.adviceFilter.getString("adviceBeanName");
        if (StringUtils.hasText(adviceBeanName)) {
            Advice advice = this.beanFactory.getBean(adviceBeanName, Advice.class);
            return () -> advice;
        }
        return super.advice();
    }

}
