package jade.tools.ascml.dependencymanager;

import jade.tools.ascml.absmodel.*;
import jade.tools.ascml.onto.*;

import java.util.HashSet;

public class DependencyRecord extends AbstractDependencyRecord{

	public DependencyRecord(IAbstractRunnable absRunnable) {
		super(absRunnable);
	}

	protected void checkStatus() {
		if (neededWatcherTypes.size() == 0 && neededAgentNames.size() == 0 && neededSocietyNames.size() == 0) {
			//All Deps fulfilled
			if (absRunnable.getStatus()==null) {
				absRunnable.setStatus(new Starting());
			}
		}
	}
}
