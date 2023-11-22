package org.ene.RPC.core.annotation;


import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface CircuitBreakRule {

    String strategy();

    int timeOut();

    int exceptionCount() default 0;

    double exceptionRate() default 0;
}