package RPC.registry;

import RPC.core.annotation.Service;
import RPC.core.annotation.ServiceScan;
import RPC.core.util.ReflectUtil;
import cn.hutool.core.util.ClassUtil;

import java.io.File;
import java.util.Set;

public class ServiceRegistry {

    public void scanServices() {
        String mainClassName = ReflectUtil.getMainClassName();
        Class<?> mainClass = null;
        try {
            mainClass = Thread.currentThread().getContextClassLoader().loadClass(mainClassName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        assert mainClass != null;
        if (!mainClass.isAnnotationPresent(ServiceScan.class)) {
            return;
        }
        ServiceScan annotation = mainClass.getAnnotation(ServiceScan.class);
        String[] basePackages = annotation.basePackage();
        for (String basePackage : basePackages) {
            Set<Class<?>> classes = ClassUtil.scanPackageByAnnotation(basePackage, Service.class);
        }
    }

}
