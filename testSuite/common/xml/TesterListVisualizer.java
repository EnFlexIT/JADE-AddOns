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
		document = XMLManager.getDocument(xmlFileName);
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
					FunctionalityDescriptor func = XMLManager.getFunctionalityDescriptor(el);
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

  // The set of TestSuite tags that must not be displayed as 
  // tree elements.
  private static String[] nonTreeElementTags = {
			 	// The FUNC_DESCRIPTION_TAG and TEST_WHAT_TAG are not 
				// visualized in the tree as their value is displayed in 
				// the left part when the corresponding functionality/test
				// is selected.
        XMLManager.FUNC_DESCRIPTION_TAG,
        XMLManager.TEST_WHAT_TAG
  };
  
  private boolean isNonTreeElementTag(String tag) {
    for (int i = 0; i < nonTreeElementTags.length; i++) {
      if (tag.equalsIgnoreCase(nonTreeElementTags[i])) { 
      	return true;
      }
    }
    return false;
  }

  /**
     Inner class AdapterNode.
     This class wraps a DOM node into an Object that has a 
     String representation consistent with what we want to display 
     in the tree.
     Some utility methods to provide children, index values and 
     child counts are also provided.
   */
  private class AdapterNode { 
    Node domNode;

    /**
       Construct an AdapterNode wrapping a given DOM node
     */
    public AdapterNode(Node node) {
      domNode = node;
    }

    /**
       Return a String representation of the wrapped node consistent 
       with what we want to display in the tree. 
     */
    public String toString() {
    	// Default value
      String s = typeName[domNode.getNodeType()];
      
			if (domNode instanceof Element){
				Element e = (Element) domNode;
				String tag = e.getTagName();
				if(tag.equalsIgnoreCase(XMLManager.FUNC_TAG) || tag.equalsIgnoreCase(XMLManager.TEST_TAG)) {
					// The string representation is: <name-attribute> [SKIPPED]
					s = e.getAttribute(XMLManager.NAME_ATTR);
					if ("true".equalsIgnoreCase(e.getAttribute(XMLManager.SKIP_ATTR))) {
						s += " SKIPPED";
					}
				}
				else if (tag.equalsIgnoreCase(XMLManager.TEST_ARG_TAG)) {
					// The string representation is: Arg: <key-attribute> = <value-attribute>
					s = "Arg: "+e.getAttribute(XMLManager.KEY_ATTR)+" = "+e.getAttribute(XMLManager.VALUE_ATTR);
				}
				else {
					// The string representation is just the tag
					s = tag;
				}
			}
			
      if(s.equals("Notation")){
      	// The root
      	s="JADE Test Suite";
      }
      return s;
    }

    /**
       Return the content to be displayed in the right part of the 
       test selection window when the node wrapped by this 
       <code>AdapterNode</code> is selected. 
     */
    public String content() {
    	String s = "";
    	if (domNode instanceof Element) {
    		Element e = (Element) domNode;
    		String tag = e.getTagName();
				if (tag.equalsIgnoreCase(XMLManager.FUNC_TAG)) {
					// Display the content of the <Description> sub-element
					s = XMLManager.getContent(XMLManager.getSubElement(e, XMLManager.FUNC_DESCRIPTION_TAG));
				}
				else if (tag.equalsIgnoreCase(XMLManager.TEST_TAG)) {
					// Display the content of the <What> sub-element
					s = XMLManager.getContent(XMLManager.getSubElement(e, XMLManager.TEST_WHAT_TAG));
				}
				else {
					// Display the content of this element
					s = XMLManager.getContent(e);
				}
    	}
    	return s;
					
      /*String s = "";
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
      */
    }

    /*
     * Return children, index, and count values
     */
    public int index(AdapterNode child) {
      /*System.err.println("Looking for index of " + child);
      int count = childCount();
      for (int i=0; i<count; i++) {
        AdapterNode n = this.child(i);
        if (child.domNode == n.domNode) return i;
      }
      return -1; // Should never get here.
      */
      int count = 0;
      NodeList nl = domNode.getChildNodes();
      int length = nl.getLength();
      for (int i = 0; i < length; i++) {
      	Node n = nl.item(i); 
      	if (n.getNodeType() == ELEMENT_TYPE) {
      		String tag = n.getNodeName();
      		if (XMLManager.isTestSuiteTag(tag) && !isNonTreeElementTag(tag)) {
      			if (n == child.domNode) {
      				return count;
      			}
      		}
      	}
      }
      return -1;
    }

    public AdapterNode child(int searchIndex) {
      int count = 0;
      NodeList nl = domNode.getChildNodes();
      int length = nl.getLength();
      for (int i = 0; i < length; i++) {
      	Node n = nl.item(i); 
      	if (n.getNodeType() == ELEMENT_TYPE) {
      		String tag = n.getNodeName();
      		if (XMLManager.isTestSuiteTag(tag) && !isNonTreeElementTag(tag)) {
      			if (count == searchIndex) {
      				return new AdapterNode(n);
      			}
	        	++count;
      		}
      	}
      }
      return null;
      /*
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
      */
    }
    
    public int childCount() {
      //if (!compress) {
        // Indent this
      //  return domNode.getChildNodes().getLength();
      //} 
      int count = 0;
      NodeList nl = domNode.getChildNodes();
      int length = nl.getLength();
      for (int i = 0; i < length; i++) {
      	Node n = nl.item(i); 
      	if (n.getNodeType() == ELEMENT_TYPE) {
      		String tag = n.getNodeName();
      		if (XMLManager.isTestSuiteTag(tag) && !isNonTreeElementTag(tag)) {
	        	++count;
      		}
      	}
      }
      return count;
    } 
  }  // END of Inner class AdapterNode

  /**
     Inner class DomToTreeModelAdapter.
     This adapter converts the current Document (a DOM) into 
     a JTree model.
   */
  private class DomToTreeModelAdapter implements javax.swing.tree.TreeModel 
  {
    /**
       Retrieve the root element of the tree
     */
    public Object  getRoot() {
      return new AdapterNode(document);
    }
    
    /**
       Detect whether or not a given element is a leaf in the tree
     */
    public boolean isLeaf(Object aNode) {
      // Return true for any node with no children
      AdapterNode node = (AdapterNode) aNode;
      Node n = node.domNode;
   		if (n.getNodeName().equalsIgnoreCase(XMLManager.FUNC_TESTSLIST_TAG)) {
		 		Document d = XMLManager.getDocument(XMLManager.getContent((Element) n));
		 		if (d == null) {
		 			return true;
		 		}
		 		// Replace the DOM node representing the Tests-list filename with
		 		// the DOM node representing the document inside the Tests-list file
		 		NodeList nl =  d.getElementsByTagName(XMLManager.TESTS_TAG);
				node.domNode = (Node) nl.item(0);
		 		return false;
   		}
   		else {
	    	return (node.childCount() == 0);
	    }
    }
    
    /**
       Return the number of child elements of an element in the tree
     */
    public int getChildCount(Object parent) {
      AdapterNode node = (AdapterNode) parent;
      return node.childCount();
    }

    /**
       Return the child of a given element at position <code>index</code>
     */
    public Object getChild(Object parent, int index) {
      AdapterNode node = (AdapterNode) parent;
      return node.child(index);
    }
    
    /**
       Return the index of a child of a given element in the tree
     */
    public int getIndexOfChild(Object parent, Object child) {
      AdapterNode node = (AdapterNode) parent;
      return node.index((AdapterNode) child);
    }
    
    public void valueForPathChanged(TreePath path, Object newValue) {
      // Null. We won't be making changes in the GUI
      // If we did, we would ensure the new value was really new,
      // adjust the model, and then fire a TreeNodesChanged event.
    }

    //////////////////////////////////////////////////////
    // The following methods are only needed to implement 
    // the TreeModel interface
    //////////////////////////////////////////////////////
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
