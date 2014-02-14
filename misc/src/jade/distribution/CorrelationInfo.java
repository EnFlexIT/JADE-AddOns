package jade.distribution;

public class CorrelationInfo {
	private Object key;
	private boolean last;
	
	public CorrelationInfo(Object key) {
		this(key, false);
	}
	
	public CorrelationInfo(Object key, boolean last) {
		this.key = key;
		this.last = last;
	}
	
	public Object getKey() {
		return key;
	}
	
	public boolean isLast() {
		return last;
	}
}
