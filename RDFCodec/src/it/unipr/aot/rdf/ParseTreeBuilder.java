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

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.io.IOException;
import org.w3c.rdf.model.Literal;
import org.w3c.rdf.model.ModelException;
import org.w3c.rdf.model.Statement;

/**
  Create a tree of resources and properties.
  It is possible to keep information about the hierarchy if and only if
  there are not nesting references.
  Example:

    <CoMMA:Researcher rdf:about="http://www.inria.fr/Alain.Giboin">
    	<CoMMA:FamilyName>GIBOIN</CoMMA:FamilyName>
    	<CoMMA:FirstName>Alain</CoMMA:FirstName>
    	<CoMMA:HasForActivity><CoMMA:Research/></CoMMA:HasForActivity>
    	<CoMMA:HasForActivity><CoMMA:Education/></CoMMA:HasForActivity>

    	<CoMMA:IsInterestedBy><CoMMA:KnowledgeEngineeringTopic/></CoMMA:IsInterestedBy>
    	<CoMMA:IsInterestedBy><CoMMA:HCITopic/></CoMMA:IsInterestedBy>
    	<CoMMA:IsInterestedBy><CoMMA:CognitiveSciencesTopic/></CoMMA:IsInterestedBy>

    </CoMMA:Researcher>

    <CoMMA:Employee rdf:about="http://www.inria.fr/Alain.Giboin">
    	<CoMMA:EmployedBy><CoMMA:LocalOrganizationGroup rdf:about="http://www-sop.inria.fr/" /></CoMMA:EmployedBy>
      <CoMMA:EmploymentContract><CoMMA:FullTime/></CoMMA:EmploymentContract>
    </CoMMA:Employee>


  @author Paola Turci - Universita` di Parma
 */
 class ParseTreeBuilder {
  /**
    Constructor
   */
  ParseTreeBuilder(){
    triples=new HashMap();
    idsTable=new HashMap();
  }

  /**
    This method is invoked every time a new statement is generated.
    The statements are collected in a tree;
    The nodes of the tree are ParseTrees or Leafs and represent
    RDF resources or properties
   */
  void addStatement (Statement s){
    try{
	  //verifies if the subject of the statement is a new AbsObject
	  if (newParseTree(s.subject().getURI())){
        ParseTree n = new ParseTree();
        idsTable.put(s.subject().getURI(),new Integer(0));   //first time for this ID
	    triples.put(nodeID(s.subject().getURI()),n);
      }else{
		ParseTree node =(ParseTree)triples.get(nodeID(s.subject().getURI()));
		if(s.predicate().getLabel().equals(TYPE) && node.getName()!=null){ //more than one reference to the same resource
          ParseTree n = new ParseTree();
          Integer index = (Integer) idsTable.get(s.subject().getURI());
          int i = index.intValue();
          i++;   //increments the reference
          idsTable.put(s.subject().getURI(),new Integer(i)); //from now to onwards, it is the new reference to consider ????
                                                             //it is necessary for the hierarchy information
 	      triples.put(nodeID(s.subject().getURI()),n);
	    }
	  }

      //gets the reference to the node which represents the resource
      ParseTree node =(ParseTree)triples.get(nodeID(s.subject().getURI()));

      //adds the new predicate to the node list,
      //which represents the subject of the statement,
      addNode(s,node);

      //triples.put(nodeID(s.subject().getURI()),node);

    }catch(ModelException me){
	   me.printStackTrace(); //FIXME
    }
  }

  //gives the name of the subject (without the namespace prefix).
  private String nodeID(String s)throws ModelException{
     return s + "_" +((Integer)idsTable.get(s)).toString();
  }

  //tests if the resource, identified by the name, has already a node associated
  private boolean newParseTree(String s)throws ModelException{
	return (idsTable.get(s) == null);
  }

  //adds a new node to the tree associated with an AbsObject
  private void addNode(Statement s, ParseTree parseTree)throws ModelException{

    if (s.predicate().getLabel().equals(TYPE)){
       //the name of the AbsObject
	   parseTree.setName(s.object().getLabel());
	   if (isURI(s.subject().getLabel()))
	      parseTree.setAbout(s.subject().getLabel());
    }else if (s.predicate().getLabel().equals(ID)){
       parseTree.setID(s.object().getLabel());
	}else{
      if((s.object() instanceof Literal) ||
         (isURI(s.object().getLabel()) && newParseTree(s.object().getLabel()))){  //it is not an URI of an existing node
        Leaf n = new Leaf();

        //it is supposed to be a string
        n.setValue((String)(s.object().getLabel())); //Literal or URI
        n.setName(s.predicate().getLabel());
        parseTree.addNode(n); //reference to a node

      }else{ //is an AbsObject

        ParseTree n;
        if(newParseTree(s.object().getLabel())){ //if it is new
          n = new ParseTree();
          idsTable.put(s.object().getLabel(),new Integer(0));   //first time for this ID
          n.setName(s.object().getLabel());
          triples.put(nodeID(s.object().getLabel()),n);
	    }
        else
	      n = (ParseTree)triples.get(nodeID(s.object().getLabel()));

          n.setRoot(false);
/*
        if(!parseTree.exist(s.predicate().getLabel())){
          ParseTree newNode = new ParseTree();
          newNode.setName(s.predicate().getLabel());
          newNode.addNode(n);
          newNode.setRoot(false);
          parseTree.addNode(newNode); //reference to a node
	    }else{
		  ParseTree predicateNode = (ParseTree) parseTree.getNode(s.predicate().getLabel());
		  predicateNode.addNode(n);
	    }
*/
        ParseTree newNode = new ParseTree();
        newNode.setName(s.predicate().getLabel());
        newNode.addNode(n);
        newNode.setRoot(false);
        parseTree.addNode(newNode); //reference to a node


	  }
    }
  }

  private boolean isURI(String s){//FIXME it is better to use FactoryNode to verify if an id generated by SiRPAC or an URI
    int i = s.indexOf("#genid");
    if (i < 0)
        i = s.indexOf(":genid");
    if (i < 0) return true; //it is an URI
    else return false;

   // return s.startsWith("http://",0);
  }

  Map getAllTriples(){
	 return triples;
  }

  void toStream(java.io.DataOutputStream stream)throws IOException{
	//the list is supposed to have only one element
	Iterator i=triples.values().iterator();
	((Node)i.next()).toStream(stream);
  }

  private Map triples;   //place where SiRPAC generated statements are memorized
  private Map idsTable;  /* Useful when more than one statement refer to the same resource as in:
                          <CoMMA:Researcher rdf:about="http://www.inria.fr/Alain.Giboin">
                               . . .
                          </CoMMA:Researcher>
                          <CoMMA:Employee rdf:about="http://www.inria.fr/Alain.Giboin">
                              . . .
                          </CoMMA:Employee>

                          with this map it is possible to keep trace of the hierarchy */

  private static String TYPE = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";
  private static String ID   = "http://www.w3.org/1999/02/22-rdf-syntax-ns#id";
}