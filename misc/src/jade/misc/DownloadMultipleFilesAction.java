package jade.misc;

import java.util.List;

import jade.content.AgentAction;
import jade.content.onto.annotations.Slot;

public class DownloadMultipleFilesAction implements AgentAction {

	private static final long serialVersionUID = -6293958640491034610L;

	private List<String> filesPathName;

	public DownloadMultipleFilesAction() {
	}
	
	public DownloadMultipleFilesAction(List<String> filesPathName) {
		this.filesPathName = filesPathName;
	}

	@Slot(mandatory=true)
	public List<String> getFilesPathName() {
		return filesPathName;
	}

	public void setFilesPathName(List<String> filesPathName) {
		this.filesPathName = filesPathName;
	}
}
