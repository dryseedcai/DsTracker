package com.dryseed.dstracker.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
public @interface TimeCost {
    String name() default "";

    long milliTime() default 1000L;
}
