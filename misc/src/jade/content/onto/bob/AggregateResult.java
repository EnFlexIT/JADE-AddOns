package jade.content.onto.bob;

import jade.content.schema.ObjectSchema;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface AggregateResult {
	Class type();
	int cardMin() default 0;
	int cardMax() default ObjectSchema.UNLIMITED;
}
