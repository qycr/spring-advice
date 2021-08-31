package com.qycr.framework.aop.support.engine.aware;

import org.springframework.aop.framework.Advised;
import org.springframework.beans.factory.Aware;

public interface AdviceAware extends Aware {

    void setAdvice(Advised advised);

}
