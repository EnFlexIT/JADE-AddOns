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
package it.pisa.jade.gui;

import javax.swing.DefaultListModel;

/**
 * @author Domenico Trimboli
 * @author Fabrizio Marozzo
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
