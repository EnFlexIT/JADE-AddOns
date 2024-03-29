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

import java.util.*;
import java.io.*;

import org.w3c.rdf.model.*;
import org.w3c.rdf.syntax.*;
import org.w3c.rdf.implementation.model.NodeFactoryImpl;
import org.w3c.rdf.util.RDFUtil;
import org.w3c.rdf.util.RDFFactory;
import org.w3c.rdf.util.RDFFactoryImpl;

//import org.w3c.rdf.implementation.syntax.sirpac.SiRS;
//import org.w3c.rdf.implementation.syntax.sirpac.SiRPAC;

import org.xml.sax.SAXException;
import org.xml.sax.InputSource;


/**
  A RDF consumer which uses SiRPAC to parser a RDF Content Language.
  Creates a list of ParseTree objects

  @author Paola Turci - Universita` di Parma
 */

public class RDFFrameConsumer implements RDFConsumer, RDFParserWrapper{

  /* Class paths for SAX parser */
  static final String SYSTEM_PROPERTY_SAX_PARSER = "org.xml.sax.parser";
  static final String SAX_PARSER = "org.apache.xerces.parsers.SAXParser";

  public RDFFrameConsumer(){
  }

  public void startModel () {
    parseTreeBuilder = new ParseTreeBuilder();
  }

  public void endModel () {
  }

  public NodeFactory getNodeFactory() {
    return nodeFactory;
  }

  /**
    This method is invoked by RDF parser every time a new
    statement is generated.
    The statements are collected in a tree;
    the nodes of the tree are subclass of the class Node
    (e.g. ParseTree and Leaf objects)
    Each main resource (main AbsObject) has a tree associated
   */
  public void addStatement (Statement s){
    parseTreeBuilder.addStatement(s);
  }

  /**
    parser
    "content" is a string in RDF format
   */
  public List parse(String content) throws SAXException{
    if (content.length()==0) return null;
/*
    java -Dorg.xml.sax.parser=org.apache.xerces.parsers.SAXParser -classpath ./lib/jade.jar:./lib/jadeTools.jar:./lib/xerces.jar:./lib/rdf.jar jade.Boot ( for Unix )
    or
    java -Dorg.xml.sax.parser=org.apache.xerces.parsers.SAXParser -classpath .\lib\jade.jar;.\lib\jadeTools.jar;.\lib\xerces.jar;.\lib\rdf.jar jade.Boot ( for Windows )
*/

    String parserClassName=System.getProperty(SYSTEM_PROPERTY_SAX_PARSER);
	if( (parserClassName == null) || parserClassName.equals("") ) {
          System.setProperty(SYSTEM_PROPERTY_SAX_PARSER, SAX_PARSER);
	}
/*
    //this property is used by SiRPAC
    System.setProperty(SYSTEM_PROPERTY_SAX_PARSER, SAX_PARSER);
*/
    RDFFactory f = new RDFFactoryImpl();
    try{
      //Prepare input source
      InputSource source = new InputSource(new StringReader(content));

      RDFParser parser = f.createParser();

      //If one wants to fetch the schemas
      //SiRPAC parser = (SiRPAC)f.createParser();
      //parser.setFetchSchemas(true);

      parser.parse(source, this);

      return createParseTreeList();
    }catch (SAXException se){
       se.printStackTrace(System.err);
       throw new SAXException("RDF Parser");
    }catch (ModelException me){
	       me.printStackTrace(System.err);
	       throw new SAXException("Model Exception generated by RDF parser");
    }
  }

  //creates a list of ParseTree from the triples
  private List createParseTreeList()throws ModelException{
	Vector trees = new Vector();
    triples = parseTreeBuilder.getAllTriples();

	for(Iterator i=triples.values().iterator(); i.hasNext();){
	  ParseTree n = (ParseTree) i.next();
	  if(n.isRoot()) trees.add(n);
    }

    return trees;
  }


  private NodeFactory nodeFactory = new NodeFactoryImpl();
  private Map triples;
  private ParseTreeBuilder parseTreeBuilder;
}
