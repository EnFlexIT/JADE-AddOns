/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
JSA - JADE Semantics Add-on is a framework to develop cognitive
agents in compliance with the FIPA-ACL formal specifications.

Copyright (C) 2008 France T�l�com

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

package jade.semantics.ext.sqlwrapper.test;

import jade.semantics.interpreter.SemanticCapabilities;
import jade.semantics.interpreter.SemanticRepresentation;
import jade.semantics.interpreter.sips.adapters.NotificationSIPAdapter;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SL;

/**
 * This principle is intended to be applied to all intentions that have not be 
 * realised. These intentions are considered as not feasible.
 * @author Vincent Pautret - France Telecom
 * @version Date: 2005/06/20 Revision: 1.0
 */
public class SIP_TestQuerry extends NotificationSIPAdapter{
    
    private static Formula Pattern = SL.formula("(B ??myself ??phy)");

    private SemanticCapabilities get_sc()
    { return this.myCapabilities;}
    
    public SIP_TestQuerry(SemanticCapabilities capabilities) {
        super(capabilities, Pattern);
    } // End of Failure/1
    
    
    /*********************************************************************/
    /**                         METHODS                                 **/
    /*********************************************************************/
    
    /**
     * @inheritDoc
     */
    public void notify(MatchResult applyResult, 
            SemanticRepresentation sr)
    {
        try 
        {
            Formula formula;
            Formula[] pattern = new Formula[20] ;
            Formula term = applyResult.getFormula("phy");
            pattern[0] = SL.formula("(test_simple ??x)");
            pattern[10] = SL.formula("(test_simple ??x ??y)");
            pattern[1] = SL.formula("(remove ??x)");
            pattern[2] = SL.formula("(type_int ??x)");
            pattern[3] = SL.formula("(type_string ??x)");
            pattern[4] = SL.formula("(type_real ??x)");
            pattern[5] = SL.formula("(type_date ??x)");
            pattern[6] = SL.formula("(test_join ??x ??y)");
            pattern[7] = SL.formula("(user ??x ??y)");
            pattern[8] = SL.formula("(posede ??x ??y ??z)");
            pattern[9] = SL.formula("(nb_prenom_by_name ??x ??y)");
            pattern[11] = SL.formula("(clpredicat ??x)");
            pattern[12] = SL.formula("(not (clpredicat ??x))");
            pattern[13] = SL.formula("(type_slterm ??x ??y)");
            pattern[14] = SL.formula("(type_slterm_2 ??x ??y)");
            pattern[15] = SL.formula("(type_slterm_3 ??x ??y)");
            pattern[16] = SL.formula("(type_slterm2 ??x ??y)");
            pattern[17] = SL.formula("(type_slterm2_2 ??x ??y)");
            pattern[18] = SL.formula("(type_slterm2_3 ??x ??y)");
            
            
            
            for (int i = 0; i <=18; i++)
                if (SL.match(pattern[i], term) != null)
                {
                    formula = SL.formula("(assert_ok ??phy)").instantiate("phy", SL.term(term.toString()));
                    if(this.get_sc().getMyKBase().query(formula) == null)
                        System.out.println("#KO# - " + term.toString());
                    else
                    {
                        this.get_sc().getMyKBase().retractFormula(formula);
                        System.out.println("[OK] - " + term.toString());
                    }
                    return;
                }
           // System.out.println("?��? - " + term.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    } // End of apply/1
    
} // End of class Failure
