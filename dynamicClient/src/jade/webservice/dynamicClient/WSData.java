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

import jade.content.abs.AbsObject;
import jade.content.abs.AbsPrimitive;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class WSData {

	private Map<String, AbsObject> parameters = new HashMap<String, AbsObject>();
	private Map<String, AbsObject> headers = new HashMap<String, AbsObject>();

	public AbsObject getParameter(String name) {
		return parameters.get(name);
	}
	public String getParameterString(String name) {
		return ((AbsPrimitive)getParameter(name)).getString();
	}
	public boolean getParameterBoolean(String name) {
		return ((AbsPrimitive)getParameter(name)).getBoolean();
	}
	public int getParameterInteger(String name) {
		return ((AbsPrimitive)getParameter(name)).getInteger();
	}
	public long getParameterLong(String name) {
		return ((AbsPrimitive)getParameter(name)).getLong();
	}
	public float getParameterFloat(String name) {
		return ((AbsPrimitive)getParameter(name)).getFloat();
	}
	public double getParameterDouble(String name) {
		return ((AbsPrimitive)getParameter(name)).getDouble();
	}
	public Date getParameterDate(String name) {
		return ((AbsPrimitive)getParameter(name)).getDate();
	}
	public byte[] getParameterByteSequence(String name) {
		return ((AbsPrimitive)getParameter(name)).getByteSequence();
	}
	
	public void setParameter(String name, AbsObject abs) {
		this.parameters.put(name, abs);
	}
	public void setParameter(String name, String value) {
		setParameter(name, AbsPrimitive.wrap(value));
	}
	public void setParameter(String name, boolean value) {
		setParameter(name, AbsPrimitive.wrap(value));
	}
	public void setParameter(String name, int value) {
		setParameter(name, AbsPrimitive.wrap(value));
	}
	public void setParameter(String name, long value) {
		setParameter(name, AbsPrimitive.wrap(value));
	}
	public void setParameter(String name, float value) {
		setParameter(name, AbsPrimitive.wrap(value));
	}
	public void setParameter(String name, double value) {
		setParameter(name, AbsPrimitive.wrap(value));
	}
	public void setParameter(String name, Date value) {
		setParameter(name, AbsPrimitive.wrap(value));
	}
	public void setParameter(String name, byte[] value) {
		setParameter(name, AbsPrimitive.wrap(value));
	}
	
	public AbsObject getHeader(String name) {
		return headers.get(name);
	}
	public String getHeaderString(String name) {
		return ((AbsPrimitive)getHeader(name)).getString();
	}
	public boolean getHeaderBoolean(String name) {
		return ((AbsPrimitive)getHeader(name)).getBoolean();
	}
	public int getHeaderInteger(String name) {
		return ((AbsPrimitive)getHeader(name)).getInteger();
	}
	public long getHeaderLong(String name) {
		return ((AbsPrimitive)getHeader(name)).getLong();
	}
	public float getHeaderFloat(String name) {
		return ((AbsPrimitive)getHeader(name)).getFloat();
	}
	public double getHeaderDouble(String name) {
		return ((AbsPrimitive)getHeader(name)).getDouble();
	}
	public Date getHeaderDate(String name) {
		return ((AbsPrimitive)getHeader(name)).getDate();
	}
	public byte[] getHeaderByteSequence(String name) {
		return ((AbsPrimitive)getHeader(name)).getByteSequence();
	}
	
	public void setHeader(String name, AbsObject abs) {
		this.headers.put(name, abs);
	}
	public void setHeader(String name, String value) {
		setHeader(name, AbsPrimitive.wrap(value));
	}
	public void setHeader(String name, boolean value) {
		setHeader(name, AbsPrimitive.wrap(value));
	}
	public void setHeader(String name, int value) {
		setHeader(name, AbsPrimitive.wrap(value));
	}
	public void setHeader(String name, long value) {
		setHeader(name, AbsPrimitive.wrap(value));
	}
	public void setHeader(String name, float value) {
		setHeader(name, AbsPrimitive.wrap(value));
	}
	public void setHeader(String name, double value) {
		setHeader(name, AbsPrimitive.wrap(value));
	}
	public void setHeader(String name, Date value) {
		setHeader(name, AbsPrimitive.wrap(value));
	}
	public void setHeader(String name, byte[] value) {
		setHeader(name, AbsPrimitive.wrap(value));
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("\t\tParameters\n");
		Iterator<Entry<String, AbsObject>> itp = parameters.entrySet().iterator();
		while(itp.hasNext()){
			Entry<String, AbsObject> entry = itp.next();
			sb.append("\t\t\t"+entry.getKey()+"="+entry.getValue()+"\n");
		}

		sb.append("\t\tHeaders\n");
		Iterator<Entry<String, AbsObject>> ith = headers.entrySet().iterator();
		while(ith.hasNext()){
			Entry<String, AbsObject> entry = ith.next();
			sb.append("\t\t\t"+entry.getKey()+"="+entry.getValue()+"\n");
		}
		
		return sb.toString();
	}
}
