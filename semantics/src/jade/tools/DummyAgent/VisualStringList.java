/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2004 France T�l�com

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
 * VisualStringList.java
 * Created on 5 oct. 2005
 * Author : Vincent Pautret
 */
package jade.tools.DummyAgent;

import jade.gui.StringDlg;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.Iterator;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

/**
 * @author Vincent Pautret - France Telecom
 * @version Date:  Revision: 1.0
 */
public class VisualStringList extends JPanel
{
    /**
  @serial
  */
  Component owner;
    
    /**
  @serial
  */
  JList genericList;
    /**
  @serial
  */
  JScrollPane pane;
    /**
  @serial
  */
  DefaultListModel listModel;
    /**
  @serial
  */
  JPopupMenu popUp;
    /**
  @serial
  */
  JMenuItem editItem;
    /**
  @serial
  */
  JMenuItem addItem;
    /**
  @serial
  */
  JMenuItem removeItem;
    
    static String VIEW_LABEL = "View";
    static String EDIT_LABEL = "Edit";
    static String ADD_LABEL = "Add";
    static String REMOVE_LABEL = "Remove";
    
    /**
    * Constructor of a panel showing a list of strings.
    * By default the panel is editable and all the three operations (Add - Edit - Remove) are allowed.
    *
    * @param content an iterator of the items to show in the panel
    *
    */
    
    VisualStringList(Iterator content,Component ownerGui)
    {
    
        owner = ownerGui;
        listModel = new DefaultListModel();
        try
      {
        while(content.hasNext())
        {
            listModel.addElement(content.next());
        }
            
      }catch(Exception e){e.printStackTrace();}
        
      
      genericList = new JList(listModel);
      genericList.setCellRenderer(new VisualListCellRenderer(this));
      genericList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      genericList.setSelectedIndex(0);
      
      pane = new JScrollPane(genericList);
      
      genericList.setToolTipText("Right mouse click to show the popup menu");
      genericList.addMouseListener(new PopupMouser());
        
        popUp = new JPopupMenu();
        
        popUp.setLightWeightPopupEnabled(false);
        
        //initilized with poUp editable
        editItem = new JMenuItem(EDIT_LABEL);
        editItem.addActionListener(new ActionListener()
          {
            public void actionPerformed(ActionEvent e)
            {
                String param = e.getActionCommand();
                if(param.equals(EDIT_LABEL))
                    editAction();
                else
                if(param.equals(VIEW_LABEL))
                    viewAction();   
            }     
        });

        popUp.add(editItem);
        
      
        addItem = new JMenuItem(ADD_LABEL);
      addItem.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                String param = e.getActionCommand();
                if(param.equals(ADD_LABEL))
                {       
                    Object el = editElement(null,true);
                    
                    if(el != null)
                        addElement(el);
                
                }
            
            }     
        });
      popUp.insert(addItem,0);
        
      removeItem = new JMenuItem(REMOVE_LABEL);
      removeItem.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            { 
                String param = e.getActionCommand();
                if(param.equals(REMOVE_LABEL))
                    {
                        if(genericList.getModel().getSize() > 0)
                        {
                            Object el =  genericList.getSelectedValue();
                          if(el != null)
                            removeElement(el);
                        }
                        
                    }
            }
        
        });
      popUp.insert(removeItem,2);
        
      this.add(pane);
    }
    
    
    private void viewAction()
    {
        if(genericList.getModel().getSize() > 0)
        {
            Object el = genericList.getSelectedValue();
            if(el != null)
                editElement(el,false);
        }
    }
    
    private void editAction()
    {
        if(genericList.getModel().getSize() > 0)
      {
        Object el = genericList.getSelectedValue();
        int index = genericList.getSelectedIndex();
                    
        if(el != null)
        {
          Object new_el = editElement(el,true);
            if(new_el != null)
              listModel.set(index, new_el);
        }
      }
    }
    
    /**
    * Use this method to enable/disable the Add and Remove fields of the popUp menu.
    */
    @Override
	public void setEnabled(boolean enabled)
    {
        super.setEnabled(enabled);
    
        addItem.setEnabled(enabled);
        removeItem.setEnabled(enabled);
        
        if(!enabled)
            editItem.setText(VIEW_LABEL);
            
    }
    
    
    
    /** 
    * This method returns the string that will be shown on the panel.
    * Can be override by subclasses for more complex elements.
    */
    protected String getElementName(Object el)
    {
        return el.toString();
    }
    
    //Inserts a new element in the list
    private void addElement(Object el)
    {
        listModel.addElement(el);
    }
    
    //Removes an element from the list
    protected void removeElement(Object el)
    {
        listModel.removeElement(el);
    }
    
    //Reset the items to show in the panel
    protected void resetContent(Iterator content)
    {
        listModel = new DefaultListModel();
        try
      {
        while(content.hasNext())
        {
            listModel.addElement(content.next());
        }
            
      }catch(Exception e){e.printStackTrace();}
      
      genericList.setModel(listModel);

    }
    
    //Can be override by subclasses. Default implementation is for string
    protected Object editElement(Object el, boolean isEditable)
    {
        Object out = null;
        
        if(isEditable)
        {

            StringDlg dlgString = new StringDlg(owner,"Insert a new value:");
      out = dlgString.editString((String)el);
      
        }
        else
        {

            StringDlg dlgString = new StringDlg(owner,"Value:");
      dlgString.viewString((String)el);
      
        }
    
        return out;
        
    }
    
    /**
    * Returns the list of items 
    */
    public Enumeration getContent()
    {
        return (listModel.elements());
    }
    
    /**
    * Sets the dimension of the panel.
    */
    public void setDimension(Dimension d)
    {
        pane.setPreferredSize(d);
      pane.setMinimumSize(d);
      pane.setMaximumSize(d);

    }

    
    private class VisualListCellRenderer extends DefaultListCellRenderer 
    {
        private VisualStringList myList;
        VisualListCellRenderer( VisualStringList l) 
        {
            super();
          myList = l;
        }
        
      @Override
	public Component getListCellRendererComponent(
          JList list,
          Object value,
          int index,
          boolean isSelected,
          boolean cellHasFocus)
      {
            Font courier = new Font("Courier", Font.BOLD, 12);
            setFont(courier);
            setText(myList.getElementName(value));
          setBackground(isSelected ? Color.lightGray : Color.white);
          
            return this;
        }
    }
    
          
  private class PopupMouser extends MouseAdapter
  {
    
    public PopupMouser(){}

    @Override
	public void mouseReleased(MouseEvent e) {
        if (e.isPopupTrigger())
         popUp.show(e.getComponent(), e.getX(), e.getY());

    }

    @Override
	public void mousePressed(MouseEvent e) {
        if (e.isPopupTrigger())
           popUp.show(e.getComponent(), e.getX(), e.getY());

    }
    
    @Override
	public void mouseClicked(MouseEvent e){
        if(e.getClickCount() == 2)
        {
            if(e.getComponent().getParent().getParent().getParent().isEnabled())
                ((VisualStringList)e.getComponent().getParent().getParent().getParent()).editAction();
            else
              ((VisualStringList)e.getComponent().getParent().getParent().getParent()).viewAction();
              
        }
    }
}   
} 
