package faculty;

class Doc {
	String label;
	String url;
	String localPath;
	String name;

	/**
	 * Create a new Document object
	 * @param label Name to use in link
	 * @param url URL
	 */
	Doc(String label, String url){
		this.label = label;
		this.url = url;
	}

	String extension() {
		return " [" + url.substring(url.lastIndexOf('.') + 1).toUpperCase() + "]";
	}

	/**
	 * Return as a formatted list item/link
	 * @return HTML list item
	 */
	String toHTML() {
		return "\n<li><a href=\"" + legalURL() + "\">" + label + this.extension() + "</a></li>";
	}

	/**
	* Replace or remove illegal characters 
	*/
	String legalName(){
		String result;

		// Need to replace : and &

		// replace spaces with hyphen
		result = this.name.replaceAll(" ", "-");
		// remove single apostrophes
		result = this.name.replaceAll("'", "");
		// remove colons
		result = this.name.replaceAll(":", "");
		// remove commas
		result = result.replaceAll(",", "");
		// remove ampersands
		result = this.name.replaceAll("&", "and");
		// remove parentheses
		result = result.replaceAll("\\(", "");
		result = result.replaceAll("\\)", "");
		// remove square brackets
		result = result.replaceAll("\\]", "");
		result = result.replaceAll("\\[", "");
		// remove backslashes
		result = result.replaceAll("\\\\", "");
		// remove at sign
		result = result.replaceAll("@", "");
		// remove plus sign
		result = result.replaceAll("\\+", "");
		return result;
	}

	/**
	* Replace or remove illegal characters 
	*/
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
		result = result.replaceAll("\\\\", "");
		result = result.replaceAll("@", "");
		return result;
	}
}