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


package jade.tools.ascml.model;

import java.util.ArrayList;
import jade.tools.ascml.absmodel.IDocument;

/**
 *  The document model for file related info.
 */
public class DocumentModel implements IDocument
{
	//-------- attributes --------

	/** The source. */
	protected Object source;

	/** The package name. */
	protected String packname;

	/** The imports. */
	protected ArrayList imports;

	//-------- constructors --------

	/**
	 *  Create a new document.
	 */
	public DocumentModel()
	{
		this.source = "";
		this.packname = "";
		this.imports = new ArrayList();
	}

	/**
	 *  Create a new document.
	 *  @param src  The source-path of this document
	 */
	public DocumentModel(String src)
	{
		this();
		this.source = src;
	}

	//-------- methods --------

	/**
	 *  Set the source.
	 *  @param source  The source-object.
	 */
	public void setSource(Object source)
	{
		this.source = source;
	}

	/**
	 *  Get the source.
	 *  @return  The source-object.
	 */
	public Object getSource()
	{
		return source;
	}

	/**
	 *  Set the package name.
	 *  @param packname The package name.
	 */
	public void setPackageName(String packname)
	{
		if(packname==null)
			packname = "";
		this.packname = packname;
	}

	/**
	 *  Get the package name.
	 *  @return  The package name.
	 */
	public String getPackageName()
	{
		return packname;
	}

	/**
	 *  Set the imports.
	 *  @param imp The imports.
	 */
	public void addImport(String imp)
	{
		this.imports.add(imp);
	}

	/**
	 *  Get the imports.
	 *  @return The imports.
	 */
	public String[] getImports()
	{
		return (String[])imports.toArray(new String[imports.size()]);
	}
}
