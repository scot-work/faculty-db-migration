package faculty;

import java.util.*;

class Course {
	int id;
	String title;
	String name;
	String location;
	String dayAndTime;
	String description;
	String facultyHandle;
	List<Section> sections; 

	public Course(String title) {
		this.title = title;
	}

	public String toString() {
		String result = title + ", " + name + ", " + location + ", " + dayAndTime;
		if (description != null){
			result += "\n" + description;
		}
		if (sections != null) {
			result += "\nSections:";
			for (Section s : sections) {
				result += s;
			}
		}
		return result;
	}

	public String url() {
		return "/people/" + facultyHandle + "/courses/" + name;
	}

}
