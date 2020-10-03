package com.dragon.apt.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Documented
@Inherited
@Retention(RetentionPolicy.SOURCE)
public @interface Serial {
}
