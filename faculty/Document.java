package faculty;

class Document {
	String label;
	String url;
	
// example: /people/scot.close/courses/test/s0/52-Fall-09-1 Greensheet.pdf

	public Document(String label, String url){
		this.label = label;
		this.url = url;
	}

	public String toHTML() {
		return "<li><a href=\"" + url + "\">" + label + "</a></li>";
	}

}