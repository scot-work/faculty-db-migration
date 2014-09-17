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

import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;



import org.w3c.dom.CDATASection;
//import org.w3c.dom.Attr;
import org.w3c.dom.Comment;
//import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
//import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

public class XmlHelper {

	static Comment openDivComment;
	static Comment closeDivComment;
	static Comment editorComment;
	static Comment openPropertiesComment;
	static Comment closePropertiesComment;
	/**
	 * Get DOM as a string
	 * @param doc
	 * @return
	 */
	public static String getStringFromDoc(org.w3c.dom.Document doc)    {
		if (doc == null){
			System.out.println("XML document is null");
		}
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
        	System.out.println("Transformer Exception");
           // ex.printStackTrace();
           return "Error in transformation";
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
	 * Compose a page
	 */
	
	static void toXml(Faculty faculty, String content, String path){
		Document doc = XmlHelper.getBasicOutline();
		// Get empty DOM
		// insert data into doc

		// Add title
		Text titleText = doc.createTextNode(faculty.fullName());
		Element titleNode = (Element) (doc.getElementsByTagName("title")).item(0);
		titleNode.appendChild(titleText);
		// Editable areas
		Element bodyNode = (Element) (doc.getElementsByTagName("maincontent")).item(0);
		Element columnTwo = doc.createElement("column_two");
		bodyNode.appendChild(columnTwo);
		columnTwo.appendChild(doc.createComment(StringConstants.OMNIUPDATE_DIV_OPEN));
		columnTwo.appendChild(doc.createComment(StringConstants.OMNIUPDATE_EDITOR));

		// Add content
		CDATASection bodyText = doc.createCDATASection(content);
		columnTwo.appendChild(bodyText);
		columnTwo.appendChild(doc.createComment(StringConstants.OMNIUPDATE_DIV_CLOSE));
		String xml = XmlHelper.getStringFromDoc(doc);

		// Remove CDATA tag before writing file
		xml = xml.replaceAll("<!\\[CDATA\\[", "");
		xml = xml.replaceAll("\\]\\]>", "");
		// XmlHelper.outputPcf(faculty.handle + "/courses/" + this.name, xml);
		outputPcf(path, xml);
	}

	/**
	 * Write an index.pcf page
	 * @param content
	 */
	static void outputPcf(String directory, String content) {
		content = cleanup(content);
		try {
			String outputDir = Migrate.outputDirectory + directory;
			new File(outputDir).mkdirs();
			String outputFile = outputDir + "/index.pcf";
			PrintWriter writer = new PrintWriter(outputFile, "utf-8");
			writer.println(content);
			writer.close();
		} catch (FileNotFoundException fnfe){
			fnfe.printStackTrace();
		} catch (UnsupportedEncodingException uee) {
		    uee.printStackTrace();
		}
	}
	
	/**
	 * Write a sidenav.inc page
	 * @param content
	 */
	static void outputSidenav(String directory, String content) {
		// content = cleanup(content);
		try {
			String outputDir = Migrate.outputDirectory + directory;
			new File(outputDir).mkdirs();
			String outputFile = outputDir + "/sidenav.inc";
			PrintWriter writer = new PrintWriter(outputFile, "UTF-8");
			writer.println(content);
			writer.close();
		} catch (FileNotFoundException fnfe){
			fnfe.printStackTrace();
		} catch (UnsupportedEncodingException uee) {
		    uee.printStackTrace();
		}
	}

	/**
	 * Create minimum viable XML content for the basic faculty file
	 * <!DOCTYPE document SYSTEM "http://commons.omniupdate.com/dtd/standard.dtd">
	 * @return
	 */
	static Document getBasicOutline() {
		Document doc = null;
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			docFactory.setNamespaceAware(true);
			
			// root elements
			doc = docBuilder.newDocument();
			doc.setXmlStandalone(true);
			// Set stylesheet type
			ProcessingInstruction pi = doc.createProcessingInstruction("pcf-stylesheet", 
					StringConstants.STYLESHEET_DECLARATION);
			
			// create root element
			
			Element document = doc.createElement("document");
                        doc.appendChild(document);
			document.setAttribute("xmlns:ouc", StringConstants.NAMESPACE);
			doc.insertBefore(pi, document);
			 
			// create other elements
			Element headcode = doc.createElement("headcode");
                        document.appendChild(headcode);
			Element bodycode = doc.createElement("bodycode");
                        document.appendChild(bodycode);
			Element footcode = doc.createElement("footcode");
                        document.appendChild(footcode);
                        
                       
			Element metaProperties = doc.createElementNS(StringConstants.NAMESPACE,"ouc:properties");
			metaProperties.setAttribute("label", "metadata");
			document.appendChild(metaProperties);
			Element title = doc.createElement("title");
                        metaProperties.appendChild(title);
                        
                        Element metaDescription = doc.createElement("meta");
                        metaDescription.setAttribute("content", "");
                        metaDescription.setAttribute("name", "Description");
                        metaProperties.appendChild(metaDescription);
                        
                        Element metaKeywords = doc.createElement("meta");
                        metaKeywords.setAttribute("content", "");
                        metaKeywords.setAttribute("name", "Keywords");
                        metaProperties.appendChild(metaKeywords);
                        
                        Element metaAuthor = doc.createElement("meta");
                        metaAuthor.setAttribute("content", "");
                        metaAuthor.setAttribute("name", "Author");
                        metaProperties.appendChild(metaAuthor);
                        
                        Element pagetypeParameter = doc.createElement("parameter");
                        pagetypeParameter.setAttribute("name", "pagetype");
                        pagetypeParameter.appendChild(doc.createTextNode("facultybasic"));
                        document.appendChild(pagetypeParameter);
                        
                        Element layoutParameter = doc.createElement("parameter");
                        layoutParameter.setAttribute("name", "layout");
                        Element columnOption = doc.createElement("option");
                        columnOption.setAttribute("value", "1col");
                        columnOption.setAttribute("selected", "true");
                        columnOption.appendChild(doc.createTextNode("One Column"));
                        layoutParameter.appendChild(columnOption);
                        document.appendChild(layoutParameter);
                        
                        Element configProperties = doc.createElementNS(StringConstants.NAMESPACE,"ouc:properties");
                        configProperties.setAttribute("label", "config");
                        
                        Element photoParameter = doc.createElement("parameter");
                        photoParameter.setAttribute("name", "photo");
                        photoParameter.setAttribute("group", "Everyone");
                        photoParameter.setAttribute("type", "filechooser");
                        photoParameter.setAttribute("prompt", "Default Photo");
                        photoParameter.setAttribute("alt", "Choose a default image to show for the page.");
                        photoParameter.appendChild(doc.createTextNode("http://www.sjsu.edu/_resources/img/sjsu-spartan.jpg"));
                        configProperties.appendChild(photoParameter);
                        
                        Element hideParameter = doc.createElement("parameter");
                        hideParameter.setAttribute("name", "hide");
                        hideParameter.setAttribute("group", "Everyone");
                        hideParameter.setAttribute("type", "radio");
                        hideParameter.setAttribute("prompt", "Hide Page?");
                        hideParameter.setAttribute("alt", "Prevent the page from publishing and hide it from navigation menus and index pages.");
                        Element disableOption = doc.createElement("option");
                        disableOption.setAttribute("value", "true");
                        disableOption.setAttribute("selected", "false");
                        disableOption.appendChild(doc.createTextNode("Disable the page"));
                        hideParameter.appendChild(disableOption);
                        Element publishOption = doc.createElement("option");
                        publishOption.setAttribute("value", "false");
                        publishOption.setAttribute("selected", "true");
                        publishOption.appendChild(doc.createTextNode("Publish the page"));
                        hideParameter.appendChild(publishOption);
                        configProperties.appendChild(hideParameter);
                        document.appendChild(configProperties);

                        Element navOrderParameter = doc.createElement("parameter");
                        navOrderParameter.setAttribute("name", "nav_order");
                        navOrderParameter.setAttribute("group", "Everyone");
                        navOrderParameter.setAttribute("prompt", "Nav Order");
                        navOrderParameter.setAttribute("alt", "Enter a number from 1 to 99 to control the order in which a link to this page appears in either the main navigation menu or sidebar navigation menus. Optionally, leave it blank to keep the page off navigation menus.");
                        navOrderParameter.appendChild(doc.createTextNode("02"));
                        configProperties.appendChild(navOrderParameter);
                        
                        Element maincontentDiv = doc.createElementNS(StringConstants.NAMESPACE,"ouc:div");
                        maincontentDiv.setAttribute("label", "maincontent");
                        maincontentDiv.setAttribute("group", "Everyone");
                        maincontentDiv.setAttribute("button-class", "oucEditButton");
                        maincontentDiv.setAttribute("button-text", "Edit Content");
                        maincontentDiv.setAttribute("break", "break");
                        
                        Element editor = doc.createElementNS(StringConstants.NAMESPACE,"ouc:editor");
                        editor.setAttribute("csspath", "/_resources/ou/editor/sjsy-wysiwyg.css");
                        editor.setAttribute("cssmenu", "/_resources/ou/editor/styles.txt");
                        editor.setAttribute("width", "1040");
                        editor.setAttribute("wysiwyg-class", "maincontent");
                        
                        maincontentDiv.appendChild(editor);
                        
                        document.appendChild(maincontentDiv);

		} catch (ParserConfigurationException pce){
			pce.printStackTrace();
		}
		return doc;
	}
	
