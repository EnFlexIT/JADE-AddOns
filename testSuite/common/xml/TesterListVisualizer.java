/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2000 CSELT S.p.A. 

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

package test.common.xml;

import javax.xml.parsers.DocumentBuilder; 
import javax.xml.parsers.DocumentBuilderFactory;  
import javax.xml.parsers.FactoryConfigurationError;  
import javax.xml.parsers.ParserConfigurationException;
 
import org.xml.sax.SAXException;  
import org.xml.sax.SAXParseException;  

import java.io.File;
import java.io.IOException;

import org.w3c.dom.Document;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

// Basic GUI components
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import java.awt.*;
import java.awt.event.*;


// GUI components for right-hand side
import javax.swing.JSplitPane;
import javax.swing.JEditorPane;

// GUI support classes
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;

// For creating borders
import javax.swing.border.EmptyBorder;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;

// For creating a TreeModel
import javax.swing.tree.*;
import javax.swing.event.*;
import java.util.*;
import java.net.URL;

/**
 * @author Elisabetta Cortese - TiLab
 *
 */

public class TesterListVisualizer  extends JPanel
{
  // Global value so it can be ref'd by the tree-adapter
  static Document document;

  boolean compress = true;

  static final int windowHeight = 460;
  static final int leftWidth = 300;
  static final int rightWidth = 340;
  static final int windowWidth = leftWidth + rightWidth;
	//static final String xmlFileName = "test\\testerList.xml";
    
 	private TesterListVisualizer(String xmlFileName){
		document = getDocument(xmlFileName);
  } 

