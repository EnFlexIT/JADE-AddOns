/**
 * ***************************************************************
 * JADE - Java Agent DEvelopment Framework is a framework to develop
 * multi-agent systems in compliance with the FIPA specifications.
 * Copyright (C) 2000 CSELT S.p.A.
 *
 * GNU Lesser General Public License
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation,
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA  02111-1307, USA.
 * **************************************************************
 */
package it.unipr.aot.rdf;

import java.io.DataOutputStream;
import java.io.IOException;

  /**
    Represents a value of a property

    @author Paola Turci - Universita` di Parma
   */
  class Leaf extends Node{

    /**
      Sets the value of the property
     */
    void setValue(String s){
	  value=s;
	}

    /**
      Gets the value of the property
     */
    String getValue(){
	  return value;
	}

    /* it is here just for debugging purposes */
    public String toString(){
	  StringBuffer s=new StringBuffer();

	  s.append("\npredicate :"+ getName());
      s.append("\nliteral :"+ getValue());
	  return s.toString();
	}

    void toStream(DataOutputStream stream) throws IOException{
  	  try{
  	    System.out.println("\n LEAF name:   "+getName());
  	    System.out.println("\n LEAF value:  "+getValue());
  	    stream.writeUTF(getName());
  	    stream.writeUTF(getValue());
      }catch (IOException ioe) {
	     throw ioe;
      }
    }

	/* it is here just for debugging purposes */
    protected void dump(int indent) {
      for (int i = 0; i < indent; i++) {
         System.out.print("  ");
      }

      System.out.println(getName()+ "   " + getValue());

    }

    public void dump() {
      dump(0);
    }

	private String value;
  }
