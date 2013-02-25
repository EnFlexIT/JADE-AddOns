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
