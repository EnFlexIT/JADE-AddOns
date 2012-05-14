package jade.misc;

import jade.content.AgentAction;
import jade.content.onto.annotations.Slot;

public class DownloadFileAction implements AgentAction {

	private static final long serialVersionUID = -6293958640491034610L;

	private String filePathName;

	public DownloadFileAction() {
	}
	
	public DownloadFileAction(String filePathName) {
		this.filePathName = filePathName;
	}

	@Slot(mandatory=true)
	public String getFilePathName() {
		return filePathName;
	}

	public void setFilePathName(String filePathName) {
		this.filePathName = filePathName;
	}
}
