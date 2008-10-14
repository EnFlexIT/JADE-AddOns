package content.onto.bob.beans;


public class ClassOne extends ClassZero implements ExtendedConcept {
	private static final long serialVersionUID = 1L;

	private String fieldOneZero;
	private String fieldOneOne;
	private String fieldOneTwo;

	public String getFieldOneZero() {
		return fieldOneZero;
	}
	public void setFieldOneZero(String fieldOneZero) {
		this.fieldOneZero = fieldOneZero;
	}
	public String getFieldOneOne() {
		return fieldOneOne;
	}
	public void setFieldOneOne(String fieldOneOne) {
		this.fieldOneOne = fieldOneOne;
	}
	public String getFieldOneTwo() {
		return fieldOneTwo;
	}
	public void setFieldOneTwo(String fieldOneTwo) {
		this.fieldOneTwo = fieldOneTwo;
	}

	@Override
	protected String innerToString() {
		StringBuilder sb = new StringBuilder();
		sb.append(super.innerToString());
		sb.append(" fieldOneZero=");
		sb.append(fieldOneZero);
		sb.append(" fieldOneOne=");
		sb.append(fieldOneOne);
		sb.append(" fieldOneTwo=");
		sb.append(fieldOneTwo);
		return sb.toString();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("ClassOne {");
		sb.append(innerToString());
		sb.append('}');
		return sb.toString();
	}
}
