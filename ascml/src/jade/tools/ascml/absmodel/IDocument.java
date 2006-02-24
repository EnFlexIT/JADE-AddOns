package jade.tools.ascml.absmodel;

/**
 * 
 */
public interface IDocument
{
	String SOURCE_UNKNOWN = "Source Unknown";

	/**
	 *  Set the source (path + file-name).
	 *  @param source  The source-object.
	 */
	void setSource(String source);

	/**
	 *  Get the source (path + file-name).
	 *  @return  The source-object.
	 */
	String getSource();

	/**
	 * Get the source-path.
	 * If the source is a file, than the path within the file-system is returned.
	 * @return  The source-path.
	 */
	String getSourcePath();

	/**
	 * Get the source-name.
	 * If the source is a file, than the file-name (without it's path) is returned.
	 * @return  The source-path.
	 */
	String getSourceName();
}
