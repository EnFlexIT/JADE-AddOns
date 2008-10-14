package jade.content.onto.bob;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Slot {
	String USE_METHOD_NAME = "__USE_METHOD_NAME__";

	String name() default USE_METHOD_NAME;
	boolean mandatory() default false;
}
