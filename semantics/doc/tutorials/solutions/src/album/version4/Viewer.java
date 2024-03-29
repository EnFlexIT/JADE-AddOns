package album.version4;

import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.semantics.interpreter.SemanticAgent;
import jade.semantics.interpreter.SemanticCapabilities;
import jade.semantics.interpreter.SemanticInterpretationPrincipleTable;
import jade.semantics.interpreter.SemanticRepresentation;
import jade.semantics.interpreter.Tools;
import jade.semantics.interpreter.SemanticInterpretationPrinciple;
import jade.semantics.interpreter.sips.adapters.NotificationSIPAdapter;
import jade.semantics.interpreter.sips.adapters.IntentionTransferSIPAdapter;
import jade.semantics.kbase.FilterKBase;
import jade.semantics.kbase.KBase;
import jade.semantics.kbase.filter.KBAssertFilter;
import jade.semantics.kbase.filter.KBAssertFilterAdapter;
import jade.semantics.lang.sl.grammar.ActionExpression;
import jade.semantics.lang.sl.grammar.Constant;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.IdentifyingExpression;
import jade.semantics.lang.sl.grammar.IntegerConstantNode;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.grammar.TermSet;
import jade.semantics.lang.sl.grammar.TermSetNode;
import jade.semantics.lang.sl.grammar.TrueNode;
import jade.semantics.lang.sl.grammar.WordConstantNode;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SL;
import jade.semantics.lang.sl.tools.SL.WrongTypeException;
import jade.util.leap.ArrayList;
import album.tools.DefaultViewerGUI;
import album.tools.ViewerGUI;

public class Viewer extends SemanticAgent {
    ViewerGUI myGUI = new DefaultViewerGUI("Photo Viewer");

    IdentifyingExpression ANY_IMAGE_CONTENT_IRE_PATTERN =
	(IdentifyingExpression)SL.fromTerm("(any ?a (image-content ??id ?a))");
    Formula B_IMAGE_CONTENT_PATTERN =
	SL.fromFormula("(B ??myself (image-content ??id ??content))");

    // Define a pattern of ActionExpression, to be used to request the album agent to perform the diaporama action
    ActionExpression DIAPORAMA_ACTION_PATTERN = (ActionExpression)
		SL.fromTerm("(action ??actor (PLAY-DIAPO :images ??images (::? :tempo ??tempo) :viewer ??viewer))");

    public class ViewerSemanticCapabilities extends SemanticCapabilities {

	protected SemanticInterpretationPrincipleTable setupSemanticInterpretationPrinciples() {
		SemanticInterpretationPrincipleTable res_table = super.setupSemanticInterpretationPrinciples();
		SemanticInterpretationPrinciple sip =
		new NotificationSIPAdapter(this, B_IMAGE_CONTENT_PATTERN) {
		    protected void notify(final MatchResult applyResult, SemanticRepresentation sr) {
			potentiallyAddBehaviour(new OneShotBehaviour() {
				public void action() {
				    myGUI.displayPhoto(((Constant)applyResult.term("content")).byteValue());
				}
			    });
		    }
		};
	    res_table.addSemanticInterpretationPrinciple(sip);
	    return res_table;
	}

	protected KBase setupKbase() {
		FilterKBase res_kbase = (FilterKBase)super.setupKbase();
	    KBAssertFilter filter = new KBAssertFilterAdapter(B_IMAGE_CONTENT_PATTERN) {
		    public Formula doApply(Formula formula) {
			return new TrueNode();
		    }
		};
	    res_kbase.addKBAssertFilter(filter);
	    return res_kbase;
	}
    }

    public Viewer() {
	setSemanticCapabilities(new ViewerSemanticCapabilities());
    }

    public void setup() {
	super.setup();
	Term album = Tools.AID2Term(new AID((String)getArguments()[0], AID.ISLOCALNAME));
	TermSet imageSet = new TermSetNode();

	// Read the picture URIs to play with the diaporama action and add them to the imageSet TermSet (as WordConstantNodes)
	for (int i=1 ; i<getArguments().length ; i++) {
	    imageSet.addTerm(new WordConstantNode((String)getArguments()[i]));
	}

	try {
	    // Instantiate properly the DIAPORAMA_ACTION_PATTERN pattern
	    ActionExpression diapoActionExp = (ActionExpression)SL
	    	.instantiate(DIAPORAMA_ACTION_PATTERN,
	    			 "actor", album,
				     "tempo", new IntegerConstantNode(new Long(5000)),
				     "images", imageSet,
				     "viewer", getSemanticCapabilities().getAgentName());

	    // Request the album agent to perform this action
	    getSemanticCapabilities().request(diapoActionExp, album);
	} catch (WrongTypeException e) {
	    e.printStackTrace();
	}
    }
}
