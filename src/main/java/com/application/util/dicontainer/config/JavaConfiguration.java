package com.application.util.dicontainer.config;

import com.application.util.database.PropertiesUtil;

import java.util.Map;

public class JavaConfiguration implements Configuration {
    @Override
    public String getPackageToScan() {
        return PropertiesUtil.getProperty("bean.packageToScan");
    }

    @Override
    public Map<Class, Class> getInterfaceToImplementations() {
        return Map.of();
    }

}
