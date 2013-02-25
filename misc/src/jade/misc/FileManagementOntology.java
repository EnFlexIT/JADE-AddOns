/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2002 TILAB

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
package jade.misc;

import jade.content.onto.BeanOntology;
import jade.content.onto.BeanOntologyException;
import jade.content.onto.Ontology;

public class FileManagementOntology extends BeanOntology {

	private static final long serialVersionUID = -8116783124673669104L;

	// The singleton instance of this ontology
    private final static Ontology theInstance = new FileManagementOntology();

    public final static Ontology getInstance() {
        return theInstance;
    }

    private FileManagementOntology() {
        super("File-management-ontology");

        try {
            // concept schemas
        	add(FileInfo.class);
        	
        	// actions schemas
        	add(GetFilesListAction.class);
        	add(DownloadFileAction.class);
        	add(DownloadMultipleFilesAction.class);
        }
        catch (BeanOntologyException e){
            e.printStackTrace();
        }
    }
}
