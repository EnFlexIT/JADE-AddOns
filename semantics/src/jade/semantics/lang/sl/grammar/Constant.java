
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


//-----------------------------------------------------
// This file has been automatically produced by a tool.
//-----------------------------------------------------

package jade.semantics.lang.sl.grammar;

public abstract class Constant extends Term
{
        public interface Operations extends Term.Operations
        {
            public Long intValue(Constant node);
            public Double realValue(Constant node);
            public String stringValue(Constant node);
            public byte[] byteValue(Constant node);
        }
        public Long intValue()
        {
            
            if ( _thisoperations == null ) {
                _thisoperations = getOperations();
            }
            return((Constant.Operations)_thisoperations).intValue(this );
        }
        public Double realValue()
        {
            
            if ( _thisoperations == null ) {
                _thisoperations = getOperations();
            }
            return((Constant.Operations)_thisoperations).realValue(this );
        }
        public String stringValue()
        {
            
            if ( _thisoperations == null ) {
                _thisoperations = getOperations();
            }
            return((Constant.Operations)_thisoperations).stringValue(this );
        }
        public byte[] byteValue()
        {
            
            if ( _thisoperations == null ) {
                _thisoperations = getOperations();
            }
            return((Constant.Operations)_thisoperations).byteValue(this );
        }
    public static Integer ID = new Integer(10015);
    public int getClassID() {return ID.intValue();}

    public Constant(int capacity)  {
      super (capacity);
    }

    public void copyValueOf(Node n) {
        if (n instanceof Constant) {
            super.copyValueOf(n);
            Constant tn = (Constant)n;
        }
    }

    public Node.Operations getOperations() {
        Node.Operations result = (Node.Operations)_operations.get(ID);
        if ( result == null ) {result = super.getOperations();}
        return result;
    }
}