package faculty;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class Publication {
	Faculty faculty;
	String title;
	String name;
	String publisher;
	int year;
	int month ;
	String  volume;
	String  issue;
	String  page;
	String  abstr;
	String  url;
	String  authors;
	String  publisherLocation;
	int position;
	String  publicationType ;
	String[] months = {"January","February","March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};

	/**
	 * Constructor
	 */
	public Publication() {

	}

	/**
	 * Output to pcf
	 * TODO move this to XmlHelper
	 */
	void toXml(){
		if ((faculty.useFormEntryForPublications && (faculty.publications.size() > 0)) 
				|| Migrate.isValid(faculty.publicationsText)) {
			try {
				String outputDir = Migrate.outputDirectory + faculty.handle + "/publications/";
				String outputFile = outputDir + "/index.html";
				new File(outputDir).mkdirs();
				PrintWriter writer = new PrintWriter(outputFile, "UTF-8");
				writer.println(HtmlStrings.HEADER);
				writer.println(HtmlStrings.TITLE);
				writer.println(HtmlStrings.BODY);
				writer.println("<h2>" + faculty.fullName() + "</h2>"); 
				writer.println("<h3>Publications &amp; Presentations</h3>");
				writer.println("<ul>");
				if (faculty.useFormEntryForPublications && (faculty.publications.size() > 0)) {
					for (Publication p : faculty.publications) {
						writer.println(p.toHTML());
					}
				} else {
					writer.println("<li>" + faculty.publicationsText + "</li>");
				}
				writer.println("</ul>");
				writer.println(HtmlStrings.FOOTER);
				writer.close();
			} catch (FileNotFoundException | UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Return as formatted HTML
	 * @return
	 */
	public String toHTML(){
		String result = "<li>";
		if (Migrate.isValid(authors)) {
			result += authors + ". ";
		}
		if (Migrate.isValid(title)) {
			result += "&quot;" + title + ",&quot; ";
		}
		if (Migrate.isValid(publisher)) {
			result += publisher + ". ";
		}
		if (Migrate.isValid(publicationType)) {
			result += publicationType + ". ";
		}
		if (Migrate.isValid(volume)) {
			result += "Vol. " + volume + ". ";
		}
		if (Migrate.isValid(issue)) {
			result += "Issue " + issue + ". ";
		}
		if (Migrate.isValid(publisherLocation)) {
			result += publisherLocation + ": ";
		}
		if (Migrate.isValid(publisher)) {
			result += publisher + ", " ;
		}
		result += "(";
		if (month > 0) {
			result += months[month - 1] + " ";
		}
		result += year + "). ";
		if (Migrate.isValid(page)) {
			result += "pp." + page + "."; 
		}
		if (Migrate.isValid(abstr)) {
			result += "<p>Abstract: " + abstr + "</p>";
		}
		result += "</li>";
		return result;
	}

}