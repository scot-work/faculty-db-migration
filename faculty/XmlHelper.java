package faculty;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
//import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

//import org.w3c.dom.Attr;
import org.w3c.dom.Comment;
//import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
//import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.ProcessingInstruction;

public class XmlHelper {

	

	/*public static void main(String argv[]) {

		try {

			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			// root elements
			Document doc = docBuilder.newDocument();
			doc.setXmlStandalone(true);
			Element document = doc.createElement("document");
			ProcessingInstruction pi = doc.createProcessingInstruction("pcf-stylesheet", "path=\"/_resources/xsl/default.xsl\" site=\"templates\" extension=\"html\"");

			Element headcode = doc.createElement("headcode");
			Element bodycode = doc.createElement("bodycode");
			Element footcode = doc.createElement("footcode");
			Element config = doc.createElement("config");
			Element mainContent = doc.createElement("maincontent");
			Element metadata = doc.createElement("metadata");
			Element columnOne = doc.createElement("column_one");
			Comment openComment = doc.createComment(StringConstants.OMNIUPDATE_COMMENT_OPEN);
			Comment closeComment = doc.createComment(StringConstants.OMNIUPDATE_COMMENT_CLOSE);
			Element parameter = doc.createElement("parameter");
			parameter.setAttribute("name", "columns");
			parameter.setAttribute("type", "select");
			parameter.setAttribute("group","Everyone");
			parameter.setAttribute("prompt", "Number of Columns");
			parameter.setAttribute("alt", "How many columns do you want your page to have?");
			Element title = doc.createElement("title");
			Element option = doc.createElement("option");
			doc.appendChild(document);
			document.appendChild(headcode);
			doc.insertBefore(pi, document);
			document.appendChild(bodycode);
			document.appendChild(footcode);
			document.appendChild(config);
			config.appendChild(openComment);
			config.appendChild(title);
			config.appendChild(closeComment);
			config.appendChild(parameter);
			parameter.appendChild(option);
			document.appendChild(metadata);
			document.appendChild(mainContent);
			mainContent.appendChild(columnOne);

			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "http://commons.omniupdate.com/dtd/standard.dtd");
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File("test.xml"));

			transformer.transform(source, result);

		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		}
	}*/

	/**
	 * Get DOM as a string
	 * @param doc
	 * @return
	 */
	public static String getStringFromDoc(org.w3c.dom.Document doc)    {
        try
        {
           DOMSource domSource = new DOMSource(doc);
           StringWriter writer = new StringWriter();
           StreamResult result = new StreamResult(writer);
           TransformerFactory tf = TransformerFactory.newInstance();
           Transformer transformer = tf.newTransformer();
           transformer.setOutputProperty(OutputKeys.INDENT, "yes");
           transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "http://commons.omniupdate.com/dtd/standard.dtd");
           transformer.transform(domSource, result);
           writer.flush();
           return writer.toString();
        }
        catch(TransformerException ex)
        {
           ex.printStackTrace();
           return null;
        }
    }
	
	/**
	 * Clean up HTML so it doesn't break XML
	 * @param in
	 * @return
	 */
	static String cleanup(String in) {
		String out = in.replaceAll("<!\\[CDATA\\[", "");
		out = out.replaceAll("\\]\\]>", "");
		out = out.replaceAll("&(?!amp;)", "&amp;"); 
		out = out.replaceAll("<br>", "<br />"); 
		out = out.replaceAll("<p>\\s*?</li>", "</p>\n</li>"); 
		return out;
	}

	/**
	 * Write the page
	 * @param content
	 */
	static void outputHtml(String handle, String content) {
		content = cleanup(content);
		try {
			String outputDir = Migrate.outputDirectory + handle;
			new File(outputDir).mkdirs();
			String outputFile = outputDir + "/index.pcf";
			PrintWriter writer = new PrintWriter(outputFile, "UTF-8");
			writer.println(content);
			writer.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e){
			e.printStackTrace();
		}
	}
	
	/**
	 * Get the basic XML content for the pcf file
	 * <!DOCTYPE document SYSTEM "http://commons.omniupdate.com/dtd/standard.dtd">
	 * @return
	 */
	static Document getXmlOutline() {
		Document doc = null;
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			
			// root elements
			doc = docBuilder.newDocument();
			doc.setXmlStandalone(true);
			//DOMImplementation domImpl = doc.getImplementation();

			//DocumentType doctype = domImpl.createDocumentType("document",
			//		 "SYSTEM", "http://commons.omniupdate.com/dtd/standard.dtd");
			
			Element document = doc.createElement("document");
			ProcessingInstruction pi = doc.createProcessingInstruction("pcf-stylesheet", 
					StringConstants.STYLESHEET_DECLARATION);
			Element headcode = doc.createElement("headcode");
			Element bodycode = doc.createElement("bodycode");
			Element footcode = doc.createElement("footcode");
			Element config = doc.createElement("config");
			Element mainContent = doc.createElement("maincontent");
			Element metadata = doc.createElement("metadata");
			// Element columnOne = doc.createElement("column_one");
			Comment openComment = doc.createComment(StringConstants.OMNIUPDATE_COMMENT_OPEN);
			Comment closeComment = doc.createComment(StringConstants.OMNIUPDATE_COMMENT_CLOSE);
			// Element parameter = doc.createElement("parameter");
			// parameter.setAttribute("type", "select");
			// parameter.setAttribute("group","Everyone");
			Element title = doc.createElement("title");
			// Element option = doc.createElement("option");
			doc.appendChild(document);
			document.appendChild(headcode);
			doc.insertBefore(pi, document);
			document.appendChild(bodycode);
			document.appendChild(footcode);
			document.appendChild(config);
			config.appendChild(openComment);
			config.appendChild(title);
			config.appendChild(closeComment);
			// config.appendChild(parameter);
			// parameter.appendChild(option);
			document.appendChild(metadata);
			document.appendChild(mainContent);

		} catch (ParserConfigurationException pce){
			pce.printStackTrace();
		}
		return doc;
	}
}