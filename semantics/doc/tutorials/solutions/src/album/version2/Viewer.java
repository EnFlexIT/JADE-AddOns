package album.version2;

import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.semantics.interpreter.SemanticAgent;
import jade.semantics.interpreter.SemanticCapabilities;
import jade.semantics.interpreter.SemanticInterpretationPrincipleTable;
import jade.semantics.interpreter.SemanticRepresentation;
import jade.semantics.interpreter.Tools;
import jade.semantics.interpreter.SemanticInterpretationPrinciple;
import jade.semantics.interpreter.sips.adapters.NotificationSIPAdapter;
import jade.semantics.lang.sl.grammar.Constant;
import jade.semantics.lang.sl.grammar.IdentifyingExpression;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SL;
import jade.util.leap.ArrayList;
import album.tools.DefaultViewerGUI;
import album.tools.ViewerGUI;

public class Viewer extends SemanticAgent
{
    // Create a GUI using the provided DefaultViewerGUI implementation
    ViewerGUI myGUI = new DefaultViewerGUI("Photo Viewer");

    IdentifyingExpression ANY_IMAGE_CONTENT_IRE =
	(IdentifyingExpression)SL.fromTerm("(any (sequence ?x ?y) (image-content ?x ?y))");

    // Define the ViewerSemanticCapabilities class
    public class ViewerSemanticCapabilities extends SemanticCapabilities {

	// Override the setupSemanticInterpretationPrinciples method
	protected SemanticInterpretationPrincipleTable setupSemanticInterpretationPrinciples() {
		// Create a default SIP table
		SemanticInterpretationPrincipleTable table = super.setupSemanticInterpretationPrinciples();

	    // Create a new SIP to display the picture, using a NotificationSIPAdapter
	    SemanticInterpretationPrinciple sip_view =
	    new NotificationSIPAdapter(this, "(image-content ??id ??content)") {
		    protected void notify(final MatchResult applyResult, SemanticRepresentation sr) {
		    	potentiallyAddBehaviour(new OneShotBehaviour() {
					public void action() {
					    myGUI.displayPhoto(((Constant)applyResult.term("content")).byteValue());
					}
				});
		    }
		};

	    // Add the created SIP into the SIP table
	    table.addSemanticInterpretationPrinciple(sip_view);
	    return table;
	}
    }

    public Viewer() {
    // Initialize the agent's capabilities using the ViewerSemanticCapabilities class
	setSemanticCapabilities(new ViewerSemanticCapabilities());
    }

    public void setup() {
	super.setup();
	Term album = Tools.AID2Term(new AID((String)getArguments()[0], AID.ISLOCALNAME));
	getSemanticCapabilities().queryRef(ANY_IMAGE_CONTENT_IRE, album);
    }
}
