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
import java.util.List;
import java.util.ArrayList;

/**
  Represents a tree connected to an AbsConcept.

  @author Paola Turci - Universita` di Parma
 */
class ParseTree extends Node{

  ParseTree(){
    root = true;
    resourceId=null;
    resourceAbout=null;
    nodes = new ArrayList();//initial capacity?
  }

  /**
    True if the node is a root node.
   */
  boolean isRoot(){
    return root;
  }

  /**
    Sets the node kind
   */
  void setRoot(boolean b){
    root=b;
  }


  /**
    Sets the URI of the "resource"
   */
  void setID(String s){
    resourceId=s;
  }

  /**
    Gets the URI of the "resource"
   */
  String getID(){
    return resourceId;
  }

  /**
    Sets the reference of the "resource"
   */
  void setAbout(String s){
    resourceAbout=s;
  }


  /**
    Gets the reference of the "resource"
   */
  String getAbout(){
    return resourceAbout;
  }

  /**
    Adds a node to the tree
   */
  void addNode(Node n){
    nodes.add(n);
  }

  /**
    Removes node "i" from the tree
   */
  void removeNode(int i){
	nodes.remove(i);
  }


  /**
    Gets the 'i' node
   */
  Node getNode(int i){
    return (Node)nodes.get(i);
  }

  /**
    Gets the node with name "name"
   */
  Node getNode(String name){
    for (int i=0; i<size(); i++)
      if (getNode(i).getName().equals(name))
         return getNode(i);

    return null;
  }

  /**
    Return true if the node with the name "name" is a sub-node
   */
  boolean exist(String name){
    for (int i=0; i<size(); i++)
      if (getNode(i).getName().equals(name))
         return true;

    return false;
  }

  /**
    Returns the number of nodes
   */
  int size(){
	return nodes.size();
  }

  /* it is here just for debugging purposes */
  public String toString(){
    StringBuffer s = new StringBuffer();
    s.append("\nNode: " + getName());
	for (int i=0; i<size(); i++)
      s.append("\nNode: " + getName() + "  child " + i + " " + ((Node)getNode(i)).toString());

    return s.toString();
  }

  void toStream(DataOutputStream stream) throws IOException{
    try{
	  stream.writeUTF(getName());
	  for (int i=0; i<size(); i++)
        ((Node)getNode(i)).toStream(stream);
    }catch (IOException ioe) {
	   throw ioe;
    }
  }

  /* it is here just for debugging purposes */
  protected void dump(int indent) {
    for (int i = 0; i < indent; i++) {
        System.out.print("  ");
    }

    System.out.println(getName());
    if(getID()!=null) System.out.println(getID());
    else if(getAbout()!=null) System.out.println(getAbout());

    for (int i = 0; i<size(); i++) {
       for (int j = 0; j < indent; j++) {
            System.out.print("  ");
       }

       Node n = (Node) nodes.get(i);
       n.dump(indent + 1);
    }
  }

  public void dump() {
    dump(0);
  }


  private boolean root;          //used to identify main AbsObject
  private String resourceId;     //Optional; URI of the resource
  private String resourceAbout;  //Optional; reference
  private List nodes;
}
