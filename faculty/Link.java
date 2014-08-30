package faculty;

class Link {
	String label;
	String url;

	/**
	 * Create a new hyperlink
	 * @param label Link text
	 * @param url Link destination
	 */
	public Link(String label, String url){
		this.label = label;
		this.url = url;
	}

	/**
	 * Return as a list item/link
	 * @return
	 */
	public String toHTML(){
		return "<li><a href=\"" + url + "\">" + label + "</a></li>";
	}
}