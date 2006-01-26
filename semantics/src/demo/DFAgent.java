/*****************************************************************
 JADE - Java Agent DEvelopment Framework is a framework to develop 
 multi-agent systems in compliance with the FIPA specifications.
 Copyright (C) 2004 France T�l�com
 
 GNU Lesser General Public License
 
 This library is custom software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Custom Software Foundation, 
 version 2.1 of the License. 
 
 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.
 
 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the
 Custom Software Foundation, Inc., 59 Temple Place - Suite 330,
 Boston, MA  02111-1307, USA.
 *****************************************************************/

package demo;

import jade.semantics.interpreter.SemanticAgentBase;
/**
 * DF agent
 * @author Thierry Martinez - France Telecom
 * @version Date: 2005/07/04 Revision: 1.0
 */
public class DFAgent extends SemanticAgentBase {
    
    public DFAgent() {
        super(new DFCapabilities());
    }
}
