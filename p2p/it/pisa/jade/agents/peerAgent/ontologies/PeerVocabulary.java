package it.pisa.jade.agents.peerAgent.ontologies;/** *  * @author Fabrizio Marozzo * */public interface PeerVocabulary {   //-------> Basic vocabulary	public static final String TYPE_SERVICE="P2P-service";	public static final String NAME_SERVICE="P2P";		public static final String TYPE_FILE_DOCUMENT="type-file-document";	public static final String TYPE_FILE_MUSIC="type-file-music";	public static final String TYPE_FILE_IMAGE="type-file-image";	public static final String TYPE_FILE_ARCHIVE="type-file-archive";   //-------> Ontology vocabulary	//LookFor Message   public static final String LOOKFOR = "LookFor";   public static final String LOOKFOR_SEARCH_KEY = "searchKey";   public static final String LOOKFOR_SEARCH_STRING = "searchString";   public static final String LOOKFOR_EXTENSION = "extension";   public static final String LOOKFOR_TYPE = "type";   	//Found Message   public static final String FOUND = "Found";   public static final String FOUND_SEARCH_KEY = "searchKey";   public static final String FOUND_NAME = "name";   public static final String FOUND_URL= "url";   public static final String FOUND_TYPE = "type";   public static final String FOUND_EXTENSION = "extension";   public static final String FOUND_AGENT = "agent";   public static final String FOUND_SUMMARY = "summary";      //NoFound Message   public static final String NOFOUND = "NoFound";   public static final String NOFOUND_SEARCH_KEY = "searchKey";            //Choose Message   public static final String CHOOSE = "Choose";   public static final String CHOOSE_SEARCH_KEY = "searchKey";   public static final String CHOOSE_URL = "url";   public static final String CHOOSE_ADDRESS_IP = "ip";   public static final String CHOOSE_ADDRESS_PORT = "port";         //RefuseChoose Message   public static final String REFUSECHOOSE = "RefuseChoose";   public static final String REFUSECHOOSE_SEARCH_KEY = "searchKey";   public static final String REFUSECHOOSE_URL = "url";   // RefuseChoose Message   public static final String AGREECHOOSE = "RefuseChoose";   public static final String AGREECHOOSE_SEARCH_KEY = "searchKey";   public static final String AGREECHOOSE_URL = "url";      }