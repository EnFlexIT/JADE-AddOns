/*****************************************************************
 JADE - Java Agent DEvelopment Framework is a framework to develop 
 multi-agent systems in compliance with the FIPA specifications.
 Copyright (C) 2004 France T�l�com
 
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
/*
 * NonPrimitiveInform.java
 * Created on 8 juin 2005
 * Author : Vincent Louis
 *
 */
package jade.semantics.actions.performatives;

import jade.semantics.actions.SemanticAction;
import jade.semantics.actions.SemanticActionTable;
import jade.semantics.interpreter.SemanticInterpretationException;
import jade.semantics.lang.sl.grammar.Content;
import jade.semantics.lang.sl.grammar.ContentNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.ListOfContentExpression;
import jade.semantics.lang.sl.grammar.Node;
import jade.semantics.lang.sl.grammar.WordConstantNode;
import jade.semantics.lang.sl.tools.SLPatternManip;
import jade.semantics.lang.sl.tools.SLPatternManip.WrongTypeException;

/**
 * This class specifies a non primitive inform action.
 * @author Vincent Louis - France Telecom
 * @version Date: 2005/06/08 Revision: 1.0
 */
public abstract class NonPrimitiveInform extends Inform {
    
    /**
     * Pattern used to build the inform content corresponding
     * to the ActConditionInform
     */
    protected Formula informContentPattern;
    
    /**
     * Creates a new NonPrimitiveInform prototype.
     * @param table the SemanticActionTable, which this action prototype belongs
     * to
     * @param surfacePerformative the surface form
     * @param surfaceContentFormat the list of class expected in the surface
     * content
     * @param surfaceContentFormatMessage the message to send when an 
     * unexpected exception occurs
     * @param informContentPattern pattern of the inform content
     */
    public NonPrimitiveInform(SemanticActionTable table, int surfacePerformative, Class[] surfaceContentFormat,
            String surfaceContentFormatMessage, Formula informContentPattern) {
        super(table, surfacePerformative, surfaceContentFormat, surfaceContentFormatMessage,
                getRationalEffectRecognitionPattern(informContentPattern));
        this.informContentPattern = informContentPattern;
    }
    
    /**
     * Returns an instance of <code>CommunicativeActionImpl</code>.
     * @return an instance of <code>CommunicativeActionImpl</code>.
     */
    abstract public CommunicativeActionImpl createInstance();
    
    /**
     * Returns the rational effect recognition pattern fro
     * @param informContentPattern
     * @return the rational effect recognition pattern
     */
    static Formula getRationalEffectRecognitionPattern(Formula informContentPattern) {
        try {
            return (Formula)SLPatternManip.instantiate(SLPatternManip.fromFormula("(B ??receiver ??informContent)"),
                    "informContent", informContentPattern);
        }
        catch (WrongTypeException e) {
            return null; // TODO what else???
        }
    }
    
    /**
     * @inheritDoc
     */
    public SemanticAction doNewAction(Content surfaceContent) throws SemanticInterpretationException {
        Content informContent = new ContentNode(new ListOfContentExpression());
        try {
            informContent.addContentElement(instantiateInformContentPattern(surfaceContent));
        }
        catch (WrongTypeException e) {
            throw new SemanticInterpretationException("ill-formed-message", new WordConstantNode(""));
        }
        return super.doNewAction(informContent);
    }
    
    /**
     * Instantiates the content pattern (<code>informContentPattern</code>) with
     * the specified content.
     * @param surfaceContent a content
     * @return an instantied pattern
     * @throws WrongTypeException if an exception is raised during the 
     * instantiation
     */
    abstract protected Node instantiateInformContentPattern(Content surfaceContent) throws WrongTypeException;
}
