package faculty;

class Doc {
	String label;
	String url;

	/**
	 * Create a new Document object
	 * @param label Name to use in link
	 * @param url URL
	 */
	Doc(String label, String url){
		this.label = label;
		this.url = url;
	}

	/**
	 * Return as a formatted list item/link
	 * @return HTML list item
	 */
	String toHTML() {
		return "\n<li><a href=\"" + legalURL() + "\">" + label + "</a></li>";
	}

	String legalURL() {
		String result;
		// remove apostrophes
		result = this.url.replaceAll("'", "");
		// remove commas
		result = result.replaceAll(",", "");
		// remove parentheses
		result = result.replaceAll("\\(", "");
		result = result.replaceAll("\\)", "");
		// remove square brackets
		result = result.replaceAll("\\]", "");
		result = result.replaceAll("\\[", "");
		return result;
	}

}