	/**
	 * Create minimum viable XML content for the pcf file
	 * <!DOCTYPE document SYSTEM "http://commons.omniupdate.com/dtd/standard.dtd">
	 * @return
	 */
	static Document getXmlOutlinex(String pageType) {
		Document doc = null;
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			
			// root elements
			doc = docBuilder.newDocument();
			doc.setXmlStandalone(true);
			// Set stylesheet type
			ProcessingInstruction pi = doc.createProcessingInstruction("pcf-stylesheet", 
					StringConstants.STYLESHEET_DECLARATION);
			
			// create root element
			Element document = doc.createElement("document");
			
			// create other elements
			Element headcode = doc.createElement("headcode");
			Element bodycode = doc.createElement("bodycode");
			Element footcode = doc.createElement("footcode");
			Element config = doc.createElement("config");
			Element mainContent = doc.createElement("maincontent");
			Element metadata = doc.createElement("metadata");
			Element metaDescription = doc.createElement("meta");
			Element metaKeywords = doc.createElement("meta");
			Element metaAuthor = doc.createElement("meta");
			Element optionOneCol = doc.createElement("option");
			Element optionTwoCol = doc.createElement("option");
			Element optionThreeCol = doc.createElement("option");
			Element title = doc.createElement("title");
			Element pageTypeParameter = doc.createElement("parameter");
			Element columnsParameter = doc.createElement("parameter");
			
			// Static elements available to other classes as needed
			openDivComment = doc.createComment(StringConstants.OMNIUPDATE_DIV_OPEN);
			closeDivComment = doc.createComment(StringConstants.OMNIUPDATE_DIV_CLOSE);
			editorComment = doc.createComment(StringConstants.OMNIUPDATE_EDITOR);
			
			// Add page template name
			pageTypeParameter.setAttribute("name", "pagetype");
			Text pageTemplateName = doc.createTextNode(pageType);
			pageTypeParameter.appendChild(pageTemplateName);
			
			// columns
			columnsParameter.setAttribute("name", "columns");
			columnsParameter.setAttribute("type", "select");
			columnsParameter.setAttribute("group", "Everyone");
			columnsParameter.setAttribute("prompt", "Number of Columns");
			columnsParameter.setAttribute("alt", "How many columns do you want your page to have?");
			columnsParameter.appendChild(optionOneCol);
			optionOneCol.setAttribute("value", "1col");
			optionOneCol.setAttribute("selected", "false");
			optionOneCol.appendChild(doc.createTextNode("One Column"));
			columnsParameter.appendChild(optionTwoCol);
			optionTwoCol.setAttribute("value", "2col");
			optionTwoCol.setAttribute("selected", "true");
			optionTwoCol.appendChild(doc.createTextNode("Two Columns"));
			columnsParameter.appendChild(optionThreeCol);
			optionThreeCol.setAttribute("value", "3col");
			optionThreeCol.setAttribute("selected", "false");
			optionThreeCol.appendChild(doc.createTextNode("Three Columns"));
			
			// Assemble DOM
			doc.appendChild(document);
			document.appendChild(headcode);
			doc.insertBefore(pi, document);
			document.appendChild(bodycode);
			document.appendChild(footcode);

			document.appendChild(title);

			document.appendChild(config);
			config.appendChild(pageTypeParameter);

			config.appendChild(columnsParameter);

			document.appendChild(metadata);

			metaDescription.setAttribute("content", "");
			metaDescription.setAttribute("name", "description");
			metaKeywords.setAttribute("content", "");
			metaKeywords.setAttribute("name", "keywords");
			metaAuthor.setAttribute("content", "");
			metaAuthor.setAttribute("name", "author");
			metadata.appendChild(metaDescription);
			metadata.appendChild(metaKeywords);
			metadata.appendChild(metaAuthor);

			
			document.appendChild(metadata);
			document.appendChild(mainContent);

		} catch (ParserConfigurationException pce){
			pce.printStackTrace();
		}
		return doc;
	}
}