/**
 * 
 */
package jade.content.onto;

import java.lang.reflect.Method;

class SlotAccessData {
	Class type;
	Method getter;
	Method setter;
	boolean aggregate;
	boolean mandatory;
	Class aggregateClass;
	int cardMin;
	int cardMax;

	SlotAccessData(Class type, Method getter, Method setter, boolean mandatory, Class aggregateClass, int cardMin, int cardMax) {
		this.type = type;
		this.getter = getter;
		this.setter = setter;
		aggregate = java.util.Collection.class.isAssignableFrom(type) || jade.util.leap.Collection.class.isAssignableFrom(type);
		this.mandatory = mandatory;
		this.aggregateClass = aggregateClass;
		this.cardMin = cardMin;
		this.cardMax = cardMax;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("SlotAccessData {");
		sb.append("type=");
		sb.append(type.getName());
		sb.append(" getter=");
		sb.append(getter.getName());
		sb.append(" setter=");
		sb.append(setter.getName());
		sb.append(" aggregate=");
		sb.append(aggregate);
		sb.append(" aggregateClass=");
		sb.append(aggregateClass != null ? aggregateClass.getName() : null);
		sb.append(" mandatory=");
		sb.append(mandatory);
		sb.append(" cardMin=");
		sb.append(cardMin);
		sb.append(" cardMax=");
		sb.append(cardMax);
		sb.append('}');
		return sb.toString();
	}
}