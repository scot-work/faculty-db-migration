package faculty;

class Doc {
	String label;
	String url;

	/**
	 * Create a new Document object
	 * @param label Name to use in link
	 * @param url URL
	 */
	public Doc(String label, String url){
		this.label = label;
		this.url = url;
	}

	/**
	 * Return as a formatted list item/link
	 * @return HTML list item
	 */
	public String toHTML() {
		return "<li><a href=\"" + url + "\">" + label + "</a></li>";
	}

}