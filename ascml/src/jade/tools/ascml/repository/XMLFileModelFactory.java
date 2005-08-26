/*
 * Copyright (C) 2005 Chair of Computer Science 4
 * Aachen University of Technology
 *
 * Copyright (C) 2005 Dpt. of Communcation and Distributed Systems
 * University of Hamburg
 *
 * This file is part of the ASCML.
 *
 * The ASCML is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * The ASCML is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ASCML; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */


package jade.tools.ascml.repository;

import javax.xml.parsers.*;
import java.io.*;
import java.util.zip.*;
import java.util.jar.*;
import java.util.Vector;

import jade.tools.ascml.absmodel.*;
import jade.tools.ascml.repository.loader.xml.*;
import jade.tools.ascml.exceptions.ModelException;
import jade.tools.ascml.exceptions.ResourceNotFoundException;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

/**
 *  The default model loader creates the default model elements.
 *  This class is instantiated via reflection by the ModelConfiguration
 */
public class XMLFileModelFactory implements IModelFactory
{
	private boolean debug = false;
	private boolean validating = false;

	/** This variable is only used in case of an Exception to tell
	    the user which file failed to be parsed. **/
	private String xmlFile;
	
	/**
	 *  Load a model (capability, agent or ...?).
	 *  @param xmlFile  The agent definition file.
	 *  @param repository  The Repository-object
	 *  @return The model.
	 */
	public Object createModel(String xmlFile, Repository repository) throws ModelException, ResourceNotFoundException
	{
		this.xmlFile = xmlFile;
		
		Object ret = null;

		// Can this be removed? Does anyone call this method with
		// concatenated filename?
		String jarFile = null;
		if(xmlFile.indexOf(".jar::")!=-1)
		{
			jarFile = xmlFile.substring(0, xmlFile.indexOf(".jar::")+4);
			xmlFile = xmlFile.substring(xmlFile.indexOf(".jar::")+6, xmlFile.length());
		}
		else if(xmlFile.indexOf(".zip::")!=-1)
		{
			jarFile = xmlFile.substring(0, xmlFile.indexOf(".zip::")+4);
			xmlFile = xmlFile.substring(xmlFile.indexOf(".zip::")+6, xmlFile.length());
		}

		InputStream modelInputStream = null;
		if(jarFile!=null)
			modelInputStream = getModelInputStreamFromJar(xmlFile, jarFile);
		else
		{
			modelInputStream = getModelInputStream(xmlFile);
		}

		Document doc = readDocument(modelInputStream);
		Element root = doc.getDocumentElement();

		if(xmlFile.endsWith(".society.xml"))
		{
			ISocietyType model = SocietyTypeLoader.getModel(root, xmlFile, repository);
			model.getDocument().setSource(xmlFile);
			return model;
		}
		else if(xmlFile.endsWith(".agent.xml"))
		{
			IAgentType model = AgentTypeLoader.getModel(root, xmlFile, repository.getListenerManager().getModelChangedListener());
			return model;
		}
		throw new ResourceNotFoundException("Malformatted model-source description: '"+xmlFile+"'", "The source used for loading the modeldescription is malformatted. For example: AgentType-descriptions need to be in a file having the format 'MyAgent.agent.xml'. The filename of SocietyTypes on the other hand must have following format: 'MySociety.society.xml'.");
	}

	/**
	 * Read the xml file and generate the DOM-tree.
	 *
	 * @param inputStream An InputStream to the xmlFile, this may be a FileInputStream
	 *                    in case the file can be adressed directly or a ByteArrayInputStream
	 *                    in case the file is contained within a jar-archiv.
	 * @return The document.
	 */
	private Document readDocument(InputStream inputStream) throws ModelException
	{
		long millisec;
		Document document = null;

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(validating);
		factory.setNamespaceAware(true);
		factory.setIgnoringElementContentWhitespace(true);
		factory.setIgnoringComments(true);
		// factory.setErrorHandler(this);

		try
		{
			DocumentBuilder builder = factory.newDocumentBuilder();
			// parse and build the DOM tree
			document = builder.parse(inputStream);
			inputStream.close();
		}
		catch(javax.xml.parsers.ParserConfigurationException p)
		{
			throw new ModelException("XML-Parser is not configured properly", "The configuration of the XML-parser needed to parse the description-files could not be found. This is a serious problem, cause without the parser-configuration, the ASCML isn't able to load description-files. Please check your installation and configuration of the Java-Runtime environment", p);
		}
		catch(SAXException s)
		{
			throw new ModelException("Error while parsing the model-file '"+xmlFile+"'","The SAX-Parser, used to parse the description-files of agents and societies, reported an error. Please check the syntax of your description-file and the configuration and installation of your Java-Runtime environment",  s);
		}
		catch(IOException i)
		{
			throw new ModelException("Error while reading a model-file '"+xmlFile+"'", "There has been an error while trying to read a description-file. Please check if the specified resource is still present at the given location and if you have the rights to access this resource.",  i);
		}

		millisec = System.currentTimeMillis();

		if(debug) System.out.println("XMLFileModelFactory.readDocument: Parsing took about "+(System.currentTimeMillis()-millisec)+" mSecs");

		return document;
	}
	
	/**
	 *  Returns an InputStream to the given file.
	 *  The InputStream may either be an instance of FileInputStream or
	 *  ByteArrayInputStream depending on whether the file is contained within a jar-archiv or not.
	 *  @param fileName The fileName, for which an InputStream should be returned
	 *  @return An InputStream pointing to the file.
	 */
	private InputStream getModelInputStream(String fileName) throws ResourceNotFoundException
	{
		// toDo: Vielleicht JNDI-Support implementieren ?
		// http://java.sun.com/products/jndi/tutorial/getStarted/examples/naming.html
		
		InputStream is = XMLFileModelFactory.class.getClassLoader().getResourceAsStream(fileName);
		if(is == null)
		{
			try
			{
				is = new FileInputStream(fileName);
			}
			catch(FileNotFoundException e)
			{
				// e.printStackTrace();
				throw new ResourceNotFoundException("The following file couldn't be found: '" + fileName + "'.", "The file could not be found, please check the filename given in the property-file and then reload the project !");
			}
		}
		return is;
	}

	/**
	 * This method returns an inputStream for the xml-File, which is
	 * contained within a jar-archiv.
	 *
	 * @param modelName   fileName of the model-description-file.
	 * @param jarFileName fileName of the jar-file, that contains the xml-file.
	 * @return ByteArrayInputStream containing the xml-Files content.
	 */
	private InputStream getModelInputStreamFromJar(String modelName, String jarFileName)
	{
		InputStream returnStream = null;
		try
		{
			JarFile jarFile = new JarFile(jarFileName);

			FileInputStream fis = new FileInputStream(jarFileName);
			ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
			ZipEntry entry = null;
			
			// iterate through all entries of the jar-File.
			while((entry = zis.getNextEntry())!=null)
			{
				// if the matching entry is found, start loading the data
				// and create an ByteArrayInputStream with the loaded data.
				if(entry.getName().equals(modelName))
				{
					int count = 0;
					byte data[] = new byte[512];

					ByteArrayOutputStream bos = new ByteArrayOutputStream();

					while((count = zis.read(data, 0, 512))!=-1)
					{
						bos.write(data, 0, count);
					}

					bos.flush();
					bos.close();
					
					// the inputStream is used by the ModelRepository to read and parse the
					// xml-file.
					returnStream = new ByteArrayInputStream(bos.toByteArray());
				}
			}
			zis.close();
			jarFile.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return returnStream;
	}
	
}
