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


package jade.tools.ascml.onto;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
   * Protege name: AbsModel
* @author ontology bean generator
* @version 2005/06/29, 16:24:48
*/
public class AbsModel implements Concept {

   /**
   * Protege name: ModelStatus
   */
   private Status modelStatus;
   public void setModelStatus(Status value) { 
    this.modelStatus=value;
   }
   public Status getModelStatus() {
     return this.modelStatus;
   }

   /**
   * Protege name: ToolOptions
   */
   private List toolOptions = new ArrayList();
   public void addToolOptions(ToolOption elem) { 
     toolOptions.add(elem);
   }
   public boolean removeToolOptions(ToolOption elem) {

     boolean result = toolOptions.remove(elem);
     return result;
   }
   public void clearAllToolOptions() {

     toolOptions.clear();
   }
   public Iterator getAllToolOptions() {return toolOptions.iterator(); }
   public List getToolOptions() {return toolOptions; }
   public void setToolOptions(List l) {toolOptions = l; }

   /**
   * The full quallified Name
   * Protege name: Name
   */
   private String name;
   public void setName(String value) { 
    this.name=value;
   }
   public String getName() {
     return this.name;
   }

   /**
   * Protege name: Config
   */
   private String config;
   public void setConfig(String value) { 
    this.config=value;
   }
   public String getConfig() {
     return this.config;
   }

}
