package jade.semantics.lang.sl.content;

import jade.semantics.lang.sl.grammar.Content;
import jade.semantics.lang.sl.parser.ParseException;
import jade.semantics.lang.sl.parser.SLParser;
import jade.semantics.lang.sl.parser.SLUnparser;

import java.io.StringWriter;

public class DefaultContentParser implements ContentParser {

	/**
	 * @return the language handled by this content manager
	 */
	public String getLanguage() {return "fipa-sl";}

	/**
	 * @param message the message the content of which to extract
	 * @return the SL content extracted from the message.
	 * @throws ExctractContentException
	 */
	public Content parseContent(String foreignContent) 
	  	throws ParseContentException {
		try {
			return SLParser.getParser().parseContent(foreignContent);
		} catch (ParseException e) {
			throw new ParseContentException();
		}
	}

	/**
	 * @param message the message the content of which is filled
	 * @param c the content to fill the message
	 * @throws UnparseContentException
	 */
	public String unparseContent(Content content)
		throws UnparseContentException {
//		return content.toString();
		try {
			StringWriter writer = new StringWriter();
			new SLUnparser(writer).unparseTrueSL(content);
			return writer.toString();
		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
