package com.qycr.framework.aop.support.config;

import com.qycr.framework.aop.support.annotation.Advice;
import com.qycr.framework.aop.support.annotation.AdviceExecutionConfiguration;
import com.qycr.framework.aop.support.engine.inject.AdviceProxyAwarePostProcessor;
import com.qycr.framework.aop.support.exception.AdviceLoadClassException;
import com.qycr.framework.aop.support.replacer.AdviceMethodExecutionReplacer;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.*;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.lang.annotation.Annotation;
import java.util.Objects;

public class AnnotationDrivenAdviceBeanDefinitionParser implements BeanDefinitionParser {


    private static final String ANNOTATION_TYPES_ATTRIBUTE = "annotation-types";

    private static final String POINTCUT_BEAN_NAME_ATTRIBUTE = "pointcut-bean-name";

    private static final String ADVICE_FILTER_ELEMENT = "advice-filter";

    private static final String ADVICE_TYPE_ATTRIBUTE = "type";

    private static final String ADVICE_PROCESSOR_ATTRIBUTE = "advice-processor";

    private static final String ADVICE_EXPRESSION_ATTRIBUTE = "expression";

    private static final String ADVICE_BEAN_NAME_ATTRIBUTE = "advice-bean-name";


    private final BeanNameGenerator beanNameGenerator = new DefaultBeanNameGenerator();

    @Override
    public BeanDefinition parse(Element element, ParserContext parserContext) {

        if (!parserContext.getRegistry().containsBeanDefinition(AdviceExecutionConfiguration.METHOD_INVOKER_ADVICE)) {

            AdviceFilter adviceFilter = parseAdviceFilter(element, parserContext);

            final Object source = parserContext.extractSource(element);

            final RootBeanDefinition simpleAdviceReplacerBeanDefinition = new RootBeanDefinition();
            simpleAdviceReplacerBeanDefinition.setBeanClassName(element.getAttribute(ADVICE_PROCESSOR_ATTRIBUTE));
            simpleAdviceReplacerBeanDefinition.setSource(source);

            final RootBeanDefinition methodInvokerAdviceBeanDefinition = new RootBeanDefinition(MethodInvokerAdviceExecutionFactoryBean.class);
            methodInvokerAdviceBeanDefinition.setSource(source);
            methodAdviceConstructorArgumentValues(methodInvokerAdviceBeanDefinition, adviceFilter, true);

            final RootBeanDefinition localProxyBeanBeanDefinition = new RootBeanDefinition(LocalProxyBeanFactoryExecutionProcessor.class);
            localProxyBeanBeanDefinition.setSource(source);

            final RootBeanDefinition adviceMethodHandleBeanDefinition = new RootBeanDefinition(AdviceMethodExecutionHandlerFactoryBean.class);
            //adviceMethodHandleBeanDefinition.getPropertyValues().addPropertyValue("pointcut",);
            methodAdviceConstructorArgumentValues(adviceMethodHandleBeanDefinition, adviceFilter, false);
            adviceMethodHandleBeanDefinition.setSource(source);

            final RootBeanDefinition adviceMethodBeanDefinition = new RootBeanDefinition(AdviceMethodExecutionReplacer.class);
            adviceMethodBeanDefinition.setSource(source);

            final RootBeanDefinition adviceProxyAwareBeanDefinition = new RootBeanDefinition(AdviceProxyAwarePostProcessor.class);
            final String enableSingleton = element.getAttribute("enableSingleton");
            if (Boolean.parseBoolean(enableSingleton)) {
                adviceProxyAwareBeanDefinition.getPropertyValues().addPropertyValue("enableSingleton", Boolean.TRUE);
            }
            adviceProxyAwareBeanDefinition.setSource(source);

            parserContext.registerBeanComponent(new BeanComponentDefinition(adviceMethodHandleBeanDefinition, beanNameGenerator.generateBeanName(adviceMethodHandleBeanDefinition, parserContext.getRegistry())));

            parserContext.registerBeanComponent(new BeanComponentDefinition(localProxyBeanBeanDefinition, beanNameGenerator.generateBeanName(localProxyBeanBeanDefinition, parserContext.getRegistry())));

            parserContext.registerBeanComponent(new BeanComponentDefinition(methodInvokerAdviceBeanDefinition, beanNameGenerator.generateBeanName(methodInvokerAdviceBeanDefinition, parserContext.getRegistry())));

            parserContext.registerBeanComponent(new BeanComponentDefinition(adviceMethodBeanDefinition, AdviceMethodExecutionReplacer.COMMON_ADVICE_REPLACER));

            parserContext.registerBeanComponent(new BeanComponentDefinition(adviceProxyAwareBeanDefinition, beanNameGenerator.generateBeanName(adviceProxyAwareBeanDefinition, parserContext.getRegistry())));

            parserContext.registerBeanComponent(new BeanComponentDefinition(simpleAdviceReplacerBeanDefinition, beanNameGenerator.generateBeanName(simpleAdviceReplacerBeanDefinition, parserContext.getRegistry())));

        }
        return null;
    }

