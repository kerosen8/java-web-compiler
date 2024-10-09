package com.application.util.dicontainer.configurator;

import org.reflections.Reflections;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class JavaBeanConfigurator implements BeanConfigurator {

    private final Reflections scanner;
    private final Map<Class, Class> interfaceToImplementation;

    public JavaBeanConfigurator(String packageToScan) {
        this.scanner = new Reflections(packageToScan);
        this.interfaceToImplementation = new ConcurrentHashMap<>();
    }

    @Override
    public <T> Class<? extends T> getImplementationClass(Class<T> interfaceClass) {
        return interfaceToImplementation.computeIfAbsent(interfaceClass, clazz -> {
            Set<Class<? extends T>> implementations = scanner.getSubTypesOf(interfaceClass);
            if (implementations.size() != 1) {
                throw new RuntimeException("Interface has zero or more than one implementation of " + interfaceClass.getName());
            }
            return implementations.stream().findFirst().get();
        });
    }
}
