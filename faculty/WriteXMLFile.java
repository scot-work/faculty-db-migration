package faculty;

import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
 
import org.w3c.dom.Attr;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.ProcessingInstruction;
 
public class WriteXMLFile {
 
	public static void main(String argv[]) {
 
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
		Comment openComment = doc.createComment("com.omniupdate.properties");
		Comment closeComment = doc.createComment("/com.omniupdate.properties");
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
	}
}