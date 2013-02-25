package jade.misc;

/**
 * Configuration class of a FileManagerClient. For details see the WSDC user guide.
 * <p>
 * The default values are:
 * <li>tmpDir: system property <code>java.io.tmpdir</code>
 *  
 */
public class FileManagerProperties {

		private String tmpDir;
		
		public FileManagerProperties() {
			tmpDir = System.getProperty("java.io.tmpdir");
		}

		public String getTmpDir() {
			return tmpDir;
		}

		public void setTmpDir(String tmpDir) {
			this.tmpDir = tmpDir;
		}
		
	
}
