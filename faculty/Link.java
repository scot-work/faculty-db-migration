package faculty;

class Link {
	String label;
	String url;

	public Link(String label, String url){
		this.label = label;
		this.url = url;
	}

	public String toHTML(){
		return "<a href=\"" + url + "\">" + label + "</a>";
	}
}