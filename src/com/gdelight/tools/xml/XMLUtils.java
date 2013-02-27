package com.gdelight.tools.xml;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XMLUtils {

	public static Document parseXMLDocument(String xmlData) throws ParserConfigurationException, SAXException, IOException {
				
		Document xmlDoc = null;
		
		DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(xmlData));

		xmlDoc = db.parse(is);

		return xmlDoc;
	}
	
	public static String escapeIllegalXMLCharacters(String xmlData) {
		
		String parsedXML = xmlData.replace("&", "&amp;"); //replace any ampersands.
		
		return parsedXML;
		
	}

	public static String unEscapeIllegalXMLCharacters(String xmlData) {
		
		String parsedXML = xmlData.replace("&amp;", "&"); //replace any ampersands.
		
		return parsedXML;
		
	}

	/**
	 * XML utility method to get a value given an XML element
	 * @param sTag the tag being used as the key
	 * @param eElement the element containing the key
	 * @return the value the key corresponds with
	 */
	public static String getValueFromElement(String sTag, Element eElement) {
		String value = "";
		NodeList nlList = eElement.getElementsByTagName(sTag);
		if (nlList != null) {
			if (nlList.item(0) != null) {
				nlList = nlList.item(0).getChildNodes();
				if (nlList == null) {
					return value;
				}
			} else {
				return value;
			}
		} else {
			return value;
		}
		Node nValue = (Node) nlList.item(0);
		if (nValue != null) {
			value = nValue.getNodeValue();
		}
		return value;
	}
}
