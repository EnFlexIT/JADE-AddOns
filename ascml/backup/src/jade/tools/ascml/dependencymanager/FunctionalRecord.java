package jade.tools.ascml.dependencymanager;

import jade.tools.ascml.absmodel.*;
import jade.tools.ascml.onto.*;

import java.util.HashSet;

public class FunctionalRecord extends AbstractDependencyRecord {

	public FunctionalRecord(IAbstractRunnable absRunnable) {
		super(absRunnable);
	}

	protected void checkStatus() {
		if (neededWatcherTypes.size() == 0 && neededAgentNames.size() == 0 && neededSocietyNames.size() == 0) {
			if (!absRunnable.getStatus().equals(new Functional())) {
				absRunnable.setStatus(new Functional());
				absRunnable.setDetailedStatus("The instance successfully started and is now running.");				
			}
		} else {
			if (absRunnable.getStatus().equals(new Functional())) {
				absRunnable.setStatus(new NonFunctional());
			}
		}
	}
}
