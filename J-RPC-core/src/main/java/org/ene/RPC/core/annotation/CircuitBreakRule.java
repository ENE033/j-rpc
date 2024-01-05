package org.ene.RPC.core.annotation;


import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface CircuitBreakRule {

    String strategy();

    int timeOut();

    long interval() default 30;

    int exceptionCountThreshold() default 10;

    double exceptionRate() default 0;
}