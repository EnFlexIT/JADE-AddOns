/*
 * Copyright (C) 2005 Chair of Computer Science 4
 * Aachen University of Technology
 *
 * Copyright (C) 2005 Dpt. of Communcation and Distributed Systems
 * University of Hamburg
 *
 * This file is part of the ASCML.
 *
 * The ASCML is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * The ASCML is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ASCML; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package jade.tools.ascml.launcher;

import java.util.*;
import jade.tools.ascml.absmodel.*;
import jdsl.graph.algo.TopologicalSort;
import jdsl.graph.api.*;
import jdsl.graph.ref.IncidenceListGraph;


/**
 * This Class takes a scenarioModel and does a topological sort, where vertices are agentinstances and
 * edges are dependencies
 */

/*
public class ScenarioSorter
{

	private IncidenceListGraph myGraph;
	private ISocietyInstance mySModel;
	private TopologicalSort myTSort;
	private HashMap nameVertexMap;
	private HashMap typeVertexMap;
	private HashMap nameTypeMap;
	private HashMap serviceVertexMap;
	private HashMap typeCountMap;
	private boolean isSorted;

	/**
	 * Create a new Sorter with a given SocietyInstanceModel
	 *
	 * @param smodel SocietyInstanceModel to be sorted
	 */
/*
	public ScenarioSorter(ISocietyInstance smodel)
	{
		super();
		isSorted = false;
		myTSort = new TopologicalSort();
		nameVertexMap = new HashMap();
		typeVertexMap = new HashMap();
		typeCountMap = new HashMap();
		serviceVertexMap = new HashMap();
		nameTypeMap = new HashMap();
		mySModel = smodel;
		myGraph = Convert(mySModel);
	}

	/**
	 * Sorts the SocietyInstanceModel and returns whether it is cyclic or not
	 *
	 * @return true if SocietyInstanceModel isn't cylic
	 */
/*
	public boolean Sort()
	{
		myTSort.cleanup();
		myTSort.execute(myGraph);
		isSorted = !myTSort.isCyclic();
		return !myTSort.isCyclic();
	}

	/**
	 * If sorting succeeded this return the sorted list of AgentModelsInstances
	 *
	 * @return a vector containing all AgentModelInstances in the right order
	 */
/*
	public Vector getList()
	{
		Vector av = new Vector();
		if(isSorted)
		{
			VertexIterator vi = myTSort.sortedVertices();
			while(vi.hasNext())
				av.add(vi.nextVertex().element());
		}
		return av;
	}

	public HashMap getNameTypeMap()
	{
		return nameTypeMap;
	}

	public HashMap getTypeCountMap()
	{
		return typeCountMap;
	}

	private IncidenceListGraph Convert(ISocietyInstance smodel)
	{
		//Insert models
		IncidenceListGraph newGraph = new IncidenceListGraph();
		IAgentInstance[] ais = smodel.getAgentInstanceModels();
		for(int i=0; i<ais.length; i++)
		{
			String modelName = ais[i].getName();
			// System.out.println("Inserting name: "+modelName);
			IAgentInstance amodel = smodel.getAgentInstanceModel(modelName);

			//Build up the Name to Type Map
			nameTypeMap.put(modelName, amodel.getType().getName());
			int typecount = 0;
			if(typeCountMap.containsKey(amodel.getType().getName()))
			{
				typecount = Integer.parseInt((String)typeCountMap.get(amodel.getType().getName()));
				typeCountMap.remove(amodel.getType().getName());
			}
			typecount++;
			typeCountMap.put(amodel.getType().getName(), String.valueOf(typecount));

			Vertex myVertex = newGraph.insertVertex(amodel);

			Vector myVector;
			if(nameVertexMap.containsKey(amodel.getName()))
			{
				myVector = (Vector)nameVertexMap.get(amodel.getName());
			}
			else
			{
				myVector = new Vector();
			}
			myVector.add(myVertex);
			nameVertexMap.put(amodel.getName(), myVector);

			if(typeVertexMap.containsKey(amodel.getType().getName()))
			{
				myVector = (Vector)typeVertexMap.get(amodel.getType().getName());
			}
			else
			{
				myVector = new Vector();
			}
			myVector.add(myVertex);
			// System.out.println("Inserting type: "+amodel.getType().getName());
			typeVertexMap.put(amodel.getType().getName(), myVector);
		}

		//insert dependencies
		for(int i=0; i<ais.length; i++)
		{
			Vertex sVertex = null;
			Vertex eVertex = null;

			String modelName = ais[i].getName();
			IAgentInstance amodel = smodel.getAgentInstanceModel(modelName);
			Vector myVector = (Vector)nameVertexMap.get(modelName);
			while(myVector.iterator().hasNext())
			{
				Vertex myVertex = (Vertex)myVector.iterator().next();
				if(myVertex.element().equals(amodel))
				{
					sVertex = myVertex;
					break;
				}
			}
			if(sVertex==null)
			{
				break;
			}
			//Alle dependencies f�r den Agenten eintragen
			IDependency[] dv = amodel.getDependencies();
			for(int j = 0; j<dv.length; j++)
			{
				IDependency oneD = dv[j];
				if(oneD.isActive())
				{
					System.out.println("Inserting "+oneD.toFormattedString());
					Vector targetLookup;
					if(oneD.isAgentInstanceDependend())
					{
						targetLookup = (Vector)nameVertexMap.get(oneD.getAgentInstanceDependency().get("name"));
						eVertex = (Vertex)targetLookup.get(0);
						System.out.println("From "+sVertex.toString()+" to "+eVertex.toString());
						newGraph.insertDirectedEdge(sVertex, eVertex, oneD);
					}
					else if(oneD.isAgentTypeDependend())
					{
						targetLookup = (Vector)typeVertexMap.get(oneD.getAgentTypeDependency().get("name"));
						for(int k = 0; k<targetLookup.size(); k++)
						{
							eVertex = (Vertex)targetLookup.get(k);
							System.out.println("From "+sVertex.toString()+" to "+eVertex.toString());
							newGraph.insertDirectedEdge(sVertex, eVertex, oneD);
						}
					}
					else if(oneD.isServiceDependend())
					{
						//TODO Service Dependency
					}
					else if(oneD.isSocietyInstanceDependend())
					{
						//TODO SocietyInstance Dependency
					}
					else if(oneD.isSocietyTypeDependend())
					{
						//TODO SocietyType Dependency
					}
					else
					{
						//FIXME What to do?
					}
				}
			}

		}
		return newGraph;
	}


}
*/
