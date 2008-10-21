package jade.content.onto.bob;

//#J2ME_EXCLUDE_FILE
//#APIDOC_EXCLUDE_FILE

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Result {

	Class type();
}
