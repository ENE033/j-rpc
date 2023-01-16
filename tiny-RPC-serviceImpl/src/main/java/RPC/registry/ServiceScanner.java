package RPC.registry;

import RPC.core.ServiceRegistry;
import RPC.core.annotation.Service;
import RPC.core.annotation.ServiceScan;
import RPC.core.util.ReflectUtil;
import RPC.core.ServiceProvider;
import cn.hutool.core.util.ClassUtil;

import java.net.InetSocketAddress;

public class ServiceScanner {

    public void scanServices(InetSocketAddress inetSocketAddress) {
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
            for (Class<?> clazz : ClassUtil.scanPackageByAnnotation(basePackage, Service.class)) {
                for (Class<?> anInterface : clazz.getInterfaces()) {
                    ServiceProvider.addService(anInterface.getCanonicalName(), clazz);
                    ServiceRegistry.registry(anInterface.getCanonicalName(), inetSocketAddress);
//                    for (Method declaredMethod : anInterface.getDeclaredMethods()) {
//                        ServiceProvider.addMethod(anInterface.getCanonicalName(), declaredMethod);
//                    }
                }
            }
        }
    }

}