    private AdviceFilter parseAdviceFilter(Element element, ParserContext parserContext) {
        NodeList childNodes = element.getChildNodes();
        String annotationTypes = element.getAttribute(ANNOTATION_TYPES_ATTRIBUTE);
        AdviceFilter adviceFilter = new AdviceFilter(AdviceType.ANNOTATION, annotationTypes(annotationTypes, parserContext.getReaderContext().getBeanClassLoader()));
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                String localName = parserContext.getDelegate().getLocalName(node);
                if (ADVICE_FILTER_ELEMENT.equals(localName)) {
                    Element ele = (Element) node;
                    adviceFilter.adviceType(adviceType(ele.getAttribute(ADVICE_TYPE_ATTRIBUTE)))
                            .adviceBeanName(ele.getAttribute(ADVICE_BEAN_NAME_ATTRIBUTE))
                            .expression(StringUtils.delimitedListToStringArray(ele.getAttribute(ADVICE_EXPRESSION_ATTRIBUTE), ";"))
                            .pointcutName(StringUtils.delimitedListToStringArray(ele.getAttribute(POINTCUT_BEAN_NAME_ATTRIBUTE), ";"));
                }
            }

        }
        return adviceFilter;
    }

    private void methodAdviceConstructorArgumentValues(RootBeanDefinition methodInvokerAdviceBeanDefinition, AdviceFilter adviceFilter, boolean typeStatus) {
        ConstructorArgumentValues constructorArgumentValues = methodInvokerAdviceBeanDefinition.getConstructorArgumentValues();
        constructorArgumentValues.addIndexedArgumentValue(0, adviceFilter.pointcutName);
        constructorArgumentValues.addIndexedArgumentValue(1, adviceFilter.annotationTypes);
        constructorArgumentValues.addIndexedArgumentValue(2, adviceFilter.adviceType);
        constructorArgumentValues.addIndexedArgumentValue(3, adviceFilter.expression);
        if (typeStatus) {
            constructorArgumentValues.addIndexedArgumentValue(4, adviceFilter.adviceBeanName);
        }
    }

    private void fillProperty(String[] annClassArray, RootBeanDefinition rootBeanDefinition) {

        if (annClassArray.length == 1 && annClassArray[0].equals(Advice.class.getName())) return;

        Class<? extends Annotation>[] annotationTypes = null;
        for (int i = 0; i < annClassArray.length; i++) {
            try {
                if (Objects.isNull(annotationTypes)) {
                    annotationTypes = new Class[annClassArray.length];
                }
                annotationTypes[i] = (Class<? extends Annotation>) ClassUtils.forName(annClassArray[i], Thread.currentThread().getContextClassLoader());
            } catch (ClassNotFoundException e) {
                throw new AdviceLoadClassException(e.getMessage());
            }
        }
        if (Objects.nonNull(annotationTypes)) {
            rootBeanDefinition.getPropertyValues().addPropertyValue("annotationTypes", annotationTypes);
        }
    }

    private Class<? extends Annotation>[] annotationTypes(String annotationTypes, ClassLoader classLoader) {

        if (!StringUtils.hasText(annotationTypes)) {
            return new Class[]{};
        }
        String[] stringArray = StringUtils.delimitedListToStringArray(annotationTypes, ";");
        Class<? extends Annotation>[] types = new Class[stringArray.length];
        for (int i = 0; i < stringArray.length; i++) {
            try {
                types[i] = (Class<? extends Annotation>) ClassUtils.forName(stringArray[i], classLoader);
            } catch (ClassNotFoundException e) {
                // ignore
            }
        }
        return types;
    }

    private AdviceType adviceType(String adviceType) {

        switch (adviceType) {
            case "annotation":
                return AdviceType.ANNOTATION;
            case "regex":
                return AdviceType.REGEX;
            case "aspectj":
                return AdviceType.ASPECTJ;
            case "consumer":
                return AdviceType.CONSUMER;
            default:
                return null;
        }

    }


    private static class AdviceFilter {

        private AdviceType adviceType;

        private String[] pointcutName;

        private String[] expression;

        private String adviceBeanName;

        private Class<? extends Annotation>[] annotationTypes;

        public AdviceFilter(AdviceType adviceType, Class<? extends Annotation>[] annotationTypes) {
            this.adviceType = adviceType;
            this.annotationTypes = annotationTypes;
        }

        public AdviceFilter(AdviceType adviceType, String[] pointcutName, String[] expression, String adviceBeanName, Class<? extends Annotation>[] annotationTypes) {
            this.adviceType = adviceType;
            this.pointcutName = pointcutName;
            this.expression = expression;
            this.adviceBeanName = adviceBeanName;
            this.annotationTypes = annotationTypes;
        }

        public AdviceType getAdviceType() {
            return adviceType;
        }

        public AdviceFilter adviceType(AdviceType adviceType) {
            this.adviceType = adviceType;
            return this;
        }

        public String[] getPointcutName() {
            return pointcutName;
        }

        public AdviceFilter pointcutName(String[] pointcutName) {
            this.pointcutName = pointcutName;
            return this;
        }

        public String[] getExpression() {
            return expression;
        }

        public AdviceFilter expression(String[] expression) {
            this.expression = expression;
            return this;
        }

        public String getAdviceBeanName() {
            return adviceBeanName;
        }

        public AdviceFilter adviceBeanName(String adviceBeanName) {
            this.adviceBeanName = adviceBeanName;
            return this;
        }

        public Class<? extends Annotation>[] getAnnotationTypes() {
            return annotationTypes;
        }

        public void setAnnotationTypes(Class<? extends Annotation>[] annotationTypes) {
            this.annotationTypes = annotationTypes;
        }
    }




}
