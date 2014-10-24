package faculty;

class License {

	int year;
	String issuedBy;
	String descr;
	String country;
	String countryCode;
	String state;
	String stateCode;
	String licenseName;
	String stateName;

	/**
	 * Constructor
	 */
	public License() {

	}

	public String toHTML() {
		return "<li>" + this.toString() + "</li>";
	}

	/**
	 * Return as a formatted string
	 */
	public String toString() {
		String result = "";
		if (Migrate.isValid(descr)) {
			result += descr;
		} else if (Migrate.isValid(licenseName)) {
			result += licenseName;
		}
		if (state != null) {
			result += ", " + state;
		}	
		if (country != null) {
			result += ", " + country;
		}	
		if (year > 0) {
			result += ", " + Integer.toString(year);
		}
		return result;
	}
}