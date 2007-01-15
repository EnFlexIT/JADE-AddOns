package com.tilab.wsig;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Result {
	
	Class success() default Void.class;
	Class refuse() default String.class;
	Class failure() default String.class;
	
}
