/**
 * 
 */
package it.pisa.jade.gui;

import javax.swing.DefaultListModel;

/**
 * @author domenico
 * 
 */
@SuppressWarnings("serial")
public class ListModel<E> extends DefaultListModel {

	public ListModel() {
		super();
	}

	@SuppressWarnings("unchecked")
	@Override
	public E get(int index) {
		E e = (E) super.get(index);
		return e;
	}

	@Deprecated
	@Override
	public void addElement(Object obj) {
		super.addElement(obj);
	}

	@Deprecated
	@Override
	public void add(int index, Object element) {
		super.add(index, element);
	}

	public void add(E enume) {
		super.addElement(enume);
	}

	public void addElement(int index, E enume) {
		super.add(index, enume);
	}

	@SuppressWarnings("unchecked")
	@Override
	public E getElementAt(int index) {
		return (E) super.getElementAt(index);
	}

	/**
	 * this method set the array elements
	 * 
	 * @param elements
	 */
	public void setElements(E[] elements) {
		clear();
		for (int i = 0; i < elements.length; i++) {
			add(elements[i]);
		}


	}

}
