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

package test.common.xml;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import org.w3c.dom.Document;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import java.net.URL;

/**
 * @author Elisabetta Cortese - TiLab
 *
 */
public class XMLManager {

	/**
	 * Constructor for XMLManager.
	 */
	public XMLManager() {
	}
	
	// this metod returns testers' list, in input tale the file name where
	// is 
	public static FunctionalityDescriptor[] getFunctionalities(String xmlFileName){
		FunctionalityDescriptor[] fd = null;
		Document doc;
		int j = 0;
		try{
			//il path del file xml
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			factory.setValidating(true);
			
			DocumentBuilder builder = factory.newDocumentBuilder();
      URL url = ClassLoader.getSystemResource(xmlFileName);
      File f = new File(url.getFile());
      System.out.println("Parsing file "+f);
			doc = builder.parse(f);
			
			NodeList list = doc.getElementsByTagName("Tester");
			fd = new FunctionalityDescriptor[list.getLength()];			
			
			for (int i = 0; i < list.getLength(); i++) {
				fd[i] = new FunctionalityDescriptor();
				Element e = (Element)list.item(i);
				fd[i].setName( e.getAttribute("name"));
				fd[i].setSkip( e.getAttribute("skip"));
				if(!fd[i].getSkip()){
					j++;
				}
				
				Node n = ((NodeList)(e.getChildNodes())).item(1);
				fd[i].setTesterClassName((n.getChildNodes()).item(0).getNodeValue());
				n = ((NodeList)(e.getChildNodes())).item(3);
				fd[i].setDescription((n.getChildNodes()).item(0).getNodeValue());
			}
			
		}catch(SAXParseException spe ){
			// Error generated by the parser
			System.out.println("\n** Parsing error"
			  + ", line " + spe.getLineNumber()
			  + ", uri " + spe.getSystemId());
			System.out.println("   " + spe.getMessage() );
			  
			// Use the contained exception, if any
			Exception  x = spe;
			if (spe.getException() != null)
			  x = spe.getException();
			x.printStackTrace();

		} catch (SAXException sxe) {
			// Error generated during parsing
			Exception  x = sxe;
			if (sxe.getException() != null)
			  x = sxe.getException();
			x.printStackTrace();

		} catch (ParserConfigurationException pce) {
    		// Parser with specified options can't be built
			pce.printStackTrace();

		}catch(IOException ioE){
			ioE.printStackTrace();
		}
		FunctionalityDescriptor[] result = new FunctionalityDescriptor[j];
		int k = 0;
		for(int i = 0; i < fd.length; i++){
			if(!fd[i].getSkip()){
				result[k] = fd[i];
				k++;
			}
		}
//		return fd;
		return result;
	}
	
	public static TestDescriptor[] getTests(String xmlFileName){
		TestDescriptor[] td = null;
		Document doc;
		int j =0;
		try{
			//il path del file xml
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			factory.setValidating(true);
			
			DocumentBuilder builder = factory.newDocumentBuilder();
			doc = builder.parse(xmlFileName);
			
			NodeList list = doc.getElementsByTagName("Test");
			td = new TestDescriptor[list.getLength()];

			for (int i = 0; i < list.getLength(); i++) {
				Element e = (Element)list.item(i);
				td[i] = new TestDescriptor();
				td[i].setName(e.getAttribute("name"));
				td[i].setSkip(e.getAttribute("skip"));
				if(!td[i].getSkip()){
					j++;
				}
				Node n = ((NodeList)(e.getChildNodes())).item(1);
				td[i].setTestClass((n.getChildNodes()).item(0).getNodeValue());
				n = ((NodeList)(e.getChildNodes())).item(3);
				td[i].setWhat((n.getChildNodes()).item(0).getNodeValue());
				n = ((NodeList)(e.getChildNodes())).item(5);
				td[i].setHow((n.getChildNodes()).item(0).getNodeValue());
				n = ((NodeList)(e.getChildNodes())).item(7);
				td[i].setPassedWhen((n.getChildNodes()).item(0).getNodeValue());
			}
			
		}catch(SAXParseException spe ){
			// Error generated by the parser
			System.out.println("\n** Parsing error"
			  + ", line " + spe.getLineNumber()
			  + ", uri " + spe.getSystemId());
			System.out.println("   " + spe.getMessage() );
			  
			// Use the contained exception, if any
			Exception  x = spe;
			if (spe.getException() != null)
			  x = spe.getException();
			x.printStackTrace();

		} catch (SAXException sxe) {
			// Error generated during parsing
			Exception  x = sxe;
			if (sxe.getException() != null)
			  x = sxe.getException();
			x.printStackTrace();

		} catch (ParserConfigurationException pce) {
    		// Parser with specified options can't be built
			pce.printStackTrace();

		}catch(IOException ioE){
			ioE.printStackTrace();
		}
		
		TestDescriptor[] result = new TestDescriptor[j];
		int k = 0;
		for(int i = 0; i < td.length; i++){
			if(!td[i].getSkip()){
				result[k] = td[i];
				k++;
			}
		}
//		return td;
		return result;
	}

}
