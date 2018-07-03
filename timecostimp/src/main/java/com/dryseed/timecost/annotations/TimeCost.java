package com.dryseed.timecost.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author caiminming
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface TimeCost {
    String name() default "";

    long milliTime() default 1000L;
}
