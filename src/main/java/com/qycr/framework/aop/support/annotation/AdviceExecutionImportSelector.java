package com.qycr.framework.aop.support.annotation;

import com.qycr.framework.aop.support.config.AdviceType;

public class AdviceExecutionImportSelector extends AdviceTypeImportSelector<EnableAdvice> {


    @Override
    protected String[] selectImports(AdviceType adviceType) {

        switch (adviceType){

            case ANNOTATION:
                return new String[]{AdviceExecutionConfiguration.class.getName()};
            case REGEX:
                return new String[]{RegexpMethodAdviceConfiguration.class.getName()};
            case ASPECTJ:
                return new String[]{AspectjExpressAdviceConfiguration.class.getName()};
            case CONSUMER:
                return new String[]{ConsumerAdviceConfiguration.class.getName()};
            default:
                return new String[0];
        }
    }
}
