package jade.semantics.lang.sl.content;

import jade.semantics.lang.sl.grammar.Content;

public interface ContentParser {
	
	/**
	 * @return the language handled by this content manager
	 */
	String getLanguage();
	
	/**
	 * @param message the message the content of which to extract
	 * @return the SL content extracted from the message.
	 * @throws ExctractContentException
	 */
	Content parseContent(String foreignContent) throws ParseContentException;
	
	/**
	 * @param message the message the content of which is filled
	 * @param c the content to fill the message
	 * @throws UnparseContentException
	 */
	String unparseContent(Content content) throws UnparseContentException;
}
