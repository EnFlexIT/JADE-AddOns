package it.pisa.jade.agents.peerAgent.ontologies;import jade.content.Concept;/** *  * @author Fabrizio Marozzo * */@SuppressWarnings("serial")public class Found implements Concept {		private String searchKey;	private String name;	private String url;	private String type;	private String extension;	private String agent;		private String summary;	public String getSummary() {		return summary;	}	public void setSummary(String summary) {		this.summary = summary;	}	public String getAgent() {		return agent;	}	public void setAgent(String agent) {		this.agent = agent;	}	public String getExtension() {		return extension;	}	public void setExtension(String extension) {		this.extension = extension;	}	public String getName() {		return name;	}	public void setName(String name) {		this.name = name;	}	public String getType() {		return type;	}	public void setType(String type) {		this.type = type;	}	public String getUrl() {		return url;	}	public void setUrl(String url) {		this.url = url;	}	public String getSearchKey() {		return searchKey;	}	public void setSearchKey(String searchKey) {		this.searchKey = searchKey;	}				}