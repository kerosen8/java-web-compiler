package com.application.util.dicontainer.context;

import com.application.util.dicontainer.factory.BeanFactory;
import lombok.Setter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ApplicationContext {

    @Setter
    private BeanFactory beanFactory;
    private Map<Class, Object> beans = new ConcurrentHashMap<>();

    public ApplicationContext() {

    }

    public <T> T getBean(Class<T> clazz) {
        if (beans.containsKey(clazz)) {
            return (T) beans.get(clazz);
        }
        T bean = beanFactory.getBean(clazz);
        beans.put(clazz, bean);
        return bean;
    }

}
