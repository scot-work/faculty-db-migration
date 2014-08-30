package faculty;

class Link {
	String label;
	String url;

	public Link(String label, String url){
		this.label = label;
		this.url = url;
	}

	public String toHTML(){
		return "<li><a href=\"" + url + "\">" + label + "</a></li>";
	}
}