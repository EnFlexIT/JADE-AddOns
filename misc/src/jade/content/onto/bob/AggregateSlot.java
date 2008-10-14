package jade.content.onto.bob;

import jade.content.schema.ObjectSchema;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface AggregateSlot {
	Class type() default Object.class;
	int cardMin() default 0;
	int cardMax() default ObjectSchema.UNLIMITED;
}
