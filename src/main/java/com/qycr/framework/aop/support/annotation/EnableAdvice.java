package com.qycr.framework.aop.support.annotation;

import com.qycr.framework.aop.support.config.AdviceType;
import org.springframework.context.annotation.Import;
import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(AdviceExecutionImportSelector.class)
public @interface EnableAdvice {

   Class<? extends Annotation>[] annotationTypes() default {};

   AdviceFilter advice() default @AdviceFilter;

   boolean exposeProxy() default false;

   @Retention(RetentionPolicy.RUNTIME)
   @Target({})
   @interface AdviceFilter {

      AdviceType type() default AdviceType.ANNOTATION;

      @Deprecated
      Class<? extends AdvicePointcut>[] advicePointcut() default {};

      String[] pointcutBeanName() default {};

      String[] expression() default {};

      String  adviceBeanName() default "";

   }


}
