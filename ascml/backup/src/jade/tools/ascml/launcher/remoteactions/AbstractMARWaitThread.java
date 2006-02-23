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


package jade.tools.ascml.launcher.remoteactions;

import jade.tools.ascml.absmodel.IAbstractRunnable;
import jade.tools.ascml.exceptions.ModelActionException;
import jade.tools.ascml.launcher.AgentLauncher;

public abstract class AbstractMARWaitThread implements Runnable {
	
	public static final int STATUS_RUNNING = 1;
	public static final int STATUS_TIMEDOUT = 2;
	public static final int STATUS_SUCCESSFULL = 3;
	public static final int STATUS_ERROR = 4;
	
	protected int result = 0;
	protected Thread t;
	protected long timeout;	
	protected AgentLauncher al;	
	protected IAbstractRunnable ar;
	protected long start;
	protected ModelActionException mae=null;

	public AbstractMARWaitThread(IAbstractRunnable ar, AgentLauncher al, long timeout) {
		this.ar=ar;
		this.timeout = timeout;		
		this.result = STATUS_RUNNING;
		this.al=al;
		start = System.currentTimeMillis();
		t=new Thread(this,"MARWaitThread: "+ar.getName());
	}


	public void error(ModelActionException e) {
		result = STATUS_ERROR;
		mae=e;
	}

	public void setCompleted() {
		if (result == STATUS_RUNNING) {
			result = STATUS_SUCCESSFULL;
		}
				
		synchronized (t) {	
			t.interrupt();
		}
	}

	public int getResult() {
		return result;
	}

	public void join() throws InterruptedException {		
		t.join();
	}
	
	public ModelActionException getError(){
		return mae;
	}

}
