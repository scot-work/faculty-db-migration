package faculty;

class Degree {
  // final values
 String degree;
 String major;
 String school;
 String country;
 String state;
 String year;

 // values from DB
 String degreeCode;
 String degreeName;
 String majorCode;
 String majorName;
 String schoolCode;
 String schoolName;
 String stateCode;
 String stateName;
 String countryCode;

 public Degree() {
  // Create empty degree if no name available
 }

 public Degree(String degree) {
  this.degree = degree;
}

public String toString() {
  // Associate of Arts, Computer Science Cabrillo College, United States, 1999
  String result = "\n" + this.degree;
  if (Migrate.isValid(major)) result += ", " + this.major;
  if (Migrate.isValid(school)) result += ", " + this.school;
  if (Migrate.isValid(state)) result += ", " + this.state;
  if (Migrate.isValid(country)) result += ", " + this.country;
  if (Migrate.isValid(year)) result += ", " + this.year;
  return result;
}


}
