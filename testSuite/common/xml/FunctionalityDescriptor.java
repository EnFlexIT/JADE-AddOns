package test.common.xml;

import jade.util.leap.Serializable;

/**
 * @author Elisabetta Cortese - TiLab
 *
 */
public class FunctionalityDescriptor  implements Serializable {

	private String name = "";
	private String testerClassName ="";
	private String testsListFile;
	private String description ="";
	private boolean skip = false;
	
	/**
	 * Constructor for FunctionalityDescriptor.
	 */
	public FunctionalityDescriptor(String n, boolean s, String c, String d) {
		name = n;
		skip = s;
		testerClassName = c;
		description = d;
	}

	public FunctionalityDescriptor() {
	}
	
	public String getName(){
		return name;
	}

	public boolean getSkip(){
		return skip;
	}
	
	public String getTesterClassName(){
		return testerClassName;
	}

	public String getTestsListFile(){
		return testsListFile;
	}

	public String getDescription(){
		return description;
	}

	public void setName(String n){
		name = n;
	}

	public void setSkip(String s){
		if(s.equalsIgnoreCase("true"))
			skip = true;
		else
			skip = false;
	}
	
	public void setTesterClassName(String c){
		testerClassName = c;
	}

	public void setTestsListFile(String testsListFile){
		this.testsListFile = testsListFile;
	}


	public void setDescription(String d){
		description = d;
	}

	public String toString() {
		return name;
	}
	
	public boolean equals(Object obj) {
		if (obj instanceof FunctionalityDescriptor) {
			return name.equals(((FunctionalityDescriptor) obj).name);
		}
		else {
			return false;
		}
	}
	
	public int hashCode() {
		return name.hashCode();
	}
}
