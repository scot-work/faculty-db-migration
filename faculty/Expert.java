package faculty;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.CDATASection;

public class Expert {
    String handle;
    String categoryOne;
    String categoryTwo;
    boolean contactForSpeaking;
    boolean contactForResearch;
    boolean contactByMedia;
    String summary;

    Expert(String handle) {
        this.handle = handle;
    }

    Element createXml(Document doc){
        Element faculty = null;

        //DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        //DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        faculty = doc.createElement("faculty");
        faculty.setAttribute("handle", this.handle);

        //doc.appendChild(faculty);
        Element contact = doc.createElement("contact");
        faculty.appendChild(contact);
        Element speaking = doc.createElement("contact_for_speaking");
        Element research = doc.createElement("contact_for_research");
        Element media = doc.createElement("contact_by_media");
        if (this.contactForSpeaking){
            contact.appendChild(speaking);
        }
        if (this.contactForResearch){
            contact.appendChild(research);
        }
        if (this.contactByMedia){
            contact.appendChild(media);
        }
        Element expertise = doc.createElement("expertise-categories");
        faculty.appendChild(expertise);
        if (Migrate.isValid(this.categoryOne)){
            Element catOne = doc.createElement("category");
            expertise.appendChild(catOne);
            catOne.appendChild(doc.createTextNode(categoryOne));
        }
        if (Migrate.isValid(this.categoryTwo)){
            Element catTwo = doc.createElement("category");
            expertise.appendChild(catTwo);
            catTwo.appendChild(doc.createTextNode(categoryTwo));
        }
        Element summary = doc.createElement("expertise-summary");
        if ( Migrate.isValid(this.summary)){
            CDATASection summaryText = doc.createCDATASection(this.summary);
            summary.appendChild(summaryText);
        }

        faculty.appendChild(summary);

        return faculty;
    }

}