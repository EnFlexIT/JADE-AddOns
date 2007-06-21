package album.version1;
import jade.semantics.interpreter.SemanticAgent;
import jade.semantics.lang.sl.grammar.ByteConstantNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.tools.SL;
import album.tools.JPEGUtilities;

public class Album extends SemanticAgent {

    // Create a pattern for the application-specific predicate "(image-content img ??content)"
    Formula IMAGE_CONTENT_PATTERN = SL.fromFormula("(image-content img ??content)");

    public void setup() {
	super.setup();

	// The picture filename is given as an argument, e.g. "file:img1.jpg"
	String imageId = (String)getArguments()[0];

	// Create a byte array that represents the picture byte content, using the JPEGUtilities class
	byte[] imageCt = JPEGUtilities.toBytesArray(imageId);

	// Instantiate the image-content predicate with a constant of kind ByteConstantNode
	Formula imageFormula = IMAGE_CONTENT_PATTERN.instantiate("content", new ByteConstantNode(imageCt));

	// Interpret the resulting formula
	getSemanticCapabilities().interpret(imageFormula);

    }
}