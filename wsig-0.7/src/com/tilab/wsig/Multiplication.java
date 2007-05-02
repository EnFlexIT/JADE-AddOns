package com.tilab.wsig;

import jade.content.AgentAction;
import jade.util.leap.List;

/**
 * Created by IntelliJ IDEA.
 * User: ue_marchetti
 * Date: 6-feb-2007
 * Time: 9.21.51
 * To change this template use File | Settings | File Templates.
 */
public class Multiplication implements AgentAction {

	private List numbers;

	public List getNumbers() {
		return numbers;
	}

	public void setNumbers(List numbers) {
		this.numbers = numbers;
	}

}
