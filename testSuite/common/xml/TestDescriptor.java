package test.common.xml;

import sun.net.TelnetInputStream;

/**
 * @author Elisabetta Cortese - TiLab
 *
 */

// FOR EACH TEST
public class TestDescriptor {
	
	private String name = "";
	private boolean skip = false;
	private String testClass = "";
	private String what = "";
	private String how = "";
	private String passedWhen = "";

	public TestDescriptor(){
	}
		
	public TestDescriptor(String n, boolean s, String c, String w, String h, String p){
		name = n;
		skip = s;
		testClass = c;
		what = w;
		how = h;
		passedWhen = p;
	}
	
	public String getName(){
		return name;
	}

	public boolean getSkip(){
		return skip;
	}
	
	public String getWhat(){
		return what;
	}

	public String getTestClassName(){
		return testClass;
	}
	
	public String getHow(){
		return how;
	}
	
	public String getPassedWhen(){
		return passedWhen;
	}

	public void setName(String n){
		name =n;
	}

	public void setSkip(String s){
		if(s.equalsIgnoreCase("true"))
			skip = true;
		else
			skip = false;
	}

	public void setTestClass(String c){
		testClass = c;
	}
	
	public void setWhat(String w){
		what = w;
	}
	
	public void setHow(String h){
		how = h;
	}
	
	public void setPassedWhen(String p){
		passedWhen = p;
	}

}