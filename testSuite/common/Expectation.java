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

package test.common;
import jade.util.leap.HashMap;

/**
   @author Giovanni Caire - TILAB
 */
public class Expectation {
	private int receivedCnt = 0;
	private HashMap expected = new HashMap();
	
	public Expectation(String[] expectedKeys) {
		for (int i = 0;i < expectedKeys.length; ++i) {
			expected.put(expectedKeys[i], new Boolean(false));
		}
	}
	
	public boolean received(String key) {
		Object obj = expected.get(key);
		if (obj != null) {
			if (!((Boolean) obj).booleanValue()) {
				expected.put(key, new Boolean(true));
				receivedCnt++;
				return true;
			}
		}
		return false;
	}
	
	public boolean isCompleted() {
		return receivedCnt == expected.size();
	}
}