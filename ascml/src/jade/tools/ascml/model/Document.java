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

/**
 *  The document model for file related info.
 */
public class Document
{
	public final static String SOURCE_UNKNOWN = "Source Unknown";

	/** The source. */
	protected String source = SOURCE_UNKNOWN;

	//-------- constructors --------

	/**
	 *  Create a new document.
	 */
	public Document()
	{
		this.source = "";
	}

	/**
	 *  Create a new document.
	 *  @param src  The source-path of this document
	 */
	public Document(String src)
	{
		this();
		this.source = src;
	}

	//-------- methods --------

	/**
	 *  Set the source.
	 *  @param source  The source-object.
	 */
	public void setSource(String source)
	{
		this.source = source;
	}

	/**
	 *  Get the source.
	 *  @return  The source-object.
	 */
	public String getSource()
	{
		if ((source == null) || (source.equals("")))
			source = SOURCE_UNKNOWN;
		return source;
	}
}
