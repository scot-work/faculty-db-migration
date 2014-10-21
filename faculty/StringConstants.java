package faculty;

public class StringConstants {
	String xmlDeclarationx = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
			"\n<?pcf-stylesheet path=\"/xsl/faculty-profile.xsl\" site=\"_resources\" extension=\"html\"?>" +
			"\n<!DOCTYPE document SYSTEM \"http://commons.omniupdate.com/dtd/standard.dtd\">\n\n";
	static final String OMNIUPDATE_DIV_OPEN = " com.omniupdate.div  label=\"column_two\"  "
			+ "group=\"Everyone\"  button=\"707\"  break=\"break\" ";
	static final String OMNIUPDATE_DIV_CLOSE = " /com.omniupdate.div ";
	static final String OMNIUPDATE_EDITOR = " ouc:editor csspath=\"/includes/ou/editor/1column/column_two.css\" "
			+ "cssmenu=\"/includes/ou/editor/content.txt\" width=\"955\"/ ";
	static final String STYLESHEET_DECLARATION = "path=\"/xsl/faculty-profile.xsl\" "
			+ "site=\"_resources\" extension=\"html\"";
	static final String NAMESPACE = "http://omniupdate.com/XSL/Variables";
	static final String SIDENAV_HEADER = "<!-- ouc:editor csspath=\"/includes/ou/editor/sidenav.css\" cssmenu=\"/includes/ou/editor/sidenav.txt\" width=\"180\"/ -->";
	static String PRIMARYNAV = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<?pcf-stylesheet path=\"/xsl/_shared/facultyprimarynav.xsl\" site=\"_resources\" extension=\"html\"?>\n<!DOCTYPE document SYSTEM \"http://commons.omniupdate.com/dtd/standard.dtd\">\n\n<document>\n<!-- com.omniupdate.properties -->\n<title>Homepage</title>\n<!-- /com.omniupdate.properties -->\n</document>";
}
