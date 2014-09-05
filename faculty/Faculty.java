package faculty;

import faculty.Education;
import faculty.Degree;

import java.util.*;
import java.io.*;

import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Text;
import org.w3c.dom.Element;

class Faculty {
	String lastName;
	String firstName;
	String handle;
	int facultyID;
	int towerID;
	List<String> emails;
	Education education;
	String bio;
	String officeHours;
	String phone;
	String additionalInfo;
	String photoDescription;
	String middleName;
	int photoSetting;
	String titles;
	List<License> licenses;
	List<Position> positions;
	List<Course> courses;
	List<Link> links;
	List<CustomPage> customPages;
	boolean useFormEntryForPublications;
	List<Publication> publications;
	List<Research> research;
	String publicationsText;

	// module settings

	boolean active; // inactive faculty will not be in sjsu_people_details_master.towerid
	boolean bioActive;
	boolean coursesActive;
	boolean educationActive;
	boolean licensesCertificatesActive;
	boolean linksActive;
	boolean professionalServicesActive;
	boolean publicationsActive;
	boolean researchActive;
	boolean expertActive;

	/**
	 * 
	 * @param facultyID internal id number
	 */
	Faculty(int facultyID) {
		this.facultyID = facultyID;
	}

	/**
	 * Full name
	 * @return
	 */
	String fullName(){
		String result = firstName;
		if (Migrate.isValid(middleName)){
			result += " " + middleName;
		}
		result += " " + lastName;
		return result;
	}

	/** 
	 * Output as XML (pcf)
	 */
	void outputXml(){
		// Get empty DOM
		Document doc = XmlHelper.getXmlOutline("interior");

		// insert data into doc
		
		// Add title
		Text titleText = doc.createTextNode(fullName());
		Element titleNode = (Element) (doc.getElementsByTagName("title")).item(0);
		titleNode.appendChild(titleText);
		// Editable areas
		Element bodyNode = (Element) (doc.getElementsByTagName("maincontent")).item(0);
		Element columnTwo = doc.createElement("column_two");
		bodyNode.appendChild(columnTwo);
		columnTwo.appendChild(doc.createComment(StringConstants.OMNIUPDATE_DIV_OPEN));
		columnTwo.appendChild(doc.createComment(StringConstants.OMNIUPDATE_EDITOR));
		
		// Add content
		CDATASection bodyText = doc.createCDATASection(getContentAsHtml());
		columnTwo.appendChild(bodyText);
		columnTwo.appendChild(doc.createComment(StringConstants.OMNIUPDATE_DIV_CLOSE));
		String xml = XmlHelper.getStringFromDoc(doc);
		
		// Remove CDATA tag before writing file
		xml = xml.replaceAll("<!\\[CDATA\\[", "");
		xml = xml.replaceAll("\\]\\]>", "");
		XmlHelper.outputPcf(this.handle, xml);
		
		// Output course pages
		for (Course c : courses) {
			c.writePcf();
		}
		// Output publications page
		String publicationContent = "";
		if ((useFormEntryForPublications && (publications.size() > 0)) 
				|| Migrate.isValid(publicationsText)) {
				publicationContent = ("<h2>" + fullName() + "</h2>"); 
				publicationContent += ("<h3>Publications &amp; Presentations</h3>");
				publicationContent += ("<ul>");
				if (useFormEntryForPublications && (publications.size() > 0)) {
					for (Publication p : publications) {
						publicationContent += (p.toHTML());
					}
				} else {
					publicationContent += ("<li>" + publicationsText + "</li>");
				}
				publicationContent += ("</ul>");
		}
		XmlHelper.toXml(this, publicationContent, this.handle + "/publications/");
		
		// Output research page
		String researchContent = "";
		
		researchContent += (HtmlStrings.HEADER);
		researchContent += (HtmlStrings.TITLE);
		researchContent += (HtmlStrings.BODY);
		researchContent += ("<h2>" + fullName() + "</h2>"); 
		researchContent += ("\n<h3>Research &amp; Scholarly Activity</h3>");
		researchContent += ("\n<ul>");
		for (Research r : research) {
			researchContent += (r.getContentAsHtml());
		}
		researchContent += ("</ul>");
		XmlHelper.toXml(this, researchContent, this.handle + "/research/");
		
		// Output custom pages
		String customContent = "";
		for (CustomPage p : customPages){
			customContent = p.getContentAsHtml();
			XmlHelper.toXml(this, customContent, this.handle + "/" + p.name);
		}
		
		
		// get photo
		
	}

