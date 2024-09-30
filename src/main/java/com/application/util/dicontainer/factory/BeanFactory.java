package com.application.util.dicontainer.factory;

import com.application.util.annotation.Inject;
import com.application.util.dicontainer.config.Configuration;
import com.application.util.dicontainer.config.JavaConfiguration;
import com.application.util.dicontainer.configurator.BeanConfigurator;
import com.application.util.dicontainer.configurator.JavaBeanConfigurator;
import com.application.util.dicontainer.context.ApplicationContext;
import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

public class BeanFactory {

    private final BeanConfigurator beanConfigurator;
    private final Configuration configuration;
    private ApplicationContext applicationContext;

    public BeanFactory(ApplicationContext applicationContext) {
        this.configuration = new JavaConfiguration();
        this.beanConfigurator = new JavaBeanConfigurator(
                configuration.getPackageToScan(),
                configuration.getInterfaceToImplementations()
        );
        this.applicationContext = applicationContext;
    }

    @SneakyThrows
    public <T> T getBean(Class<T> clazz) {
        Class<? extends T> implementationClass = clazz;
        if (implementationClass.isInterface()) {
            implementationClass = beanConfigurator.getImplementationClass(clazz);
        }
        T bean = implementationClass.getDeclaredConstructor().newInstance();

        List<Field> fields = Arrays.stream(implementationClass.getDeclaredFields()).filter(field -> field.isAnnotationPresent(Inject.class)).toList();

        for (Field field : fields) {
            field.setAccessible(true);
            field.set(bean, applicationContext.getBean(field.getType()));
        }

        return bean;
    }


}
