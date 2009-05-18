package jade.osgi.service.runtime.internal;

public class AgentInfo {
	String name;
	String className;
	Object[] args;
	
	
	public AgentInfo(String name, String className, Object[] args) {
		super();
		this.name = name;
		this.className = className;
		this.args = args;
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
}