	/**
	 * Convert to HTML string
	 * @return
	 */
	String getContentAsHtml(){
		String content = "";	

		content += ("<h2>" + fullName() + "</h2>"); 
		if (photoSetting == 2){
			String photoURL = Migrate.baseURL + handle + "/" + handle + ".jpg";
			content += ("<img src=\"" + photoURL + "\" alt=\"" + photoDescription + "\" />");
		}
		content += ("<p>");
		for(Position p : positions){
			content += (p.toHTML() + "<br />");
		}
		content += ("<em>" + titles + "</em><br />");
		content += ("</p>");
		content += ("<h4>Email</h4>");
		for(String email : emails){
			content += ("<p><a href=\"mailto:" + email + "\">" + email + "</a></p>");
		}
		content += ("<h4>Phone Number(s)</h4>");
		content += ("<p>" + phone + "</p>");
		content += ("<h4>Office Hours</h4>");
		content += ("<p>" + officeHours + "</p>");

		// Courses
		content += (coursesActive?"":"<div style=\"background-color: yellow;\">");
		content += ("<hr /><h3>Courses</h3>");
		content += ("<ul>");
		for(Course c : courses) {
			if(c.active){
				content += ("<li><a href=\"" + c.url() + "\">" + c.title + "</a></li>");
			}
		}
		content += ("</ul>");
		content += (coursesActive?"":"</div>");

		// Education
		content += (educationActive?"":"<div style=\"background-color: yellow;\">");
		content += ("<hr /><h3>Education</h3>");
		content += ("<ul>");  
		for (Degree d : education.degrees) {
			content += ("<li>" + d + "</li>");
		}
		content += ("</ul>");
		content += (educationActive?"":"</div>");

		// Licenses and Certificates
		content += (licensesCertificatesActive?"":"<div style=\"background-color: yellow;\">");
		content += ("<hr /><h3>Licenses &amp; Certificates</h3>");
		content += ("<ul>");
		for (License l : licenses){
			content += ("<li>" + l + "</li>");
		}
		content += ("</ul>");
		content += (licensesCertificatesActive?"":"</div>");

		// Bio
		content += (bioActive?"":"<div style=\"background-color: yellow;\">");
		content += ("<hr /><h3>Bio</h3>");
		content += (bio);
		content += (bioActive?"":"</div>");
		content += ("<hr /><h3>Links</h3>");
		content += ("<ul>");
		for (Link l : links ){
			content += ("<li><a href=\"" + l.url + "\">" + l.label + "</a></li>");
		}
		content += ("</ul>");
		return content;
	}

