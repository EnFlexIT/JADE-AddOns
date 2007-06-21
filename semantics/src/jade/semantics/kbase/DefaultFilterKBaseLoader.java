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

/*
* created on 12 mars 07 by Thierry Martinez
*/

package jade.semantics.kbase;

import jade.semantics.interpreter.StandardCustomization;
import jade.semantics.kbase.filter.CFPFilters;
import jade.semantics.kbase.filter.EventMemoryFilters;
import jade.semantics.kbase.filter.assertion.AllIREFilter;
import jade.semantics.kbase.filter.assertion.AndFilter;
import jade.semantics.kbase.filter.query.ExistsFilter;
import jade.semantics.kbase.filter.query.ForallFilter;
import jade.semantics.kbase.filter.query.IREFilter;
import jade.semantics.kbase.filter.query.OrFilter;

public class DefaultFilterKBaseLoader {
	
	public void load(FilterKBase kbase) {
		load(kbase, null);
	}
	
	public void load(FilterKBase kbase, StandardCustomization standardCustomization) {

		// Add several filters to handle event memory beliefs
		kbase.addFiltersDefinition(new EventMemoryFilters());
	       
		// Add other filters
		kbase.addFiltersDefinition(new CFPFilters(kbase));

		kbase.addKBQueryFilter(new IREFilter());
		kbase.addKBQueryFilter(new jade.semantics.kbase.filter.query.AndFilter());
		kbase.addKBQueryFilter(new OrFilter());
		kbase.addKBQueryFilter(new ExistsFilter());
		kbase.addKBQueryFilter(new ForallFilter());
		kbase.addKBAssertFilter(new AllIREFilter());
		kbase.addKBAssertFilter(new AndFilter());
		kbase.addKBAssertFilter(new jade.semantics.kbase.filter.assertion.ForallFilter());       
	}

}

