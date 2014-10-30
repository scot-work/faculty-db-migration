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
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;


import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;


public class XmlHelper {

    protected static final String DEFAULT_PHOTO = "http://www.sjsu.edu/_resources/img/sjsu-spartan.jpg";
    protected static final String CSS_MENU = "/_resources/ou/editor/styles.txt";
    protected static final String CSS_PATH = "/_resources/editor/sjsu-wysiwyg.css";

    /**
    * Return one element based on xPath
    */
    static Element getElementByAttribute(Document doc, String xPath){
        XPathFactory xPathfactory = XPathFactory.newInstance();
        XPath xpath = xPathfactory.newXPath();
        XPathExpression expr = null;
        NodeList nl = null;
        try {
            expr = xpath.compile(xPath);
            nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
        return (Element) nl.item(0);
    }

/**
* Return a list of elements based on xPath
*/
        static NodeList getElementsByAttribute(Document doc, String xPath){
        XPathFactory xPathfactory = XPathFactory.newInstance();
        XPath xpath = xPathfactory.newXPath();
        XPathExpression expr = null;
        NodeList nl = null;
        try {
            expr = xpath.compile(xPath);
            nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
        return nl;
    }

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
     * Output a file based on the basic PCF template
     */
    static void outputBasicFile(Faculty faculty, String title, String content, String path, Boolean active) {
        Document doc = XmlHelper.getBasicOutline();

        // Add title
        Text titleText = doc.createTextNode(title);
        Element titleNode = (Element) (doc.getElementsByTagName("title")).item(0);
        titleNode.appendChild(titleText);

        // Editable area
        Element maincontentDiv = getElementByAttribute(doc, "//*[@label='maincontent']");
        maincontentDiv.appendChild(doc.createCDATASection(content));

        if (!active){
            Element hide = XmlHelper.getElementByAttribute(doc, "//*[@value='true']");
            hide.setAttribute("selected", "true");
        }

        // convert XML to a String
        String xml = XmlHelper.getStringFromDoc(doc);
        outputPcf(path, xml);
    }

    /*
    * output default file
    */
    static void outputPcf(String directory, String content) {
        outputPcf(directory, content, "index.pcf");
     }

    /**
     * Write an index.pcf page
     * @param content
     */
    static void outputPcf(String directory, String content, String title) {
        content = cleanup(content);
        if ( !Migrate.suppressFileOutput){
        try {
            // String outputDir = Migrate.outputDirectory + Migrate.baseURL + directory;
            String outputDir = Migrate.outputDirectory + directory;
            new File(outputDir).mkdirs();
            String outputFile = outputDir + "/" + title;
            PrintWriter writer = new PrintWriter(outputFile, "utf-8");
            writer.println(content);
            writer.close();
        } catch (FileNotFoundException fnfe){
            fnfe.printStackTrace();
        } catch (UnsupportedEncodingException uee) {
            uee.printStackTrace();
        }
    }
    }

    /**
     * Write a sidenav.inc page
     * @param content
     */
    static void outputSidenav(String directory, String content) {
       if (!Migrate.suppressFileOutput){
        try {
            String outputDir = Migrate.outputDirectory + directory;
            new File(outputDir).mkdirs();
            String outputFile = outputDir + "/sidenav.inc";
            PrintWriter writer = new PrintWriter(outputFile, "UTF-8");
            writer.println(StringConstants.SIDENAV_HEADER);
            writer.println(content);
            writer.close();
        } catch (FileNotFoundException fnfe){
            fnfe.printStackTrace();
        } catch (UnsupportedEncodingException uee) {
            uee.printStackTrace();
        }
    }
    }

    /**
     * Create minimum viable XML content for the basic faculty file
     * <!DOCTYPE document SYSTEM "http://commons.omniupdate.com/dtd/standard.dtd">
     * @return
     */
    static Document getProfileOutline() {
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

            // head body foot placeholders
            Element headcode = doc.createElement("headcode");
            document.appendChild(headcode);
            Element bodycode = doc.createElement("bodycode");
            document.appendChild(bodycode);
            Element footcode = doc.createElement("footcode");
            document.appendChild(footcode);

            // Title
            Element metaProperties = doc.createElementNS(StringConstants.NAMESPACE,"ouc:properties");
            metaProperties.setAttribute("label", "metadata");
            document.appendChild(metaProperties);
            Element title = doc.createElement("title");
            metaProperties.appendChild(title);

            // Description
            Element metaDescription = doc.createElement("meta");
            metaDescription.setAttribute("content", "");
            metaDescription.setAttribute("name", "Description");
            metaProperties.appendChild(metaDescription);

            // Keywords
            Element metaKeywords = doc.createElement("meta");
            metaKeywords.setAttribute("content", "");
            metaKeywords.setAttribute("name", "Keywords");
            metaProperties.appendChild(metaKeywords);

            // Author
            Element metaAuthor = doc.createElement("meta");
            metaAuthor.setAttribute("content", "");
            metaAuthor.setAttribute("name", "Author");
            metaProperties.appendChild(metaAuthor);

            // Page Type
            Element pagetypeParameter = doc.createElement("parameter");
            pagetypeParameter.setAttribute("name", "pagetype");
            pagetypeParameter.appendChild(doc.createTextNode("facultyprofile"));
            document.appendChild(pagetypeParameter);

            // Layout
            Element layoutParameter = doc.createElement("parameter");
            layoutParameter.setAttribute("name", "layout");
            Element columnOption = doc.createElement("option");
            columnOption.setAttribute("value", "1col");
            columnOption.setAttribute("selected", "true");
            columnOption.appendChild(doc.createTextNode("One Column"));
            layoutParameter.appendChild(columnOption);
            document.appendChild(layoutParameter);

            // Properties
            Element configProperties = doc.createElementNS(StringConstants.NAMESPACE,"ouc:properties");
            configProperties.setAttribute("label", "config");

            // Show/hide
            Element hideParameter = doc.createElement("parameter");
            hideParameter.setAttribute("name", "hide");
            hideParameter.setAttribute("group", "Everyone");
            hideParameter.setAttribute("type", "checkbox");
            hideParameter.setAttribute("prompt", "Hide Page?");
            hideParameter.setAttribute("alt", "Prevent the page from publishing and hide it from navigation menus and index pages.");
            Element disableOption = doc.createElement("option");
            disableOption.setAttribute("value", "true");
            disableOption.setAttribute("selected", "false");
            disableOption.appendChild(doc.createTextNode("Hide this page"));
            hideParameter.appendChild(disableOption);
            configProperties.appendChild(hideParameter);

            // Navigation Order
            Element navOrderParameter = doc.createElement("parameter");
            navOrderParameter.setAttribute("name", "nav_order");
            navOrderParameter.setAttribute("group", "Everyone");
            navOrderParameter.setAttribute("prompt", "Nav Order");
            navOrderParameter.setAttribute("alt", "Enter a number from 1 to 99 to control the order in which a link to this page appears in either the main navigation menu or sidebar navigation menus. Optionally, leave it blank to keep the page off navigation menus.");
            navOrderParameter.appendChild(doc.createTextNode("1"));
            configProperties.appendChild(navOrderParameter);

            document.appendChild(configProperties);

            // Start profile 
            Element profile = doc.createElement("profile");

            // Photo
            Element photoDiv = doc.createElementNS(StringConstants.NAMESPACE,"ouc:div");
            photoDiv.setAttribute("label", "photo");
            photoDiv.setAttribute("group", "Everyone");
            photoDiv.setAttribute("button", "hide");
            Element photoMulti = doc.createElementNS(StringConstants.NAMESPACE,"ouc:multiedit");
            photoMulti.setAttribute("type", "image");
            photoMulti.setAttribute("prompt", "Profile Photo");
            photoMulti.setAttribute("alt", "Do you want to share a photo? (Max width 138 pixels)");
            // photo.appendChild(doc.createCDATASection("<img src=\"" + DEFAULT_PHOTO + "\" alt=\"\" />"));
            photoDiv.appendChild(photoMulti);
            profile.appendChild(photoDiv);

            // Email
            Element emailDiv = doc.createElementNS(StringConstants.NAMESPACE,"ouc:div");
            emailDiv.setAttribute("label", "preferredemail");
            emailDiv.setAttribute("group", "Everyone");
            emailDiv.setAttribute("button", "hide");
            Element preferredEmailMulti = doc.createElementNS(StringConstants.NAMESPACE, "ouc:multiedit");
            preferredEmailMulti.setAttribute("type", "text");
            preferredEmailMulti.setAttribute("prompt", "Email");
            preferredEmailMulti.setAttribute("alt", "What's your preferred email address?");
            emailDiv.appendChild(preferredEmailMulti);
            profile.appendChild(emailDiv);

            // Alternate Email
            Element altEmailDiv = doc.createElementNS(StringConstants.NAMESPACE,"ouc:div");
            altEmailDiv.setAttribute("label", "alternateemail");
            altEmailDiv.setAttribute("group", "Everyone");
            altEmailDiv.setAttribute("button", "hide");
            Element altEmailMulti = doc.createElementNS(StringConstants.NAMESPACE, "ouc:multiedit");
            altEmailMulti.setAttribute("type", "text");
            altEmailMulti.setAttribute("prompt", "Alternate Email");
            altEmailMulti.setAttribute("alt", "Do you have an alternate email address?");
            altEmailDiv.appendChild(altEmailMulti);
            profile.appendChild(altEmailDiv);

            // Phone
            Element phoneDiv = doc.createElementNS(StringConstants.NAMESPACE,"ouc:div");
            phoneDiv.setAttribute("label", "preferredphone");
            phoneDiv.setAttribute("group", "Everyone");
            phoneDiv.setAttribute("button", "hide");
            Element phoneMulti = doc.createElementNS(StringConstants.NAMESPACE, "ouc:multiedit");
            phoneMulti.setAttribute("type", "text");
            phoneMulti.setAttribute("prompt", "Phone");
            phoneMulti.setAttribute("alt", "What's your preferred phone number?");
            phoneDiv.appendChild(phoneMulti);
            profile.appendChild(phoneDiv);

            // Alternate Phone
            Element altPhoneDiv = doc.createElementNS(StringConstants.NAMESPACE,"ouc:div");
            altPhoneDiv.setAttribute("label", "alternatephone");
            altPhoneDiv.setAttribute("group", "Everyone");
            altPhoneDiv.setAttribute("button", "hide");
            Element altPhoneMulti = doc.createElementNS(StringConstants.NAMESPACE, "ouc:multiedit");
            altPhoneMulti.setAttribute("type", "text");
            altPhoneMulti.setAttribute("prompt", "Alternate Phone");
            altPhoneMulti.setAttribute("alt", "Do you have an alternate phone number?");
            altPhoneDiv.appendChild(altPhoneMulti);
            profile.appendChild(altPhoneDiv);

            // Office Hours
            Element officeHoursDiv = doc.createElementNS(StringConstants.NAMESPACE,"ouc:div");
            officeHoursDiv.setAttribute("label", "officehours");
            officeHoursDiv.setAttribute("group", "Everyone");
            officeHoursDiv.setAttribute("button", "hide");
            Element officeHoursMulti = doc.createElementNS(StringConstants.NAMESPACE, "ouc:multiedit");
            officeHoursMulti.setAttribute("type", "text");
            officeHoursMulti.setAttribute("prompt", "Office Hours");
            officeHoursMulti.setAttribute("alt", "What are your office hours?");
            officeHoursDiv.appendChild(officeHoursMulti);
            profile.appendChild(officeHoursDiv);

            // Job Title(s) and Department(s)
            Element titleDiv = doc.createElementNS(StringConstants.NAMESPACE,"ouc:div");
            titleDiv.setAttribute("label", "titledepartment");
            titleDiv.setAttribute("group", "Everyone");
            titleDiv.setAttribute("button", "hide");
            Element workingTitleMulti = doc.createElementNS(StringConstants.NAMESPACE, "ouc:multiedit");
            workingTitleMulti.setAttribute("type", "textarea");
            workingTitleMulti.setAttribute("prompt", "Title(s) and Department(s)");
            workingTitleMulti.setAttribute("rows", "6"); 
            workingTitleMulti.setAttribute("editor", "yes");
            workingTitleMulti.setAttribute("alt", "What are your working titles and which departments do you work in?");
            titleDiv.appendChild(workingTitleMulti);
            profile.appendChild(titleDiv);
            
           
           /* Element additionalInfoMulti = doc.createElementNS(StringConstants.NAMESPACE, "ouc:multiedit");
            additionalInfoMulti.setAttribute("type", "text");
            additionalInfoMulti.setAttribute("prompt", "Additional Info");
            additionalInfoMulti.setAttribute("alt", "Any additional info you want to share?");
            additionalInfoDiv.appendChild(additionalInfoMulti);
            */
            // profile.appendChild(additionalInfoDiv);

            // Additional Info Label
            /*
            Element additionalInfoTitleDiv = doc.createElementNS(StringConstants.NAMESPACE,"ouc:div");
            additionalInfoTitleDiv.setAttribute("label", "additionalinfotitle");
            additionalInfoTitleDiv.setAttribute("group", "Everyone");
            additionalInfoTitleDiv.setAttribute("button", "hide");
            Element additionalInfoTitleMulti = doc.createElementNS(StringConstants.NAMESPACE, "ouc:multiedit");
            additionalInfoTitleMulti.setAttribute("type", "text");
            additionalInfoTitleMulti.setAttribute("prompt", "Custom Title");
            additionalInfoTitleMulti.setAttribute("alt", "Enter a custom title for your Additional Info (default just says 'Additional Info')");
            additionalInfoTitleDiv.appendChild(additionalInfoTitleMulti); */

             // Additional Information
            Element additionalInfoDiv = doc.createElementNS(StringConstants.NAMESPACE,"ouc:div");
            additionalInfoDiv.setAttribute("label", "addlinfo");
            additionalInfoDiv.setAttribute("group", "Everyone");
            additionalInfoDiv.setAttribute("button-class", "oucEditButton");
            additionalInfoDiv.setAttribute("button-text", "Edit Additional Info");
            additionalInfoDiv.setAttribute("break", "break");

            Element additionalInfoEditor = doc.createElementNS(StringConstants.NAMESPACE,"ouc:editor");
            additionalInfoEditor.setAttribute("cssspath", "/_resources/ou/editor/sjsu-wysiwyg.css");
            additionalInfoEditor.setAttribute("csssmenu", "/_resources/ou/editor/content.txt");
            additionalInfoEditor.setAttribute("width", "1040");
            additionalInfoEditor.setAttribute("wysiwyg-class", "addlinfo");

            additionalInfoDiv.appendChild(additionalInfoEditor);
            

            document.appendChild(profile);
            document.appendChild(additionalInfoDiv);

            // Education
            Element educationDiv = doc.createElementNS(StringConstants.NAMESPACE,"ouc:div");
            educationDiv.setAttribute("label", "eduinfo");
            educationDiv.setAttribute("group", "Everyone");
            educationDiv.setAttribute("button-class", "oucEditButton");
            educationDiv.setAttribute("button-text", "Edit Educational Info");
            educationDiv.setAttribute("break", "break");
            Element educationEditor = doc.createElementNS(StringConstants.NAMESPACE,"ouc:editor");
            educationEditor.setAttribute("csspath", CSS_PATH);
            educationEditor.setAttribute("cssmenu", CSS_MENU);
            educationEditor.setAttribute("width", "1040");
            educationDiv.appendChild(educationEditor);
            document.appendChild(educationDiv);

            // Licenses
            Element licensesDiv = doc.createElementNS(StringConstants.NAMESPACE,"ouc:div");
            licensesDiv.setAttribute("label", "licenses");
            licensesDiv.setAttribute("group", "Everyone");
            licensesDiv.setAttribute("button-class", "oucEditButton");
            licensesDiv.setAttribute("button-text", "Edit Licenses & Certificates");
            licensesDiv.setAttribute("break", "break");
            Element licensesEditor = doc.createElementNS(StringConstants.NAMESPACE,"ouc:editor");
            licensesEditor.setAttribute("csspath", CSS_PATH);
            licensesEditor.setAttribute("cssmenu", CSS_MENU);
            licensesEditor.setAttribute("width", "1040");
            licensesDiv.appendChild(licensesEditor);
            document.appendChild(licensesDiv);

            // Bio
            Element bioDiv = doc.createElementNS(StringConstants.NAMESPACE,"ouc:div");
            bioDiv.setAttribute("label", "bioinfo");
            bioDiv.setAttribute("group", "Everyone");
            bioDiv.setAttribute("button-class", "oucEditButton");
            bioDiv.setAttribute("button-text", "Edit Biographical Info");
            bioDiv.setAttribute("break", "break");
            Element bioEditor = doc.createElementNS(StringConstants.NAMESPACE,"ouc:editor");
            bioEditor.setAttribute("csspath", CSS_PATH);
            bioEditor.setAttribute("cssmenu", CSS_MENU);
            bioEditor.setAttribute("width", "1040");
            bioDiv.appendChild(bioEditor);
            document.appendChild(bioDiv);

            // Links
            Element linksDiv = doc.createElementNS(StringConstants.NAMESPACE,"ouc:div");
            linksDiv.setAttribute("label", "links");
            linksDiv.setAttribute("group", "Everyone");
            linksDiv.setAttribute("button-class", "oucEditButton");
            linksDiv.setAttribute("button-text", "Edit Additional Links");
            linksDiv.setAttribute("break", "break");
            Element linksEditor = doc.createElementNS(StringConstants.NAMESPACE,"ouc:editor");
            linksEditor.setAttribute("csspath", CSS_PATH);
            linksEditor.setAttribute("cssmenu", CSS_MENU);
            linksEditor.setAttribute("width", "1040");
            linksDiv.appendChild(linksEditor);
            document.appendChild(linksDiv);
        } catch (ParserConfigurationException pce){
            pce.printStackTrace();
        }
        return doc;
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

            // Page Type
            Element pagetypeParameter = doc.createElement("parameter");
            pagetypeParameter.setAttribute("name", "pagetype");
            pagetypeParameter.appendChild(doc.createTextNode("facultybasic"));
            document.appendChild(pagetypeParameter);

            // Layout
            Element layoutParameter = doc.createElement("parameter");
            layoutParameter.setAttribute("name", "layout");
            Element columnOption = doc.createElement("option");
            columnOption.setAttribute("value", "1col");
            columnOption.setAttribute("selected", "true");
            columnOption.appendChild(doc.createTextNode("One Column"));
            layoutParameter.appendChild(columnOption);
            document.appendChild(layoutParameter);

            // config properties
            Element configProperties = doc.createElementNS(StringConstants.NAMESPACE,"ouc:properties");
            configProperties.setAttribute("label", "config");

            // Enable/hide
            Element hideParameter = doc.createElement("parameter");
            hideParameter.setAttribute("name", "hide");
            hideParameter.setAttribute("group", "Everyone");
            hideParameter.setAttribute("type", "checkbox");
            hideParameter.setAttribute("prompt", "Hide Page?");
            hideParameter.setAttribute("alt", "Prevent the page from publishing and hide it from navigation menus and index pages.");
            Element hideOption = doc.createElement("option");
            hideOption.setAttribute("value", "true");
            hideOption.setAttribute("selected", "false");
            hideOption.appendChild(doc.createTextNode("Hide this page"));
            hideParameter.appendChild(hideOption);
            configProperties.appendChild(hideParameter);

            // Navigation Order
            Element navOrderParameter = doc.createElement("parameter");
            navOrderParameter.setAttribute("name", "nav_order");
            navOrderParameter.setAttribute("group", "Everyone");
            navOrderParameter.setAttribute("prompt", "Nav Order");
            navOrderParameter.setAttribute("alt", "Enter a number from 1 to 99 to control the order in which a link to this page appears in either the main navigation menu or sidebar navigation menus. Optionally, leave it blank to keep the page off navigation menus.");
            navOrderParameter.appendChild(doc.createTextNode("02"));
            configProperties.appendChild(navOrderParameter);

            document.appendChild(configProperties);

            // Profile
            Element profile = doc.createElement("profile");

            // Photo
            Element photoDiv = doc.createElementNS(StringConstants.NAMESPACE,"ouc:div");
            photoDiv.setAttribute("label", "photo");
            photoDiv.setAttribute("group", "Everyone");
            photoDiv.setAttribute("button", "Hide");

            Element photoMulti = doc.createElementNS(StringConstants.NAMESPACE,"ouc:multiedit");
            photoMulti.setAttribute("type","image");
            photoMulti.setAttribute("prompt","Image or Photo");
            photoMulti.setAttribute("alt","Do you want to add an image or a photo? (Max width 138 pixels)");

            photoDiv.appendChild(photoMulti);
            profile.appendChild(photoDiv);
            document.appendChild(profile);

            Element maincontentDiv = doc.createElementNS(StringConstants.NAMESPACE,"ouc:div");
            maincontentDiv.setAttribute("label", "maincontent");
            maincontentDiv.setAttribute("group", "Everyone");
            maincontentDiv.setAttribute("button-class", "oucEditButton");
            maincontentDiv.setAttribute("button-text", "Edit Content");
            maincontentDiv.setAttribute("break", "break");

            Element editor = doc.createElementNS(StringConstants.NAMESPACE,"ouc:editor");
            editor.setAttribute("csspath", CSS_PATH);
            editor.setAttribute("cssmenu", CSS_MENU);
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
     * Output faculty home page
     * @param faculty
     */
    protected static void outputProfilePage(Faculty faculty) {
        Document doc = getProfileOutline();

        NodeList hide = XmlHelper.getElementsByAttribute(doc, "//*[@name='hide']/*");
        if (faculty.isActive) {
            System.out.println("Writing " + faculty.fullName() + "\n");
            ((Element) hide.item(0)).setAttribute("selected", "false");
        } else {
            System.out.println("Inactive Faculty " + faculty.fullName() + "\n");
            ((Element) hide.item(0)).setAttribute("selected", "true");
        }

        Text titleText = doc.createTextNode(faculty.fullName());
        Element titleNode = (Element) (doc.getElementsByTagName("title")).item(0);
        titleNode.appendChild(titleText);

         // Photo
        Element photoDiv = getElementByAttribute(doc, "//*[@label='photo']");
         if (Migrate.isValid(faculty.photoUrl())){
           // photoDiv.getChildNodes().item(1).appendChild(doc.createCDATASection("<img src=\"" + faculty.photoUrl() + "\" alt=\"" + faculty.fullName() + "\" />"));
            photoDiv.appendChild(doc.createCDATASection("<img src=\"" + faculty.photoUrl() + "\" alt=\"" + faculty.fullName() + "\" />"));
         } else {
            // photoDiv.getChildNodes().item(1).appendChild(doc.createCDATASection("<img src=\"" + DEFAULT_PHOTO + "\" alt=\"\" />"));
            photoDiv.appendChild(doc.createCDATASection("<img src=\"" + DEFAULT_PHOTO + "\" alt=\"\" />"));
         }

         // Email
         if (Migrate.isValid(faculty.sjsuEmail)){
         Element emailDiv = getElementByAttribute(doc, "//*[@label='preferredemail']");
         emailDiv.appendChild(doc.createTextNode(faculty.sjsuEmail));
        }

         // Alternate Email
         if (Migrate.isValid(faculty.alternateEmail)){
         Element altEmailDiv = getElementByAttribute(doc, "//*[@label='alternateemail']");
         altEmailDiv.appendChild(doc.createTextNode(faculty.alternateEmail));
        }

         // Phone
         if (Migrate.isValid(faculty.phone())){
         Element phoneDiv = getElementByAttribute(doc, "//*[@label='preferredphone']");
         phoneDiv.appendChild(doc.createTextNode(faculty.phone()));
        }

         // Alternate Phone
         if (Migrate.isValid(faculty.alternatePhone)){
         Element altPhoneDiv = getElementByAttribute(doc, "//*[@label='alternatephone']");
         altPhoneDiv.appendChild(doc.createTextNode(faculty.alternatePhone));
        }

         // Office Hours
         if (Migrate.isValid(faculty.officeHours)){
         Element officeHoursDiv = getElementByAttribute(doc, "//*[@label='officehours']");
         officeHoursDiv.appendChild(doc.createTextNode(faculty.officeHours));
        }

         // Title(s) and Department(s)
         if (Migrate.isValid(faculty.titles)){
         Element titleDiv = getElementByAttribute(doc, "//*[@label='titledepartment']");
         titleDiv.appendChild(doc.createCDATASection(faculty.jobTitles()));
        }

         // Additional Info
         if (Migrate.isValid(faculty.additionalInfo)){
         Element additionalInfoDiv = getElementByAttribute(doc, "//*[@label='addlinfo']");
         additionalInfoDiv.appendChild(doc.createCDATASection(faculty.additionalInfo()));
        }

         // Info Title
         /*
         Element additionalInfoTitleDiv = getElementByAttribute(doc, "//*[@label='additionalinfotitle']");
         additionalInfoTitleDiv.appendChild(doc.createCDATASection("Additional Information")); */

         // Education
         if (Migrate.isValid(faculty.education.output())){
         Element educationDiv = getElementByAttribute(doc, "//*[@label='eduinfo']");
         educationDiv.getChildNodes().item(0).appendChild(doc.createCDATASection(faculty.education.output()));
        }

         // Licenses
         if (Migrate.isValid(faculty.licenses.toString())){
         Element licensesDiv = getElementByAttribute(doc, "//*[@label='licenses']");
         licensesDiv.getChildNodes().item(0).appendChild(doc.createCDATASection(faculty.licenses()));
        }

         // Bio
         if (Migrate.isValid(faculty.bio)){
         Element bioDiv = getElementByAttribute(doc, "//*[@label='bioinfo']");
         bioDiv.getChildNodes().item(0).appendChild(doc.createCDATASection(faculty.bio));
        }

         // Links
         if (Migrate.isValid(faculty.links())){
         Element linksDiv = getElementByAttribute(doc, "//*[@label='links']");
         linksDiv.getChildNodes().item(0).appendChild(doc.createCDATASection(faculty.links()));
        }

         String path = StringConstants.SITEROOT + faculty.handle();
         String xml = getStringFromDoc(doc);
         outputPcf(path , xml);

    }
}