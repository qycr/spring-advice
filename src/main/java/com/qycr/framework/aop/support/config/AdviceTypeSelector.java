package com.qycr.framework.aop.support.config;

import com.qycr.framework.aop.support.annotation.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.ComposablePointcut;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.util.CollectionUtils;

import java.lang.annotation.Annotation;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AdviceTypeSelector {



    public static final AdviceTypeSelector INSTANCE=new AdviceTypeSelector();


    private AdviceTypeSelector(){

    }

    public  Pointcut buildPointcuts(Class<? extends Annotation>[] annotationTypes){

        final Set<Class<? extends Annotation>> collect = Stream.of(annotationTypes).filter(Objects::nonNull).collect(Collectors.toSet());

        if (CollectionUtils.isEmpty(collect)) {
            collect.add(Advice.class);
        }
        ComposablePointcut result = null;
        for (Class<? extends Annotation> annotationType : collect) {
            Pointcut cpc = new AnnotationMatchingPointcut(annotationType, true);
            Pointcut mpc = new AnnotationMatchingPointcut(null, annotationType, true);
            if (result == null) {
                result = new ComposablePointcut(cpc);
            } else {
                result.union(cpc);
            }
            result = result.union(mpc);
        }


        return (result != null ? result : Pointcut.TRUE);

    }

    public Pointcut buildPointcut(Pointcut[] pointcuts) {

        ComposablePointcut composablePointcut = null;
        for (Pointcut pt : pointcuts) {
            if (Objects.isNull(composablePointcut)) {
                composablePointcut = new ComposablePointcut(pt);
            } else {
                composablePointcut = composablePointcut.union(pt);
            }
        }
        return composablePointcut;

    }




}
