package it.pisa.jade.agents.chatAgent;

public enum ConstantChat {
	nameService("chat-service"),
	typeService("chat"),
	convesationId("chat-conversationId");
	
	ConstantChat(String s) {
		value = s;
	}

	private String value;

	public String getValue() {
		return value;
	}

}
