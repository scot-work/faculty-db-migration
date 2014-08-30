package faculty;

public class Publication {
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

	public Publication() {

	}

/*
* <li>Author(s). &quot;<a href="URL">Title</a>
		,&quot; Publication Name.
		Publication Type.
		Vol. volume. 
		Issue issue. 
		Publisher Location: Publisher Name, (December 1951). pp.page.

	  <p><span class="text_blue">Abstract: </span>abstract<p>
	</li>	
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