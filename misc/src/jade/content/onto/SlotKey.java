/**
 * 
 */
package jade.content.onto;

class SlotKey {
	Class clazz;
	String slotName;
	private int hashcode;

	SlotKey(Class clazz, String slotName) {
		this.clazz = clazz;
		this.slotName = slotName;
		calcHashcode();
	}

	private void calcHashcode() {
		hashcode = 37;
		if (clazz != null) {
			hashcode ^= clazz.hashCode();
		}
		if (slotName != null) {
			hashcode ^= slotName.hashCode();
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof SlotKey)) {
			return false;
		}
		SlotKey other = (SlotKey)obj;
		if (clazz != other.clazz) {
			return false;
		}
		if (slotName != null) {
			return slotName.equals(other.slotName);
		} else {
			return other.slotName == null;
		}
	}

	@Override
	public int hashCode() {
		return hashcode;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("SlotKey {clazz=");
		sb.append(clazz != null ? clazz.getSimpleName() : null);
		sb.append(" slotName=");
		sb.append(slotName);
		return sb.toString();
	}
}