package jade.content.onto.bob;

//#J2ME_EXCLUDE_FILE
//#APIDOC_EXCLUDE_FILE

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Element {
	String USE_CLASS_SIMPLE_NAME = "__USE_CLASS_SIMPLE_NAME__";

	String name() default USE_CLASS_SIMPLE_NAME;
}
