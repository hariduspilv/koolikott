package ee.hm.dop.ehis;

import java.io.File;
import java.io.StringReader;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;

import com.fasterxml.jackson.databind.ObjectMapper;

import ee.hm.dop.ehis.pojo.EhisIsik;

public class EhisParser {

	public static EhisIsik parse(String input) {
		try {	
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            EhisHandler userhandler = new EhisHandler();
            saxParser.parse(new InputSource(new StringReader(input)), userhandler);     

            EhisIsik ehisObject = userhandler.getEhisIsik();

            return ehisObject;
         } catch (Exception e) {
            e.printStackTrace();
         }
		return null;
	}
	
}
