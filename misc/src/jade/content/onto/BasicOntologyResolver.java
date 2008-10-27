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

package jade.content.onto;

import jade.content.schema.ObjectSchema;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

class BasicOntologyResolver {

	private Map<Class, ObjectSchema> basicSchemas;
	private static BasicOntologyResolver INSTANCE;

	private static class ClassComparator implements Comparator<Class> {
		public int compare(Class o1, Class o2) {
			if (o1 == o2) {
				return 0;
			} else {
				return o1.getName().compareTo(o2.getName());
			}
		}
	}

	private BasicOntologyResolver() {
		basicSchemas = new TreeMap<Class, ObjectSchema>(new ClassComparator());

		try {
			Ontology bo = BasicOntology.getInstance();

			basicSchemas.put(java.lang.String.class, bo.getSchema(BasicOntology.STRING));
			basicSchemas.put(java.lang.Boolean.class, bo.getSchema(BasicOntology.BOOLEAN));
			basicSchemas.put(int.class, bo.getSchema(BasicOntology.INTEGER));
			basicSchemas.put(long.class, bo.getSchema(BasicOntology.INTEGER));
			basicSchemas.put(java.lang.Integer.class, bo.getSchema(BasicOntology.INTEGER));
			basicSchemas.put(java.lang.Long.class, bo.getSchema(BasicOntology.INTEGER));
			basicSchemas.put(float.class, bo.getSchema(BasicOntology.FLOAT));
			basicSchemas.put(double.class, bo.getSchema(BasicOntology.FLOAT));
			basicSchemas.put(java.lang.Float.class, bo.getSchema(BasicOntology.FLOAT));
			basicSchemas.put(java.lang.Double.class, bo.getSchema(BasicOntology.FLOAT));
			basicSchemas.put(java.util.Date.class, bo.getSchema(BasicOntology.DATE));
			basicSchemas.put(jade.core.AID.class, bo.getSchema(BasicOntology.AID));
			basicSchemas.put(byte[].class, bo.getSchema(BasicOntology.BYTE_SEQUENCE));
			basicSchemas.put(jade.content.onto.basic.TrueProposition.class, bo.getSchema(BasicOntology.TRUE_PROPOSITION));
			basicSchemas.put(jade.content.onto.basic.FalseProposition.class, bo.getSchema(BasicOntology.FALSE_PROPOSITION));
			basicSchemas.put(jade.content.onto.basic.Done.class, bo.getSchema(BasicOntology.DONE));
			basicSchemas.put(jade.content.onto.basic.Result.class, bo.getSchema(BasicOntology.RESULT));
			basicSchemas.put(jade.content.onto.basic.Equals.class, bo.getSchema(BasicOntology.EQUALS));
			basicSchemas.put(jade.lang.acl.ACLMessage.class, bo.getSchema(BasicOntology.ACLMSG));
			basicSchemas.put(jade.content.onto.basic.Action.class, bo.getSchema(BasicOntology.ACTION));

		} catch (OntologyException oe) {
			// we should never get here
			oe.printStackTrace();
		}
	}

	synchronized static BasicOntologyResolver getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new BasicOntologyResolver();
		}
		return INSTANCE;
	}

	ObjectSchema searchSchema(Class clazz) {
		return basicSchemas.get(clazz);
	}
}
