/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
JSA - JADE Semantics Add-on is a framework to develop cognitive
agents in compliance with the FIPA-ACL formal specifications.

Copyright (C) 2008 France Télécom

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

/**
 * This JSA extension makes it possible to handle an emotional state for JSA-based
 * agents. This may be useful to improve the quality of user-interfaces implemented
 * with the intelligent agent paradigm.
 * 
 * <p>
 * To have a try, you can launch the following command in the directory of the
 * <b>{@linkplain jsademos.emotionalagent} package</b> (do not forget to properly configure the Java classpath):
 * <pre>
 * java jade.Boot -name test -gui -nomtp agent:jsademos.emotionalagent.MyEmotionalAgent(kb.txt);ihm:jsademos.emotionalagent.InterfaceJSAAgent(kb.txt)
 * </pre>
 * 
 * <br><b>Important note:</b></br>
 * You will have to include the jcommon and the jfreechart libraries (see 
 * <a href="http://www.jfree.org/">http://www.jfree.org/</a>)
 * in your classpath (e.g. we are using the 1.0.12 and 1.0.8a versions). 
 * </p>
 *  
 * The demo above and this software package result from the PhD thesis of
 * <a href="http://magalie.ochs.free.fr/indexEv1.html">Magalie Ochs</a>.
 * It is given in its current state and could not be reviewed for an english
 * translation and a complete documentation. To use it further, please
 * directly contact Magalie.
 * 
 */
package jade.semantics.ext.emotion;