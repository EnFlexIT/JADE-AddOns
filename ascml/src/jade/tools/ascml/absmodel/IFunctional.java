package jade.tools.ascml.absmodel;

import java.util.Vector;

/**
 * 
 */
public interface IFunctional
{
	/**
	 * Get all the invariants defined for the functional-state.
	 * @return a String-Vector containing the functional-invariants.
	 */
	public Vector<String> getInvariants();

	/**
	 * Add an invariant for the functional-state.
	 * @param invariant  A String representing the invariant.
	 */
	public void addInvariant(String invariant);

	/**
	 * Get all the dependencies on which the functional-state relies.
	 * @return  A Vector containing Dependency-Objects.
	 */
	public Vector<IDependency> getDependencies();

	/**
	 * Add a dependency on which the functional-state relies.
	 * @param dependency  A Dependency-model.
	 */
	public void addDependency(IDependency dependency);
}
