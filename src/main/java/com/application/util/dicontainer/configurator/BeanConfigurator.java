package com.application.util.dicontainer.configurator;

import com.sun.jdi.InterfaceType;

public interface BeanConfigurator {

    <T> Class<? extends T> getImplementationClass(Class<T> interfaceClass);

}
