
package model.impostazioni;

import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class ErrorHandler extends DefaultHandler{
	
	public void warning (SAXParseException e) {
		System.out.println("Parsing Warning: "+e.getMessage());
	}
	
	public void error (SAXParseException e) {
		System.out.println("XML document NOT valid");
		System.out.println("Parsing Error: "+e.getMessage());
	}
	
	public void fatalError (SAXParseException e) {
		System.out.println("XML document NOT well-formed");
		System.out.println("Parsing Fatal Error: "+e.getMessage());
	}
	
}
