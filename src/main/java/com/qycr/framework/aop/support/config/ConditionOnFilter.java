package com.qycr.framework.aop.support.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.ClassUtils;

import java.util.Arrays;
import java.util.Objects;

@Slf4j
public class ConditionOnFilter extends AdviceCondition implements Condition {


    protected  void match(ListableBeanFactory beanFactory,Class<?>[] types,ConditionMatcher match){
        int initialize = types.length;
        while (initialize > 0) {
            if (!(Arrays.asList(BeanFactoryUtils.beanNamesForTypeIncludingAncestors(beanFactory, types[initialize-1])).isEmpty())) {
                match.toMatch().matcherMessage(String.format("class Type bean  (%s) definition already exists", types[initialize-1].getName()));
                break;
            }
            initialize--;
        }
    }


    @Override
    protected boolean isMatch(String name, ConditionContext context, AnnotatedTypeMetadata metadata) {

        final ConditionMatcher match = ConditionMatcher.nonMatch("initialize status not matcher");
        final AnnotationAttributes attributes = AnnotationAttributes.fromMap(metadata.getAnnotationAttributes(ConditionOnDefinition.class.getName()));
        if(Objects.isNull(attributes)){//NPE
            return match.isMatch();
        }
        final String[] names = attributes.getStringArray("name");
        int initialize = names.length;
        while (initialize > 0) {
            if (context.getRegistry().containsBeanDefinition(names[initialize - 1])) {
                match.toMatch().matcherMessage(String.format("bean name (%s) definition already exists", name));
                break;
            }
            initialize--;
        }

        final Class<?>[] array = attributes.getClassArray("classes");
        if (array.length > 0 && !match.isMatch()) {
            match(context.getBeanFactory(), array, match);
        }
        if (!match.isMatch()) {
            try {
                match(context.getBeanFactory(), new Class<?>[]{ClassUtils.forName(name, context.getClassLoader())}, match);
            } catch (ClassNotFoundException e) {
                //skip
                match.toMatch().matcherMessage("Class or MethodReturnType not exists");
            }
        }
        if(match.isMatch()){
           log.debug(match.message());
        }
        return !match.isMatch();
    }


}
