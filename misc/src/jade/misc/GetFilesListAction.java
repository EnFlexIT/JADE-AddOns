package jade.misc;

import jade.content.AgentAction;
import jade.content.onto.annotations.Slot;

public class GetFilesListAction implements AgentAction {

	private static final long serialVersionUID = 2023397800948842925L;

	private String dirPathName;

	public GetFilesListAction() {
	}
	
	public GetFilesListAction(String dirPathName) {
		this.dirPathName = dirPathName;
	}

	@Slot(mandatory=false)
	public String getDirPathName() {
		return dirPathName;
	}

	public void setDirPathName(String dirPathName) {
		this.dirPathName = dirPathName;
	}
}