	/**
	 * Write as an HTML page
	 */
/*	void outputHtml() {
		try {
			String outputDir = Migrate.outputDirectory + this.handle;
			new File(outputDir).mkdirs();
			String outputFile = outputDir + "/index.html";
			PrintWriter writer = new PrintWriter(outputFile, "UTF-8");
			writer.println(HtmlStrings.HEADER);
			writer.println(HtmlStrings.TITLE);
			writer.println(HtmlStrings.BODY);

			writer.println("<h2>" + fullName() + "</h2>"); 
			if (photoSetting == 2){
				String photoURL = Migrate.baseURL + handle + "/" + handle + ".jpg";
				writer.println("<img src=\"" + photoURL + "\" alt=\"" + photoDescription + "\" />");
			}
			writer.println("<p>");
			for(Position p : positions){
				writer.println(p.toHTML() + "<br />");
			}
			writer.println("<em>" + titles + "</em><br />");
			writer.println("</p>");
			writer.println("<h4>Email</h4>");
			for(String email : emails){
				writer.println("<p><a href=\"mailto:" + email + "\">" + email + "</a></p>");
			}
			writer.println("<h4>Phone Number(s)</h4>");
			writer.println("<p>" + phone + "</p>");
			writer.println("<h4>Office Hours</h4>");
			writer.println("<p>" + officeHours + "</p>");
			writer.println("</p>");

			// Courses
			writer.println(coursesActive?"":"<div style=\"background-color: yellow;\">");
			writer.println("<hr /><h3>Courses</h3>");
			writer.println("<ul>");
			for(Course c : courses) {
				if(c.active){
					writer.print("<li><a href=\"" + c.url() + "\">" + c.title + "</a></li>");
				}
			}
			writer.println("</ul>");
			writer.println(coursesActive?"":"</div>");

			// Education
			writer.println(educationActive?"":"<div style=\"background-color: yellow;\">");
			writer.println("<hr /><h3>Education</h3>");
			writer.println("<ul>");  
			for (Degree d : education.degrees) {
				writer.print("<li>" + d + "</li>");
			}
			writer.println("</ul>");
			writer.println(educationActive?"":"</div>");

			// Licenses and Certificates
			writer.println(licensesCertificatesActive?"":"<div style=\"background-color: yellow;\">");
			writer.println("<hr /><h3>Licenses &amp; Certificates</h3>");
			writer.println("<ul>");
			for (License l : licenses){
				writer.print("<li>" + l + "</li>");
			}
			writer.println("</ul>");
			writer.println(licensesCertificatesActive?"":"</div>");

			// Bio
			writer.println(bioActive?"":"<div style=\"background-color: yellow;\">");
			writer.println("<hr /><h3>Bio</h3>");
			writer.println(bio);
			writer.println(bioActive?"":"</div>");
			writer.println("<hr /><h3>Links</h3>");
			writer.println("<ul>");
			for (Link l : links ){
				writer.println("<li><a href=\"" + l.url + "\">" + l.label + "</a></li>");
			}
			writer.println("</ul>");
			writer.println(HtmlStrings.FOOTER);
			writer.close();

		} catch (FileNotFoundException | UnsupportedEncodingException e){
			e.printStackTrace();
		}

		// Output course pages
		for (Course c : courses) {
			try {
				String outputDir = Migrate.outputDirectory + this.handle + "/courses/" + c.name;
				String outputFile = outputDir + "/index.html";
				new File(outputDir).mkdirs();
				PrintWriter writer = new PrintWriter(outputFile, "UTF-8");
				c.toHTML(writer);
				writer.close();
			} catch (FileNotFoundException | UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}

		// Output publications
		if ((useFormEntryForPublications && (publications.size() > 0)) 
				|| Migrate.isValid(publicationsText)) {
			try {
				String outputDir = Migrate.outputDirectory + this.handle + "/publications/";
				String outputFile = outputDir + "/index.html";
				new File(outputDir).mkdirs();
				PrintWriter writer = new PrintWriter(outputFile, "UTF-8");
				writer.println(HtmlStrings.HEADER);
				writer.println(HtmlStrings.TITLE);
				writer.println(HtmlStrings.BODY);
				writer.println("<h2>" + fullName() + "</h2>"); 
				writer.println("<h3>Publications &amp; Presentations</h3>");
				writer.println("<ul>");
				if (useFormEntryForPublications && (publications.size() > 0)) {
					for (Publication p : publications) {
						writer.println(p.toHTML());
					}
				} else {
					writer.println("<li>" + publicationsText + "</li>");
				}
				writer.println("</ul>");
				writer.println(HtmlStrings.FOOTER);
				writer.close();
			} catch (FileNotFoundException | UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}

		// Output research
		try {
			String outputDir = Migrate.outputDirectory + this.handle + "/research/";
			String outputFile = outputDir + "/index.html";
			new File(outputDir).mkdirs();
			PrintWriter writer = new PrintWriter(outputFile, "UTF-8");
			writer.println(HtmlStrings.HEADER);
			writer.println(HtmlStrings.TITLE);
			writer.println(HtmlStrings.BODY);
			writer.println("<h2>" + fullName() + "</h2>"); 
			writer.println("<h3>Research &amp; Scholarly Activity</h3>");
			writer.println("<ul>");
			for (Research r : research) {
				// writer.println(r.toHTML());
			}
			writer.println("</ul>");
			writer.println(HtmlStrings.FOOTER);
			writer.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}


		// Custom pages
		for (CustomPage p : customPages){
			System.out.println(p.title);
			try {
				String outputDir = Migrate.outputDirectory + this.handle + "/" + p.name;
				String outputFile = outputDir + "/index.html";
				System.out.println(outputDir);
				new File(outputDir).mkdirs();
				PrintWriter writer = new PrintWriter(outputFile, "UTF-8");
				p.toHTML(writer);
				writer.close();
			} catch (FileNotFoundException | UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}

		// get photo
		if (photoSetting == 2){
			try {
				Migrate.saveImage(Migrate.liveSiteBaseDir + handle + "/" + handle + ".jpg", 
						Migrate.outputDirectory + handle + "/" + handle + ".jpg");
			} catch (IOException e){
				e.printStackTrace();
			}
		}
	}*/
}
