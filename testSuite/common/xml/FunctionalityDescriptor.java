package test.common.xml;

import jade.util.leap.Serializable;

/**
 * @author Elisabetta Cortese - TiLab
 *
 */
public class FunctionalityDescriptor  implements Serializable {

	private String nameT = "";
	private String testerClassName ="";
	private String testsListFile;
	private String description ="";
	private boolean skip = false;
	
	/**
	 * Constructor for FunctionalityDescriptor.
	 */
	public FunctionalityDescriptor(String n, boolean s, String c, String d) {
		nameT = n;
		skip = s;
		testerClassName = c;
		description = d;
	}

	public FunctionalityDescriptor() {
	}
	
	public String getName(){
		return nameT;
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
		nameT = n;
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


}
