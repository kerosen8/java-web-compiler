package com.application;

import com.application.util.annotation.CustomFilter;
import com.application.util.annotation.CustomServlet;
import com.application.util.dicontainer.context.ApplicationContext;
import com.application.util.dicontainer.factory.BeanFactory;
import jakarta.servlet.*;
import jakarta.servlet.annotation.HandlesTypes;

import java.util.Set;

@HandlesTypes(value = {CustomServlet.class, CustomFilter.class})
public class Config implements ServletContainerInitializer {

    private ApplicationContext run() {
        ApplicationContext context = new ApplicationContext();
        BeanFactory beanFactory = new BeanFactory(context);
        context.setBeanFactory(beanFactory);
        return context;
    }

    @Override
    public void onStartup(Set<Class<?>> set, ServletContext servletContext) throws ServletException {
        for (Class<?> clazz : set) {
            if (clazz.isAnnotationPresent(CustomFilter.class)) {
                Object instance = run().getBean(clazz);
                servletContext.addFilter(clazz.getName(), (Filter) instance).addMappingForUrlPatterns(null, false, clazz.getAnnotation(CustomFilter.class).value());
            }
            if (clazz.isAnnotationPresent(CustomServlet.class)) {
                Object instance = run().getBean(clazz);
                servletContext.addServlet(clazz.getName(), (Servlet) instance).addMapping(clazz.getAnnotation(CustomServlet.class).value());
            }
        }
    }

}
