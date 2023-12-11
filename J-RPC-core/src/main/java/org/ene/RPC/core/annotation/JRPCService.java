package org.ene.RPC.core.annotation;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface JRPCService {
    String beanName() default "";
}