  public static FunctionalityDescriptor showSelectionDlg(JFrame parent, String xmlFileName) {
    final FunctionalityDescriptor[] dummy = new FunctionalityDescriptor[1];
  	final JDialog dlg = new JDialog(parent, "Test List Details", true); 

    dlg.getContentPane().setLayout(new BorderLayout());
    
    // Functionalities tree in the CENTER part
    TesterListVisualizer visualizer = new TesterListVisualizer(xmlFileName);
    final JTree functionalitiesTree = visualizer.initialize();
    dlg.getContentPane().add(BorderLayout.CENTER, visualizer);
    
    // Buttons in the SOUTH part
		JPanel p = new JPanel();
		JButton bOk = new JButton("OK");
		JButton bCancel = new JButton("Cancel");
		// Adjust buttons size (note that the preferred size of the 
		// cancel button can't be shrinked
		bOk.setPreferredSize(bCancel.getPreferredSize());
		
		// Handler for the OK button
		bOk.addActionListener(new	ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				try {
					TreePath path = functionalitiesTree.getSelectionPath();
					AdapterNode adNode = (AdapterNode) path.getPathComponent(2);
					Element el = (Element) adNode.domNode;
					FunctionalityDescriptor func = new FunctionalityDescriptor();
					func.setName(el.getAttribute("name"));
					// Note that in this context we don't care about the skip attribute
					func.setSkip(el.getAttribute("skip"));
					// The class-name is the first child of the selected element
					Node n = ((NodeList) el.getChildNodes()).item(1);
					func.setTesterClassName(n.getChildNodes().item(0).getNodeValue());
					dummy[0] = func;
				}
				catch (Exception ex) {
					// Maybe the user didn't select a valid functionality.
					// In this case the OK button acts like the CANCEL one
				}
				dlg.dispose();
			}	
		} );
		p.add(bOk);

		// Handler for the CANCEL button
		bCancel.addActionListener(new	ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dlg.dispose();
			}
		} );							
		p.add(bCancel);
		dlg.getContentPane().add(p, BorderLayout.SOUTH);

		// Handler for the right corner exit button 
  	dlg.addWindowListener(
      new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
        	dlg.dispose();
        }
      }  
    );

    // Visualize the dialog window in a "Modal way" 
		dlg.setModal(true);
    dlg.pack();
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    int w = windowWidth + 10;
    int h = windowHeight + 10;
    dlg.setLocation(screenSize.width/3 - w/2, screenSize.height/2 - h/2);
    dlg.setSize(w, h);
    dlg.show();
    
    // We reach this point when the user clicks on the OK, Cancel or right-corner button 
    return dummy[0];
  }

  private JTree initialize()
  {
		// Make a nice border
		EmptyBorder eb = new EmptyBorder(5,5,5,5);
		BevelBorder bb = new BevelBorder(BevelBorder.LOWERED);
		CompoundBorder cb = new CompoundBorder(eb,bb);
		this.setBorder(new CompoundBorder(cb,eb));
		
		// Set up the tree
		JTree tree = new JTree(new DomToTreeModelAdapter() );
		
		// Build left-side view
		JScrollPane treeView = new JScrollPane(tree);
		treeView.setPreferredSize(new Dimension( leftWidth, windowHeight ));
		
		// Build right-side view
		// (must be final to be referenced in inner class)
		final JEditorPane htmlPane = new JEditorPane("text/html","");
		htmlPane.setEditable(false);
		JScrollPane htmlView = new JScrollPane(htmlPane);
		htmlView.setPreferredSize(new Dimension( rightWidth, windowHeight ));
		
		// Wire the two views together. Use a selection listener 
		// created with an anonymous inner-class adapter.
		tree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				TreePath p = e.getNewLeadSelectionPath();
				if (p != null) {
					AdapterNode adpNode = (AdapterNode) p.getLastPathComponent();
					htmlPane.setText(adpNode.content());
				}
			}
		} );

		// Build split-pane view
		JSplitPane splitPane = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, treeView, htmlView );
		splitPane.setContinuousLayout( true );
		splitPane.setDividerLocation( leftWidth );
		splitPane.setPreferredSize(new Dimension( windowWidth + 10, windowHeight+10 ));
		
		// Add GUI components
		this.setLayout(new BorderLayout());
		this.add(BorderLayout.CENTER, splitPane );
		
		return tree;
  }


	private Document getDocument(String fileName){
		Document doc= null;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
          DocumentBuilder builder = factory.newDocumentBuilder();
          URL url = ClassLoader.getSystemResource(fileName);
          File f = new File(url.getFile());
          System.out.println("Parsing file "+f);
          doc = builder.parse(f);

        } catch (SAXParseException spe) {
           // Error generated by the parser
           System.out.println("\n** Parsing error"
              + ", line " + spe.getLineNumber()
              + ", uri " + spe.getSystemId());
           System.out.println("   " + spe.getMessage() );

           // Use the contained exception, if any
           Exception  x = spe;
           if (spe.getException() != null)
               x = spe.getException();
           x.printStackTrace();
 
        } catch (SAXException sxe) {
           // Error generated during parsing)
           Exception  x = sxe;
           if (sxe.getException() != null)
               x = sxe.getException();
           x.printStackTrace();

        } catch (ParserConfigurationException pce) {
            // Parser with specified options can't be built
            pce.printStackTrace();

        } catch (IOException ioe) {
           // I/O error
           ioe.printStackTrace();
        }
        return doc;
	}

  // An array of names for DOM node-types
  // (Array indexes = nodeType() values.)
  static final String[] typeName = {
      "none",
      "Element",
      "Attr",
      "Text",
      "CDATA",
      "ProcInstr",
      "Comment",
      "Document",
      "DocType",
      "Notation",
  };
  static final int ELEMENT_TYPE =   Node.ELEMENT_NODE;
  static final int ATTR_TYPE =      Node.ATTRIBUTE_NODE;
  static final int TEXT_TYPE =      Node.TEXT_NODE;
  static final int CDATA_TYPE =     Node.CDATA_SECTION_NODE;
  static final int PROCINSTR_TYPE = Node.PROCESSING_INSTRUCTION_NODE;
  static final int COMMENT_TYPE =   Node.COMMENT_NODE;
  static final int DOCUMENT_TYPE =  Node.DOCUMENT_NODE;
  static final int DOCTYPE_TYPE =   Node.DOCUMENT_TYPE_NODE;
  static final int NOTATION_TYPE =  Node.NOTATION_NODE;

  // Extra credit: Read the list from a file
  // Super-extra credit: Process a DTD and build the list.
  static String[] treeElementNames = {
        "TesterList",
        "Tester",
        "ClassName",   
        "Description",
        "TestsListRif",
        "TestsList",
        "Test",
        "WhatTest",
        "HowWorkTest",
        "WhenTestPass"
  };
  
  private boolean treeElement(String elementName) {
    for (int i=0; i<treeElementNames.length; i++) {
      if ( elementName.equals(treeElementNames[i]) ) 
      return true;
    }
    return false;
  }

  // This class wraps a DOM node and returns the text we want to
  // display in the tree. It also returns children, index values,
  // and child counts.
  private class AdapterNode { 
    Node domNode;

    // Construct an Adapter node from a DOM node
    public AdapterNode(Node node) {
      domNode = node;
    }

    // Return a string that identifies this node in the tree
    public String toString() {
      String s = typeName[domNode.getNodeType()];
      String nodeName = domNode.getNodeName();
      if (! nodeName.startsWith("#")) {
         if( domNode.getNodeType() == Node.ELEMENT_NODE){
           s = nodeName;
           if(s.equalsIgnoreCase("tester") || s.equalsIgnoreCase("test")){
           	s += ": "+domNode.getAttributes().getNamedItem("name").getNodeValue();
           	String checkSkip = domNode.getAttributes().getNamedItem("skip").getNodeValue();
           	if(checkSkip.equalsIgnoreCase("true")){
           		s += " "+"SKIPPED";
           	}
           }
         }else{
           s += ": " + nodeName;
         }
      }
      if(s.equals("Notation")){
      	s="From XML Tester list:";
      }
      return s;
    }

    public String content() {
      String s = "";
      NodeList nodeList = domNode.getChildNodes();
      for (int i=0; i<nodeList.getLength(); i++) {
        Node node = nodeList.item(i);
        int type = node.getNodeType();
        AdapterNode adpNode = new AdapterNode(node); 
        if (type == ELEMENT_TYPE) {
          // Skip subelements that are displayed in the tree.   
          if ( treeElement(node.getNodeName()) ) continue;

          s += "<" + node.getNodeName() + ">";
          s += adpNode.content();
          s += "</" + node.getNodeName() + ">";
        } else if (type == TEXT_TYPE) {
          s += node.getNodeValue();
        } else if (type == CDATA_TYPE) {
          // The "value" has the text, same as a text node.
          //   while EntityRef has it in a text node underneath.
          //   (because EntityRef can contain multiple subelements)
          // Convert angle brackets and ampersands for display
          StringBuffer sb = new StringBuffer( node.getNodeValue() );
          for (int j=0; j<sb.length(); j++) {
            if (sb.charAt(j) == '<') {
              sb.setCharAt(j, '&');
              sb.insert(j+1, "lt;");
              j += 3;
            } else if (sb.charAt(j) == '&') {
              sb.setCharAt(j, '&');
              sb.insert(j+1, "amp;");
              j += 4;
            }
          }
          s += "<pre>" + sb + "\n</pre>";
        }
				// Ignoring these:
				//   ATTR_TYPE      -- not in the DOM tree
				//   ENTITY_TYPE    -- does not appear in the DOM
				//   PROCINSTR_TYPE -- not "data"
				//   COMMENT_TYPE   -- not "data"
				//   DOCUMENT_TYPE  -- Root node only. No data to display.
				//   DOCTYPE_TYPE   -- Appears under the root only
				//   DOCFRAG_TYPE   -- equiv. to "document" for fragments
				//   NOTATION_TYPE  -- nothing but binary data in here
      }
      return s;
    }

    /*
     * Return children, index, and count values
     */
    public int index(AdapterNode child) {
      //System.err.println("Looking for index of " + child);
      int count = childCount();
      for (int i=0; i<count; i++) {
        AdapterNode n = this.child(i);
        if (child.domNode == n.domNode) return i;
      }
      return -1; // Should never get here.
    }

    public AdapterNode child(int searchIndex) {
      //Note: JTree index is zero-based. 
      Node node = domNode.getChildNodes().item(searchIndex);
//        System.out.println("AdapterNode[child] Preso Nodo: "+node.getNodeName());
      if (compress) {
        // Return Nth displayable node
        int elementNodeIndex = 0;
        for (int i=0; i<domNode.getChildNodes().getLength(); i++) {
          node = domNode.getChildNodes().item(i);
          if (node.getNodeType() == ELEMENT_TYPE 
          		&& treeElement( node.getNodeName() )
		           	&& elementNodeIndex++ == searchIndex) {
		           		
             break; 
          }
        }
      }
      return new AdapterNode(node); 
    }
    
    public int childCount() {
      if (!compress) {
        // Indent this
        return domNode.getChildNodes().getLength();
      } 
      int count = 0;
      for (int i=0; i<domNode.getChildNodes().getLength(); i++) {
         org.w3c.dom.Node node = domNode.getChildNodes().item(i); 
         if (node.getNodeType() == ELEMENT_TYPE && treeElement( node.getNodeName() )) 
         {
           // Note: 
           //   Have to check for proper type. 
           //   The DOCTYPE element also has the right name
           ++count;
         }
      }
      return count;
    } 
  }  // END of Inner class AdapterNode

  // This adapter converts the current Document (a DOM) into 
  // a JTree model. 
  private class DomToTreeModelAdapter implements javax.swing.tree.TreeModel 
  {
    // Basic TreeModel operations
    public Object  getRoot() {
 //     System.err.println("Returning root: " +document);
      
      return new AdapterNode(document);
      
    }
    public boolean isLeaf(Object aNode) {
      // Determines whether the icon shows up to the left.
      // Return true for any node with no children
  //    System.out.println("DomToTreeModelAdapter[isLeaf]");
      AdapterNode node = (AdapterNode) aNode;
        Node n = node.domNode;
   		if(n.getNodeName().equalsIgnoreCase("testslistrif")){
			System.out.println("Chiamato getDocument testListRif ");
		 	Document d = getDocument(n.getFirstChild().getNodeValue());
		 	NodeList nl =  d.getElementsByTagName("TestsList");
			node.domNode = (Node) nl.item(0);
			return false;
   		}else{
	        if (node.childCount() > 0) 
	        	return false;
	        return true;
	      }
    }
    public int getChildCount(Object parent) {
      AdapterNode node = (AdapterNode) parent;
      return node.childCount();
    }

    public Object getChild(Object parent, int index) {
      AdapterNode node = (AdapterNode) parent;
    //  System.out.println("DomToTreeModelAdapter[getChild]: "+node.toString());
      return node.child(index);
    }
    public int getIndexOfChild(Object parent, Object child) {
      AdapterNode node = (AdapterNode) parent;
      return node.index((AdapterNode) child);
    }
    public void valueForPathChanged(TreePath path, Object newValue) {
      // Null. We won't be making changes in the GUI
      // If we did, we would ensure the new value was really new,
      // adjust the model, and then fire a TreeNodesChanged event.
    }

    /*
     * Use these methods to add and remove event listeners.
     * (Needed to satisfy TreeModel interface, but not used.)
     */
    private Vector listenerList = new Vector();
    
    public void addTreeModelListener(TreeModelListener listener) {
      if ( listener != null && ! listenerList.contains( listener ) ) {
         listenerList.addElement( listener );
      }
    }
    
    public void removeTreeModelListener(TreeModelListener listener) {
      if ( listener != null ) {
         listenerList.removeElement( listener );
      }
    }

    // Note: Since XML works with 1.1, this example uses Vector.
    // If coding for 1.2 or later, though, I'd use this instead:
    //   private List listenerList = new LinkedList();
    // The operations on the List are then add(), remove() and
    // iteration, via:
    //  Iterator it = listenerList.iterator();
    //  while ( it.hasNext() ) {
    //    TreeModelListener listener = (TreeModelListener) it.next();
    //    ...
    //  }

    /*
     * Invoke these methods to inform listeners of changes.
     * (Not needed for this example.)
     * Methods taken from TreeModelSupport class described at 
     *   http://java.sun.com/products/jfc/tsc/articles/jtree/index.html
     * That architecture (produced by Tom Santos and Steve Wilson)
     * is more elegant. I just hacked 'em in here so they are
     * immediately at hand.
     */
    public void fireTreeNodesChanged( TreeModelEvent e ) {
      Enumeration listeners = listenerList.elements();
      while ( listeners.hasMoreElements() ) {
        TreeModelListener listener = 
          (TreeModelListener) listeners.nextElement();
        listener.treeNodesChanged( e );
      }
    } 
    public void fireTreeNodesInserted( TreeModelEvent e ) {
      Enumeration listeners = listenerList.elements();
      while ( listeners.hasMoreElements() ) {
         TreeModelListener listener =
           (TreeModelListener) listeners.nextElement();
         listener.treeNodesInserted( e );
      }
    }   
    public void fireTreeNodesRemoved( TreeModelEvent e ) {
      Enumeration listeners = listenerList.elements();
      while ( listeners.hasMoreElements() ) {
        TreeModelListener listener = 
          (TreeModelListener) listeners.nextElement();
        listener.treeNodesRemoved( e );
      }
    }   
    public void fireTreeStructureChanged( TreeModelEvent e ) {
      Enumeration listeners = listenerList.elements();
      while ( listeners.hasMoreElements() ) {
        TreeModelListener listener =
          (TreeModelListener) listeners.nextElement();
        listener.treeStructureChanged( e );
      }
    }
  } //END of Inner class DomToTreeModelAdapter
}
