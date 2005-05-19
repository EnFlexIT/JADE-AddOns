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
 * KBFilter.java
 * Created on 15 déc. 2004
 * Author : louisvi
 */
package jade.semantics.kbase.filter;

import jade.semantics.interpreter.SemanticAgent;
import jade.semantics.kbase.KBase;
import jade.semantics.kbase.KbaseImpl_List;

/**
 * General object that represents a knowledge base filter.
 * @author louisvi
 * @version Date: 2004/11/30 17:00:00  Revision: 1.0
 */
public class KBFilter {
    
    /**
     * Knowledge nase onto apply this filter
     */
    protected KbaseImpl_List myKBase;
    
    /**
     * @return Returns the myKBase.
     */
    public KBase getMyKBase() {
        return myKBase;
    } // End of getMyKBase/0
    
    /**
     * @param myKBase The myKBase to set.
     */
    public void setMyKBase(KbaseImpl_List myKBase) {
        this.myKBase = myKBase;
    } // End of setMyKBase/1
    
} // End of KBFilter
