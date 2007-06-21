/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
JSA - JADE Semantics Add-on is a framework to develop cognitive
agents in compliance with the FIPA-ACL formal specifications.

Copyright (C) 2007 France Telecom

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

package jade.semantics.lang.sl.tools;

import jade.semantics.lang.sl.grammar.ListOfNodes;
import jade.semantics.lang.sl.grammar.Node;

public class ListOfMatchResults extends ListOfNodes {
	
	/**
	 * Constructs a empty list of match results
	 *
	 */
	public ListOfMatchResults() {
		super();
	}
	
	/**
	 * This method return a clone of the list.
	 * @return a new recreated graph.
	 */
	public Node getClone()
	{
		Node clone = new ListOfMatchResults();
		clone.copyValueOf(this);
		return clone;
	}	
	
	/**
	 * Returns the join list of match results between this list and the other 
     * one given as an argument. Returns null if no MatchResult of the list
     * joins a MatchResult of the other list. 
	 * @param other a list of MatchResult.
	 * @return the join list of match results between this list and the other one given as an argument
	 */
	public ListOfMatchResults join(ListOfMatchResults other)
	{
		ListOfMatchResults result = null;
		for (int i=0; i<size(); i++) {
			for (int j=0; j<other.size(); j++) {
				MatchResult joined = ((MatchResult)get(i)).join((MatchResult)other.get(j));
				if ( joined != null) {
                    if (result == null) result = new ListOfMatchResults();
					if (!result.contains(joined)) result.add(joined);
				}
			}
		}
		return result;
	}
	
	/**
	 * Returns the union between this list of match results and the other one given as an argument
	 * @param other
	 * @return the union between this list of match results and the other one given as an argument
	 */
	public ListOfMatchResults union(ListOfMatchResults other)
	{
		ListOfMatchResults result = (ListOfMatchResults)getClone();
        if (other != null) {
    		for (int i=0; i<other.size(); i++) {
    			if ( !result.contains(other.get(i)) ) {
    				result.add(other.get(i));
    			}
    		}
    		result.removeSubsumedMatchResult();
        }
		return result;
	}
	
    /**
     * Returns true if this list of match results and the other one given as an 
     * argument are equals. Two lists are equals if they have the same
     * size and all MatchResults of the the first list are in the second one.
     * @param other a list of MatchResults
     * @return true if this list of match results and the other one given as an 
     * argument are equals, false if not.
     */
    public boolean equals(ListOfMatchResults other) {
        if (other != null) {
            if (this.size() == other.size()){
                for (int i = 0; i < this.size(); i++) {
                    if (!other.contains(get(i))) return false;     
                }
                return true;
            }
        }
        return false;
    }
    
	public void add(Node node) {
		if ( node != null ) {
			super.add(node);
		}
		else {
			System.err.println("!!!!!!!! WARNING !!!!!!!!! Trying to add a NULL MatchResult");
		}
	}

    
	/**
	 * This method return a display of the list of match results.
	 */
	public String toString() {
		String result = "";
        if (size() == 0) return "Empty list";
		for (int i = 0; i < size(); i++) {
			result += (i==0 ? get(i).toString() : "/\n"+get(i).toString());
		}
		return result;
	}
	
	// ===============================================
	// Package private implementation
	// ===============================================
	void removeSubsumedMatchResult()
	{
		for (int i=0; i<size(); i++) {
			for (int j=i+1; j<size(); j++) {
				MatchResult inters = ((MatchResult)get(i)).intersect((MatchResult)get(j));
				if ( inters != null && inters.size() != 0 ) {
					// valid intersection exists
					if ( inters.equals(get(i)) ) {
						remove(get(j--));
					}
					else if ( inters.equals(get(j))) {
						remove(get(i--));
						break;
					}
				}
			}
		}
	}
}
