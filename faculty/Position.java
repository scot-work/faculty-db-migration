package faculty;

class Position {
String positionDescription;
String department;

 Position(String desc){
	this.positionDescription = desc;
}
/**
 * Return as formatted HTML
 * @return
 */
 String toHTML(){
	 return "<em>" + positionDescription + ", " + department + "</em>";
 }

}
