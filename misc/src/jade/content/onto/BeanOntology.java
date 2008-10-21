package jade.content.onto;

//#J2ME_EXCLUDE_FILE
//#APIDOC_EXCLUDE_FILE

public class BeanOntology extends Ontology {
	private static final long serialVersionUID = 1L;

	private BeanOntologyBuilder bob;

	public BeanOntology(String name) {
		this(name, BasicOntology.getInstance());
	}

	public BeanOntology(String name, Ontology base) {
		this(name, new Ontology[] { base });
	}

	public BeanOntology(String name, Ontology[] base) {
		super(name, base, new BeanIntrospector());
		bob = new BeanOntologyBuilder(this);
	}

	public void add(Class clazz) throws BeanOntologyException {
		add(clazz, false);
	}

	public void add(String pkgname) throws BeanOntologyException {
		add(pkgname, false);
	}

	public void add(Class clazz, boolean buildHierarchy) throws BeanOntologyException {
		bob.addSchema(clazz, buildHierarchy);
	}

	public void add(String pkgname, boolean buildHierarchy) throws BeanOntologyException {
		bob.addSchemas(pkgname, buildHierarchy);
	}
}
