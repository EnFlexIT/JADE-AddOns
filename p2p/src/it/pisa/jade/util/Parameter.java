/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2000 CSELT S.p.A. 

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
package it.pisa.jade.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

/**
 * Simple class that manage the application's parameter, and if its are
 * corrupted the class set the default value.
 * 
 * @author Domenico Trimboli 
 * 
 */
public class Parameter {
	final static int INTEGER = 1;

	final static int LONG = 2;

	final static int BOOLEAN = 3;

	final static int STRING = 4;

	final static int IP = 5;

	public static final String YES = "yes";

	public static final String NO = "no";

	private static Parameter instance;

	private static final String PARAMETER_FILE = "Parameter";

	/**
	 * Return the register property, if the property is mandatory the method
	 * return always a values ele the property is optional may be null.
	 * 
	 * @param key key of property
	 * @return always value if mandatory, value or null if optional 
	 */
	public static String get(Values key) {
		if (instance == null)
			load();
		/*
		 * String property=instance.getProperties().getProperty(key.name());
		 * return (property!=null&&property.equals(""))?null:property;
		 */
		return instance.getProperties().getProperty(key.name());
	}

	public static int getInt(Values key) {
		if (instance == null)
			load();
		return Integer.parseInt(instance.getProperties().getProperty(key.name()));
	}

	public static void load() {
		instance = new Parameter();
	}

	public static void set(Values key, String value) {
		if (instance == null)
			load();
		instance.properties.put(key.name(), value);
		instance.store();
	}

	private Properties properties;

	public Parameter() {
		properties = new Properties();
		File f = new File(PARAMETER_FILE + ".xml");
		if (f.exists()) {
			try {
				properties.loadFromXML(new FileInputStream(f));
				if (!check()) {
					store();
				}
			} catch (IOException e) {
				WrapperErrori.wrap("try store properties", e);
				addDefaultValue();
				store();
			}
		} else {
			try {
				f.createNewFile();
				addDefaultValue();
				store();
			} catch (IOException e) {
				WrapperErrori.wrap("", e);
			}

		}

	}

	private void addDefaultValue() {
		Values[] values = Values.values();
		for (int i = 0; i < values.length; i++) {
			if (values[i].isMandatory())
				properties.put(values[i].name(), values[i].getDefaultValue());
		}
	}

	private boolean check() {
		Values[] values = Values.values();
		boolean ok = true;
		for (int i = 0; i < values.length; i++) {
			int type = values[i].getType();
			String property = properties.getProperty(values[i].name());
			if (!values[i].isMandatory() && property == null)
				continue;
			else
				switch (type) {
				case INTEGER:
					try {
						Integer.parseInt(property);
					} catch (Exception e) {
						properties.put(values[i].name(), values[i]
								.getDefaultValue());
						ok = false;
					}
					break;
				case LONG:
					try {
						Long.parseLong(property);
					} catch (Exception e) {
						properties.put(values[i].name(), values[i]
								.getDefaultValue());
						ok = false;
					}
					break;
				case BOOLEAN:
					if (property == null
							|| (!property.equals(YES) && !property.equals(NO))) {
						properties.put(values[i].name(), values[i]
								.getDefaultValue());
						ok = false;
					}
					break;
				case IP:
					if (property == null || property.equals("")) {
						properties.put(values[i].name(), values[i]
								.getDefaultValue());
						ok = false;
					} else {
						try {
							InetAddress.getByName(property);
						} catch (UnknownHostException e) {
							properties.put(values[i].name(), values[i]
									.getDefaultValue());
						}
					}
					break;
				case STRING:
					if (property == null || property.equals("")) {
						properties.put(values[i].name(), values[i]
								.getDefaultValue());
						ok = false;
					}
					break;

				default:
					break;
				}
		}
		return ok;
	}

	private Properties getProperties() {
		return properties;
	}

	public void store() {
		File f = new File(PARAMETER_FILE + ".xml");
		try {
			properties.storeToXML(new FileOutputStream(f), "");
		} catch (FileNotFoundException e) {
			WrapperErrori.wrap("", e);
		} catch (IOException e) {
			WrapperErrori.wrap("", e);
		}
	}

	public static long getLong(Values key) {
		if (instance == null)
			load();
		return Long.parseLong(instance.getProperties().getProperty(key.name()));
	}

}
