package content.onto.bob.beans;

import jade.content.Concept;

public class ClassZero implements Concept, Comparable<ClassZero> {
	private static final long serialVersionUID = 1L;

	private int fieldZeroZero;
	private int fieldZeroOne;

	public ClassZero() {
		fieldZeroZero = -19;
		fieldZeroOne = -79;
	}

	public ClassZero(int fieldZeroOne, int fieldZeroZero) {
		this.fieldZeroOne = fieldZeroOne;
		this.fieldZeroZero = fieldZeroZero;
	}

	public int getFieldZeroZero() {
		return fieldZeroZero;
	}

	public void setFieldZeroZero(int fieldZeroZero) {
		this.fieldZeroZero = fieldZeroZero;
	}

	public int getFieldZeroOne() {
		return fieldZeroOne;
	}

	public void setFieldZeroOne(int fieldZeroOne) {
		this.fieldZeroOne = fieldZeroOne;
	}

	/* needs to be Comparable in order to be put into a jade Set */
	public int compareTo(ClassZero o) {
		int result = fieldZeroZero-o.fieldZeroZero;
		if (result == 0) {
			result = fieldZeroOne-o.fieldZeroOne;
		}
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		return compareTo((ClassZero)obj) == 0;
	}

	@Override
	public int hashCode() {
		return fieldZeroZero ^ fieldZeroOne;
	}

	protected String innerToString() {
		StringBuilder sb = new StringBuilder();
		sb.append("fieldZeroZero=");
		sb.append(fieldZeroZero);
		sb.append(" fieldZeroOne=");
		sb.append(fieldZeroOne);
		return sb.toString();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("ClassZero {");
		sb.append(innerToString());
		sb.append('}');
		return sb.toString();
	}
}
