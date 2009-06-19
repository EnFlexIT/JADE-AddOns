package jade.osgi.service.runtime.internal;

public class AgentInfo {
	String name;
	String className;
	Object[] args;
	boolean updated;

	public AgentInfo(String name, String className, Object[] args) {
		this.name = name;
		this.className = className;
		this.args = args;
		this.updated = false;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public Object[] getArgs() {
		return args;
	}

	public void setArgs(Object[] args) {
		this.args = args;
	}

	public boolean isUpdated() {
		return updated;
	}

	public void setUpdated(boolean updated) {
		this.updated = updated;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("(");
		sb.append("name: " + name);
		sb.append(" updated: " + updated);
		sb.append(")");
		return sb.toString();
	}
	
	
	
}
