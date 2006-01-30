/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2004 France Télécom

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
 * Util.java
 * Created on 10 janv. 2006
 * Author : Vincent Pautret
 */
package jade.semantics.lang.sl.tools;

import jade.semantics.lang.sl.grammar.AndNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.ListOfNodes;
import jade.semantics.lang.sl.grammar.MetaTermReferenceNode;
import jade.semantics.lang.sl.grammar.Node;
import jade.semantics.lang.sl.grammar.OrNode;

/**
 * Utilities class.
 * @author Vincent Pautret - France Telecom
 * @version Date:  Revision: 1.0
 */
public class Util {

    /**
     * Tries to return a AndNode built with the Formulae of the given list. 
     * If the size of the list equals 1, it returns the only formula of the list.
     * If the size of the list equals 0 or if the list is null, returns null.
     * Else, it returns an AndFormula. 
     * @param l a list of formulae
     * @return a AndNode built with the Formulae of the given list, 
     * or a Formula, or null. 
     */
    public static Formula buildAndNode(ListOfNodes l) {
        Formula solution = null;
        if (l != null) {
            if (l.size() == 1) {
                solution = (Formula)l.get(0);
            } else if (l.size() >= 2) {
                AndNode andNode = new AndNode((Formula)l.get(l.size()-2), (Formula)l.get(l.size()-1));
                for (int i = l.size() - 3; i >= 0; i--) {
                    andNode = new AndNode((Formula)l.get(i), andNode);
                }
                solution = andNode;
            }
        }
        return solution;
    }

    /**
     * Tries to return a OrNode built with the Formulae of the given list.
     * If the size of the list equals 1, it returns the only formula of the list.
     * If the size of the list equals 0 or if the list is null, returns null.
     * Else, it returns an OrFormula. 
     * @param l a list of formulae
     * @return a OrNode built with the Formulae of the given list, 
     * or a Formula, or null. 
     */
    public static Formula buildOrNode(ListOfNodes l) {
        Formula solution = null;
        if (l != null) {
            if (l.size() == 1) {
                solution = (Formula)l.get(0);
            } else if (l.size() >= 2) {
                OrNode orNode = new OrNode((Formula)l.get(l.size()-2), (Formula)l.get(l.size()-1));
                for (int i = l.size() - 3; i >= 0; i--) {
                    orNode = new OrNode((Formula)l.get(i), orNode);
                }
                solution = orNode;
            }
        }
        return solution;
    }
 
    /**
     * This method returns true if a MetaVariable has the given varName in the 
     * given MatchResult and if it succeeds in giving it the given value.  
     * @param match a MatchResult 
     * @param varName the name of a metavaraible
     * @param value the value of the metavariable
     * @return true if a MetaVariable has the given varName in the 
     * given MatchResult and if it succeeds in giving it the given value, false
     * if not.
     */
    public static boolean instantiateInMatchResult(MatchResult match, String varName, Node value) {
        try {
            for (int i = 0; i < match.size(); i++) {
                if (((MetaTermReferenceNode)match.get(i)).lx_name().equals(varName)) {
                    SLPatternManip.setMetaReferenceValue((MetaTermReferenceNode)match.get(i), value);
                    return true;
                }
            }
        } catch (SLPatternManip.WrongTypeException wte) {
            wte.printStackTrace();
        }
        return false;
    }
}
