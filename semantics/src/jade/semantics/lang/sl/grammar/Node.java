
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


//-----------------------------------------------------
// This file has been automatically produced by a tool.
//-----------------------------------------------------

package jade.semantics.lang.sl.grammar;


import java.util.*;
import java.lang.reflect.*;
/**
This abstract class is the base class of all nodes belonging to a directed graph
 representing a particular abstract syntax tree. It provides all basic mechanisms
needed to manipulate a node.
*/
public abstract class Node {
    /**
    This interface defines node operations that can be redefined
     using the <b><code>addOperations</code></b> method.
    Notice the <b><code>addOperations</code></b> is a class method that 
    must be called for a particular class before creating any node of this class.
    */
    public interface Operations {
        public boolean equals(Node node1,Node node2);
        public String toString(Node node);
        public void initNode(Node node);
    }
    static java.util.HashMap _operations = new java.util.HashMap();
    Node.Operations _thisoperations = null;
    /**
    This method allows to redefine some operations for a particular class of nodes.
    @param nodeClass the class the operations of which we want to redefine.
    @param operations the new operations definition.
    */
    public static void addOperations(Class nodeClass, Node.Operations operations){
        _operations.put(nodeClass, operations);
    }
    /**
    This method allows to redefine operations on several node classes.
    @param class_operations_array an array containing alternatively a node class
     and a Node.Operations class.
    @see Node#addOperations(Class, Node.Operations)
    */
    public static void installOperations(Object[] class_operations_array){
        // class_operations_array is an array alterning Class anf Node.Operations objects.
        _operations.clear();
        for (int i=0; i<class_operations_array.length; i++) {
            _operations.put(class_operations_array[i++], class_operations_array[i]);
        }
    }
    protected Node[] _nodes = new Node[0];
    protected Node(int capacity) {
        _nodes = new Node[capacity];
        _thisoperations = (Node.Operations)_operations.get(getClass());
    }
    /**
    This method returns the children of a the node.
    @return chidlren of the node 
    */
    public Node[] children() {return _nodes;}
    /**
    This method allow to replace a child of the node. Notice
     this method performs no check on the type of the new node.
    For this reason, the use of a <b>as_</b> method should be preferred each
     time it is possible.
    @param index the index of the child to replace.
    @param node the new node.
    */
    public void replace(int index, Node node) {_nodes[index] = node;}
    /**
    This method dumps the graph this node of which is the root. 
    @param tab a prefix string used to display each node of the graph.
    */
    public void dump(String tab) {
        System.out.println(tab+getClass().getName());
        for (int i=0; i<_nodes.length; i++) {
            if (_nodes[i] != null ) _nodes[i].dump(tab+"  ");
        }
    }
    /**
    This method is part of the implementation of the visitor design pattern. 
    @param v the visitor to apply on chidlren.
    */
    public void childrenAccept(Visitor v) {
        for (int i=0; i<_nodes.length; i++) {
            if (_nodes[i] != null ) _nodes[i].accept(v);
        }
    }
    /**
    This method return a clone of the graph this node of which is the root.
    @return a new recreated graph.
    */
    public abstract Node getClone();
    /**
    This method replace the graph entirely with the other graph the root is n.
    @param n the root node of the new graph.
    */
    public void copyValueOf(Node n) {
        for (int i=0; i<_nodes.length; i++) {
            _nodes[i]= n._nodes[i].getClone();
        }
    }
    Vector _observers = null;
    /**
    This method call the <b>nodeChanged</b> method on each observer 
    attached to this node.
    */
    public void notifyChanges() {
        if ( _observers != null ) {
            for (int i = 0;i<_observers.size();i++) {
                ((NodeObserver)_observers.elementAt(i)).nodeChanged(this);
            }
        }
    }
    /**
    This method allows to add an observer for this node.
    @param observer the observer to add.
    */
    public void addObserver(NodeObserver observer) {
        if ( _observers == null ) {
            _observers = new Vector();
        }
        _observers.addElement(observer);
    }
    /**
    This method allows to remove an observer of this node.
    @param observer the observer to remove.
    */
    public void removeObserver(NodeObserver observer) {
        if ( _observers != null ) {
            _observers.removeElement(observer);
        }
    }
    /**
    This method is part of the implementation of the visitor design pattern. 
    @param visitor the visitor to apply on this node.
    */
    public abstract void accept(Visitor visitor);
    /**
    This method fills the result list with all nodes of the graph  
    instance of the <b>nodeClass</b>.  
    @param nodeClass the name of the class the return nodes must be instance of.
    @param result the result list.
    @return true if the list is not empty.
    */
    public boolean childrenOfKind(String nodeClass, ListOfNodes result) {
        try {
            return childrenOfKind(Class.forName(nodeClass), result);
        }
        catch (ClassNotFoundException cnf) {return false;}
    }
    /**
    This method fills the result list with all nodes of the graph  
    instance of the <b>nodeClass</b>.  
    @param nodeClass the class the return nodes must be instance of.
    @param result the result list.
    @return true if the returned list is not empty.
    */
    public boolean childrenOfKind(Class nodeClass, ListOfNodes result) {
        if ( nodeClass.isInstance(this) ) {
            result.add(this);
        }
        for (int i=0; i<_nodes.length; i++) {
            if (_nodes[i] != null ) _nodes[i].childrenOfKind(nodeClass, result);
        }
        return (result.size() != 0);
    }
    /**
    This method fills the result list with all nodes of the graph  
    instance of the <b>nodeClass</b>, holding an attribute the value 
    of which is the <b>value</b> parameter. 
    @param nodeClass the class the return nodes must be instance of.
    @param attribut the name of the attribute.
    @param value the expected value of this attribute for the return nodes.
    @param result the result list.
    @param all if true, this method looks for all nodes satisfaying the constraints,
    otherwise it return the first found node.
    @return true if the returned list is not empty.
    */
    public boolean find(Class nodeClass, String attribut, Object value, ListOfNodes result, boolean all) {
        dofind(nodeClass, attribut, value, result, all);
        return (result.size() != 0);
    }
    protected void dofind(Class nodeClass, String attribut, Object value, ListOfNodes result, boolean all) {
        try {
            if ( nodeClass.isInstance(this) ) {
                Method getAttributMethod = getClass().getMethod(attribut, new Class[0]);
                if ( getAttributMethod != null ) {
                    Object attributValue = getAttributMethod.invoke(this, new Object[0]);
                    if ( attributValue == value ||
                         attributValue != null &&  attributValue.equals(value) ) {
                        result.add(this);
                    }
                }
            }
        }
        catch (NoSuchMethodException nse) {}
        catch (IllegalAccessException iae) {}
        catch (InvocationTargetException ite) {}
        for (int i=0; i<_nodes.length && (all || result.size()==0); i++) {
            if (_nodes[i] != null ) _nodes[i].dofind(nodeClass, attribut, value, result, all);
        }
    }
    /**
    This method return if the node equals another node.  
    @param object the node to compare with.
    @return true if the 2 nodes are equal.
    */
    public boolean equals(Object object) {
        if ( _thisoperations == null ) {
            _thisoperations = (Node.Operations)_operations.get(getClass());
        }
        if ( _thisoperations != null ) {
            return _thisoperations.equals(this, (Node)object);
        }
        else {
            return super.equals(object);
        }
    }
    /**
    This method return a string representing the node.  
    @return the image string of the node.
    */
    public String toString() {
        if ( _thisoperations == null ) {
            _thisoperations = (Node.Operations)_operations.get(getClass());
        }
        if ( _thisoperations != null ) {
            return _thisoperations.toString(this);
        }
        else {
            return super.toString();
        }
    }
    /**
    This method is called to initialize the current node if needed.  
    In particular, it should be redefined to perform correct initialization of the semantic attributes. 
    */
    public void initNode() {
        if ( _thisoperations == null ) {
            _thisoperations = (Node.Operations)_operations.get(getClass());
        }
        if ( _thisoperations != null ) {
            _thisoperations.initNode(this);
        }
    }
}
