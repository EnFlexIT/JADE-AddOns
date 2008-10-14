package jade.content.onto;

import jade.content.AgentAction;
import jade.content.Concept;
import jade.content.onto.bob.AggregateResult;
import jade.content.onto.bob.AggregateSlot;
import jade.content.onto.bob.Element;
import jade.content.onto.bob.Result;
import jade.content.onto.bob.Slot;
import jade.content.onto.bob.SuppressSlot;
import jade.content.schema.AgentActionSchema;
import jade.content.schema.AggregateSchema;
import jade.content.schema.ConceptSchema;
import jade.content.schema.ObjectSchema;
import jade.content.schema.PrimitiveSchema;
import jade.content.schema.TermSchema;
import jade.util.Logger;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class BeanOntologyBuilder {

	private Logger logger = Logger.getMyLogger(this.getClass().getName());

	private static final String GETTER_PREFIX = "get";
	private static final String SETTER_PREFIX = "set";
	private static final Object GET_CLASS_METHOD = "getClass";

	private Ontology ontology;
	private BeanIntrospector introspector;

	public BeanOntologyBuilder(Ontology ontology) throws BeanOntologyBuilderException {
		this.ontology = ontology;
		Introspector ontoIntrospector = ontology.getIntrospector();
		if (ontoIntrospector == null) {
			throw new BeanOntologyBuilderException("introspector of the ontology cannot be null");
		}
		if (!(ontoIntrospector instanceof BeanIntrospector)) {
			throw new BeanOntologyBuilderException("ontology introspector must be of class "+BeanIntrospector.class);
		}
		introspector = (BeanIntrospector)ontoIntrospector;
	}

	private boolean isGetter(Method method) {
		/*
		 * a getter method
		 *   - takes no parameters
		 *   - has a return value
		 *   - its name starts with "get"
		 *   - its 4th char is uppercase
		 *   - its name is not "getClass" :-)
		 */
		String methodName = method.getName(); 
		if (!methodName.startsWith(GETTER_PREFIX)) {
			// it does not start with "get"
			return false;
		}
		if (!Character.isUpperCase(methodName.charAt(3))) {
			// its 4th char is not uppercase
			return false;
		}
		if (void.class.equals(method.getReturnType())) {
			// it does not have a return value
			return false;
		}
		if (method.getParameterTypes().length > 0) {
			// it takes some parameters
			return false;
		}
		if (methodName.equals(GET_CLASS_METHOD)) {
			// it is "getClass", to be discarded
			return false;
		}
		return true;
	}

	private boolean isSetter(Method method) {
		/*
		 * a setter method takes one parameter, does not have a return value and its name starts with "set" and its 4th char is uppercase
		 */
		String methodName = method.getName(); 
		if (!methodName.startsWith(SETTER_PREFIX)) {
			// it does not start with "set"
			return false;
		}
		if (!Character.isUpperCase(methodName.charAt(3))) {
			// its 4th char is not uppercase
			return false;
		}
		if (!void.class.equals(method.getReturnType())) {
			// it has a return value
			return false;
		}
		if (method.getParameterTypes().length != 1) {
			// it does not take exactly 1 parameter
			return false;
		}
		return true;
	}

	private static String buildPropertyNameFromGetter(Method getter) {
		/*
		 * 1) rip of the "get" prefix from method's name
		 * 2) make lower case the 1st char of the result
		 * 
		 * method name       slot name
		 * --------------    ---------
		 * getThatThing() -> thatThing
		 */
		String getterName = getter.getName();
		StringBuilder sb = new StringBuilder();
		sb.append(Character.toLowerCase(getterName.charAt(3)));
		sb.append(getterName.substring(4));
		return sb.toString();
	}

	private static String buildSetterNameFromBeanPropertyName(String beanPropertyName) {
		StringBuilder sb = new StringBuilder(SETTER_PREFIX);
		sb.append(Character.toUpperCase(beanPropertyName.charAt(0)));
		sb.append(beanPropertyName.substring(1));
		return sb.toString();
	}

	private boolean accessorsAreConsistent(Method getter, Method setter) {
		/*
		 *  we have for sure a getter and a setter, so we don't need
		 *  to check the number of parameters and the existence of the return value
		 *  but only their type consistency
		 */
		return getter.getReturnType().equals(setter.getParameterTypes()[0]);
	}

	private String getSchemaNameFromClass(Class clazz) {
		String result = clazz.getSimpleName();
		Element annotationElement = (Element)clazz.getAnnotation(Element.class);
		if (annotationElement != null) {
			if (!Element.USE_CLASS_SIMPLE_NAME.equals(annotationElement.name())) {
				result = annotationElement.name();
			}
		}
		return result;
	}

	public Map<SlotKey, SlotAccessData> buildAccessorsMap(Class clazz, Method[] methodsArray) {
		Map<SlotKey, SlotAccessData> result = new HashMap<SlotKey, SlotAccessData>();
		List<Method> getters = new ArrayList<Method>();
		Map<String, Method> setters = new HashMap<String, Method>();
		for (Method method: methodsArray) {
			if (method.getAnnotation(SuppressSlot.class) == null) {
				if (isGetter(method)) {
					getters.add(method);
				} else if (isSetter(method)) {
					setters.put(method.getName(), method);
				}
			}
		}

		/*
		 * now that we have a list of getters and a map of setters, we iterate through getters
		 * searching for the matching setter; when we find it, we store the couple in a SlotAccessData
		 */
		Iterator<Method> gettersIter = getters.iterator();
		Method getter, setter;
		String setterName;
		Class slotType;
		SlotAccessData sad;
		String propertyName;
		Slot slotAnnotation;
		Class aggregateType;
		AggregateSlot aggregateSlotAnnotation;
		boolean mandatory;
		int cardMin;
		int cardMax;

		while (gettersIter.hasNext()) {
			getter = gettersIter.next();
			slotType = getter.getReturnType();
			mandatory = false;
			cardMin = 0;
			cardMax = ObjectSchema.UNLIMITED;
			aggregateType = null;
			slotAnnotation = getter.getAnnotation(Slot.class);
			aggregateSlotAnnotation = getter.getAnnotation(AggregateSlot.class);

			// build the name of the bean property starting from the getter
			propertyName = buildPropertyNameFromGetter(getter);
			// build the name of the setter name coherent with bean rules
			setterName = buildSetterNameFromBeanPropertyName(propertyName);
			setter = setters.get(setterName);
			if (setter != null) {
				// ok, we have getter and setter, we need to check parameters consistency and we are done
				if (accessorsAreConsistent(getter, setter)) {
					/*
					 * if getter @Slot annotation provides a name, use it; otherwise
					 * use the bean property name
					 */
					String slotName = propertyName;
					if (slotAnnotation != null) {
						/*
						 * if there's a @Slot annotation which specifies the name of the slot, use it
						 */
						if (!Slot.USE_METHOD_NAME.equals(slotAnnotation.name())) {
							slotName = slotAnnotation.name();
						}
						mandatory = slotAnnotation.mandatory();
					}
					// if present, use getter @AggregateSlot annotation data
					if (aggregateSlotAnnotation != null) {
						if (java.util.Collection.class.isAssignableFrom(slotType) || jade.util.leap.Collection.class.isAssignableFrom(slotType)) {
							cardMin = aggregateSlotAnnotation.cardMin();
							cardMax = aggregateSlotAnnotation.cardMax();
							if (!Object.class.equals(aggregateSlotAnnotation.type())) {
								aggregateType = aggregateSlotAnnotation.type();
							}
						} else {
							// FIXME this is not an aggregate!!!
						}
					}
					sad = new SlotAccessData(slotType, getter, setter, mandatory, aggregateType, cardMin, cardMax);
					result.put(new SlotKey(clazz, slotName), sad);
				} else {
					// TODO it's not a bean property, maybe we could generate a warning...
				}
			}
		}
		return result;
	}

	private static PrimitiveSchema tryToGetPrimitiveSchema(Class clazz) {
		PrimitiveSchema result = null;
		try {
			if (String.class.equals(clazz)) {
				result = (PrimitiveSchema)BasicOntology.getInstance().getSchema(BasicOntology.STRING);
			} else if (Boolean.class.equals(clazz)) {
				result = (PrimitiveSchema)BasicOntology.getInstance().getSchema(BasicOntology.BOOLEAN);
			} else if (float.class.equals(clazz) || double.class.equals(clazz) || Float.class.equals(clazz) || Double.class.equals(clazz)) {
				result = (PrimitiveSchema)BasicOntology.getInstance().getSchema(BasicOntology.FLOAT);
			} else if (int.class.equals(clazz) || long.class.equals(clazz) || Integer.class.equals(clazz) || Long.class.equals(clazz)) {
				result = (PrimitiveSchema)BasicOntology.getInstance().getSchema(BasicOntology.INTEGER);
			}

		} catch (OntologyException oe) {
			oe.printStackTrace();
		}
		return result;
	}

	private static String getAggregateSchemaName(Class clazz) {
		String result = null;
		if (jade.util.leap.List.class.isAssignableFrom(clazz) || java.util.List.class.isAssignableFrom(clazz)) {
			result = BasicOntology.SEQUENCE;
		} else if (jade.util.leap.Set.class.isAssignableFrom(clazz) || java.util.Set.class.isAssignableFrom(clazz)) {
			result = BasicOntology.SET;
		}
		return result;
	}

	private static AggregateSchema tryToGetAggregateSchema(Class clazz) {
		AggregateSchema result = null;

		try {
			String schemaName = getAggregateSchemaName(clazz);
			if (schemaName != null) {
				result = (AggregateSchema)BasicOntology.getInstance().getSchema(schemaName);
			}
		} catch (OntologyException oe) {
			// we should never arrive here
			oe.printStackTrace();
		}
		return result;
	}

	private TermSchema supplySchemaForClassFlat(Class clazz) throws OntologyException, BeanOntologyBuilderException {
		TermSchema ts;
		ObjectSchema os = tryToGetPrimitiveSchema(clazz);
		if (os == null) {
			os = ontology.getSchema(clazz);
			if (os == null) {
				if (Concept.class.isAssignableFrom(clazz)) {
					os = doAddFlatSchema(clazz);
				} else {
					throw new BeanOntologyBuilderException("cannot add a slot of class "+clazz.getName()+" since it does not implement Concept");
				}
			}
		}
		if (os != null && os instanceof TermSchema) {
			ts = (TermSchema)os;
		} else {
			throw new BeanOntologyBuilderException("cannot add a slot of class "+clazz.getName()+" since it does not extend TermSchema");
		}
		return ts;
	}

	private TermSchema supplySchemaForClassRecursive(Class clazz) throws OntologyException, BeanOntologyBuilderException {
		TermSchema ts;
		ObjectSchema os = tryToGetPrimitiveSchema(clazz);
		if (os == null) {
			os = ontology.getSchema(clazz);
			if (os == null) {
				if (Concept.class.isAssignableFrom(clazz)) {
					ts = doAddHierarchicalSchema(clazz);
				} else {
					throw new BeanOntologyBuilderException("cannot add a slot of class "+clazz.getName()+" since it does not implement Concept");
				}
			}
		}
		if (os != null && os instanceof TermSchema) {
			ts = (TermSchema)os;
		} else {
			throw new BeanOntologyBuilderException("cannot add a slot of class "+clazz.getName()+" since it does not extend TermSchema");
		}
		return ts;
	}

	private ConceptSchema doAddFlatSchema(Class clazz) throws BeanOntologyBuilderException {
		Map<SlotKey, SlotAccessData> slotAccessorsMap = buildAccessorsMap(clazz, clazz.getMethods());
		String slotName = null;
		SlotAccessData sad;

		String schemaName = getSchemaNameFromClass(clazz);
		if (logger.isLoggable(Logger.FINE)) {
			logger.log(Logger.FINE, "building concept "+schemaName);
		}
		ConceptSchema schema;
		boolean isAction = AgentAction.class.isAssignableFrom(clazz); 
		if (isAction) {
			schema = new AgentActionSchema(schemaName);
		} else {
			schema = new ConceptSchema(schemaName);
		}

		try {
			for (Entry<SlotKey, SlotAccessData> entry: slotAccessorsMap.entrySet()) {
				slotName = entry.getKey().slotName;
				sad = entry.getValue();
				if (logger.isLoggable(Logger.FINE)) {
					logger.log(Logger.FINE, "concept "+schemaName+": adding slot "+slotName);
				}
				if (!sad.aggregate) {
					TermSchema ts = supplySchemaForClassFlat(sad.type);
					schema.add(slotName, ts, sad.mandatory ? ObjectSchema.MANDATORY : ObjectSchema.OPTIONAL);
				} else {
					TermSchema ats = null;
					if (sad.aggregateClass != null) {
						// try to get a schema for the contained type
						ats = supplySchemaForClassFlat(sad.aggregateClass);
					}
					if (ats == null) {
						schema.add(slotName, tryToGetAggregateSchema(sad.type), sad.cardMin > 0 ? ObjectSchema.MANDATORY : ObjectSchema.OPTIONAL);
					} else {
						schema.add(slotName, ats, sad.cardMin, sad.cardMax, getAggregateSchemaName(sad.type));
					}
				}
			}
			introspector.addAccessors(slotAccessorsMap);
			if (isAction) {
				Annotation annotation;
				if ((annotation = clazz.getAnnotation(Result.class)) != null) {
					TermSchema ts = supplySchemaForClassFlat(((Result)annotation).type());
					((AgentActionSchema)schema).setResult(ts);
				} else if ((annotation = clazz.getAnnotation(AggregateResult.class)) != null) {
					AggregateResult ar = (AggregateResult)annotation;
					TermSchema ts = supplySchemaForClassFlat(ar.type());
					((AgentActionSchema)schema).setResult(ts, ar.cardMin(), ar.cardMax());
				}
			}
			ontology.add(schema, clazz);
		} catch (OntologyException e) {
			throw new BeanOntologyBuilderException("error getting schema for slot "+slotName);
		}
		return schema;
	}

	private ConceptSchema doAddHierarchicalSchema(Class clazz) throws BeanOntologyBuilderException {
		Class superClazz = clazz.getSuperclass();
		ConceptSchema superSchema = null;
		if (superClazz != null) {
			if (Concept.class.isAssignableFrom(superClazz)) {
				int scms = superClazz.getModifiers();
				if (!Modifier.isAbstract(scms) && !Modifier.isInterface(scms) && !Modifier.isPrivate(scms)) {
					doAddHierarchicalSchema(superClazz);
					try {
						superSchema = (ConceptSchema)ontology.getSchema(superClazz);
					} catch (OntologyException oe) {
						throw new BeanOntologyBuilderException("error getting schema for superclass "+superClazz);
					}
				}
			}
		}

		Method[] declaredMethods = clazz.getDeclaredMethods();
		List<Method> publicDeclaredMethodsList = new ArrayList<Method>();
		int modifiers;
		for (Method m: declaredMethods) {
			modifiers = m.getModifiers();
			if (Modifier.isPublic(modifiers) && !Modifier.isStatic(modifiers) && !Modifier.isAbstract(modifiers)) {
				publicDeclaredMethodsList.add(m);
			}
		}

		Method[] publicDeclaredMethods = new Method[publicDeclaredMethodsList.size()];
		for (int i = 0; i < publicDeclaredMethods.length; i++) {
			publicDeclaredMethods[i] = publicDeclaredMethodsList.get(i);
		}
		Map<SlotKey, SlotAccessData> slotAccessorsMap = buildAccessorsMap(clazz, publicDeclaredMethods);
		String slotName = null;
		SlotAccessData sad;

		String schemaName = getSchemaNameFromClass(clazz);
		if (logger.isLoggable(Logger.FINE)) {
			logger.log(Logger.FINE, "building concept "+schemaName);
		}
		ConceptSchema schema;
		boolean isAction = AgentAction.class.isAssignableFrom(clazz); 
		if (isAction) {
			schema = new AgentActionSchema(schemaName);
		} else {
			schema = new ConceptSchema(schemaName);
		}
		if (superSchema != null) {
			if (logger.isLoggable(Logger.FINE)) {
				logger.log(Logger.FINE, "adding superschema from class "+superClazz);
			}
			schema.addSuperSchema(superSchema);
		}
		try {
			for (Entry<SlotKey, SlotAccessData> entry: slotAccessorsMap.entrySet()) {
				slotName = entry.getKey().slotName;
				sad = entry.getValue();
				if (logger.isLoggable(Logger.FINE)) {
					logger.log(Logger.FINE, "concept "+schemaName+": adding slot "+slotName);
				}
				if (!sad.aggregate) {
					TermSchema ts = supplySchemaForClassRecursive(sad.type);
					schema.add(slotName, ts, sad.mandatory ? ObjectSchema.MANDATORY : ObjectSchema.OPTIONAL);
				} else {
					TermSchema ats = null;
					if (sad.aggregateClass != null) {
						// try to get a schema for the contained type
						ats = supplySchemaForClassRecursive(sad.aggregateClass);
					}
					if (ats == null) {
						schema.add(slotName, tryToGetAggregateSchema(sad.type), sad.cardMin > 0 ? ObjectSchema.MANDATORY : ObjectSchema.OPTIONAL);
					} else {
						schema.add(slotName, ats, sad.cardMin, sad.cardMax, getAggregateSchemaName(sad.type));
					}
				}
			}
			introspector.addAccessors(slotAccessorsMap);
			if (isAction) {
				Annotation annotation;
				if ((annotation = clazz.getAnnotation(Result.class)) != null) {
					TermSchema ts = supplySchemaForClassRecursive(((Result)annotation).type());
					((AgentActionSchema)schema).setResult(ts);
				} else if ((annotation = clazz.getAnnotation(AggregateResult.class)) != null) {
					AggregateResult ar = (AggregateResult)annotation;
					TermSchema ts = supplySchemaForClassRecursive(ar.type());
					((AgentActionSchema)schema).setResult(ts, ar.cardMin(), ar.cardMax());
				}
			}
			ontology.add(schema, clazz);
		} catch (OntologyException e) {
			throw new BeanOntologyBuilderException("error getting schema for slot "+slotName);
		}
		return schema;
	}

	private void doAddSchemas(Class clazz, boolean buildHierarchy) throws BeanOntologyBuilderException {
		if (!Concept.class.isAssignableFrom(clazz)) {
			throw new BeanOntologyBuilderException("class "+clazz.getName()+" must implement "+Concept.class);
		}
		if (buildHierarchy) {
			doAddHierarchicalSchema(clazz);
		} else {
			doAddFlatSchema(clazz);
		}
	}

	/*
	 * public interface
	 */
	public void addSchemas(Class clazz, boolean buildHierarchy) throws BeanOntologyBuilderException {
		doAddSchemas(clazz, buildHierarchy);
	}

	public void add(Class clazz) throws BeanOntologyBuilderException {
		doAddSchemas(clazz, false);
	}
}
