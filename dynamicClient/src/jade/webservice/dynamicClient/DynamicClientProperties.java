/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2002 TILAB

GNU Lesser General Public License

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, 
version 2.1 of the License. 

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the
Free Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA  02111-1307, USA.
*****************************************************************/
package jade.webservice.dynamicClient;

public class DynamicClientProperties {

	private String packageName;
	private String tmpDir;
	private boolean noWrap;
	private boolean safeMode;
	private StringBuilder classPath;
	
	
	public DynamicClientProperties() {
		tmpDir = System.getProperty("java.io.tmpdir");
		noWrap = false;
		safeMode = true;
		packageName = null;
		classPath = null;
	}

	public String getPackageName() {
		return packageName;
	}
	
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	
	public String getTmpDir() {
		return tmpDir;
	}
	
	public void setTmpDir(String tmpDir) {
		this.tmpDir = tmpDir;
	}
	
	public boolean isNoWrap() {
		return noWrap;
	}
	
	public void setNoWrap(boolean noWrap) {
		this.noWrap = noWrap;
	}
	
	public boolean isSafeMode() {
		return safeMode;
	}
	
	public void setSafeMode(boolean safeMode) {
		this.safeMode = safeMode;
	}
	
	public StringBuilder getClassPath() {
		return classPath;
	}

	public void setClassPath(StringBuilder classPath) {
		this.classPath = classPath;
	}
}
