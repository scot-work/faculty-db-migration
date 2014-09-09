package faculty;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class Expert {
	String handle;
	String categoryOne;
	String categoryTwo;
	boolean contactFoSpeaking;
	boolean contactForResearch;
	boolean contactByMedia;
	String summary;

	Expert(String handle) {
		this.handle = handle;
	}

	void createXml(){
		Document doc = null;
		try {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			
			// root elements
			 doc = docBuilder.newDocument();
			doc.setXmlStandalone(true);
		} catch (ParserConfigurationException pce){
			pce.printStackTrace();
		}
	}

}