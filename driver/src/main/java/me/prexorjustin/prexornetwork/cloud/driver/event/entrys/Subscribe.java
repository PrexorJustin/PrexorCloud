package me.prexorjustin.prexornetwork.cloud.driver.event.entrys;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Subscribe {

    int priority() default Priority.MEDIUM;

    boolean async() default false;

}
