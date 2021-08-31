package com.qycr.framework.aop.support.config;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.MethodMetadata;

public abstract class AdviceCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {

        return isMatch(extractName(metadata),context,metadata);
    }

    protected String extractName(AnnotatedTypeMetadata metadata){
        if(metadata instanceof ClassMetadata){
            ClassMetadata classMetadata=(ClassMetadata) metadata;
            return classMetadata.getClassName();
        }
        else if (metadata instanceof MethodMetadata){
            MethodMetadata methodMetadata=(MethodMetadata) metadata;
            return methodMetadata.getReturnTypeName();
        }else
        {
            return null;
        }

    }


    protected abstract boolean isMatch(String name,ConditionContext context, AnnotatedTypeMetadata metadata);


}
