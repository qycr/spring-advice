package com.qycr.framework.aop.support.annotation;


import com.qycr.framework.aop.support.config.AdviceType;
import org.springframework.context.annotation.*;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.lang.annotation.Annotation;
import java.util.Objects;

public abstract class AdviceTypeImportSelector<A extends Annotation>  implements  ImportSelector {

    public static final String ADVICE_TYPE_ATTRIBUTE_NAME = "type";

    protected String getAdviceTypeAttributeName() {
        return ADVICE_TYPE_ATTRIBUTE_NAME;
    }


    @Override
    public final String[] selectImports(AnnotationMetadata annotationMetadata) {
        Class<?> annType = GenericTypeResolver.resolveTypeArgument(getClass(), AdviceTypeImportSelector.class);
        Assert.state(annType != null, "Unresolvable type argument for AdviceTypeImportSelector");

        AnnotationAttributes attributes = AnnotationAttributes.fromMap(annotationMetadata.getAnnotationAttributes(annType.getName()));

        if(Objects.isNull(attributes)){
            throw new IllegalArgumentException(String.format(
                    "@%s is not present on importing class '%s' as expected",
                    annType.getSimpleName(), annotationMetadata.getClassName()));
        }
        final AnnotationAttributes advice = attributes.getAnnotation("advice");
        AdviceType adviceType = advice.getEnum(getAdviceTypeAttributeName());
        String[] imports = selectImports(adviceType);
        if (imports == null) {
            throw new IllegalArgumentException(String.format("Unknown AdviceType: %s",adviceType));
        }
        return imports;
    }

    @Nullable
    protected abstract String[] selectImports(AdviceType adviceType);



}
