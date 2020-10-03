package com.dragon.apt.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Documented
@Inherited
@Retention(RetentionPolicy.SOURCE)
public @interface Debugger {
}
