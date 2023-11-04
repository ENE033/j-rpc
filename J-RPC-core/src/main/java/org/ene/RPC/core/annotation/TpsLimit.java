package org.ene.RPC.core.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface TpsLimit {

    int rate();

    long interval();

}
