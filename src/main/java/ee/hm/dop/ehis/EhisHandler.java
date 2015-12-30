package ee.hm.dop.ehis;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import ee.hm.dop.ehis.pojo.EhisIsik;
import ee.hm.dop.ehis.pojo.EhisOppeasutus;
import ee.hm.dop.ehis.pojo.EhisRoll;

public class EhisHandler extends DefaultHandler {

	private EhisIsik ehisIsik;
	private List<EhisOppeasutus> ehisOppeasutused;
	private List<EhisRoll> ehisRollid;
	private EhisOppeasutus ehisOppeasutusHolder;
	private EhisRoll ehisRollHolder;

	boolean isik = false;
	boolean isikukood = false;
	boolean eesnimi = false;
	boolean perenimi = false;
	
	boolean oppeasutused = false;
	boolean oppeasutus = false;
	boolean oppeasutus_id = false;
	boolean oppeasutus_reg_nr = false;
	boolean oppeasutus_nimetus = false;
	
	boolean rollid = false;
	boolean roll = false;
	boolean roll_nimetus = false;
	boolean roll_klass = false;
	boolean roll_paralleel = false;
	
	public EhisHandler() {
		ehisIsik = new EhisIsik();
		ehisOppeasutused = new ArrayList<>();
	}
	
	public EhisIsik getEhisIsik() {
		ehisIsik.setOppeasutused(ehisOppeasutused);
		return ehisIsik;
	}
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (qName.equalsIgnoreCase("isik")) {
			isik = true;
		} else if (qName.equalsIgnoreCase("isikukood")) {
			isikukood = true;
		} else if (qName.equalsIgnoreCase("eesnimi")) {
			eesnimi = true;
		} else if (qName.equalsIgnoreCase("perenimi")) {
			perenimi = true;
		} 
		
		if (qName.equalsIgnoreCase("oppeasutused")) {
			oppeasutused = true;
		} else if (qName.equalsIgnoreCase("oppeasutus")) {
			ehisOppeasutusHolder = new EhisOppeasutus();
			oppeasutus = true;
		} 
		
		if(oppeasutus && !roll) {
			if (qName.equalsIgnoreCase("id")) {
				oppeasutus_id = true;
			} else if (qName.equalsIgnoreCase("reg_nr")) {
				oppeasutus_reg_nr = true;
			} else if (qName.equalsIgnoreCase("nimetus")) {
				oppeasutus_nimetus = true;
			}
		}
		
		if (qName.equalsIgnoreCase("rollid")) {
			ehisRollid = new ArrayList<>();
			rollid = true;
		} else if (qName.equalsIgnoreCase("roll")) {
			ehisRollHolder = new EhisRoll();
			roll = true;
		}
		
		if(roll) {
			if (qName.equalsIgnoreCase("nimetus")) {
				roll_nimetus = true;
			} else if (qName.equalsIgnoreCase("klass")) {
				roll_klass = true;
			} else if (qName.equalsIgnoreCase("paralleel")) {
				roll_paralleel = true;
			}
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (qName.equalsIgnoreCase("isik")) {
			isik = false;
		} else if (qName.equalsIgnoreCase("oppeasutused")) {
			oppeasutused = false;
		} else if (qName.equalsIgnoreCase("oppeasutus")) {
			ehisOppeasutused.add(ehisOppeasutusHolder);
			oppeasutus = false;
		} else if (qName.equalsIgnoreCase("rollid")) {
			ehisOppeasutusHolder.setRollid(ehisRollid);
			rollid = false;
		} else if (qName.equalsIgnoreCase("roll")) {
			ehisRollid.add(ehisRollHolder);
			roll = false;
		}
	}

	@Override
	public void characters(char ch[], int start, int length) throws SAXException {
		if (isikukood) {
			ehisIsik.setIsikukood(new String(ch, start, length));
			isikukood = false;
		} else if (eesnimi) {
			ehisIsik.setEesnimi(new String(ch, start, length));
			eesnimi = false;
		} else if (perenimi) {
			ehisIsik.setPerenimi(new String(ch, start, length));
			perenimi = false;
		}
		
		if(oppeasutus && !roll) {
			if (oppeasutus_id) {
				ehisOppeasutusHolder.setId(new String(ch, start, length));
				oppeasutus_id = false;
			} else if (oppeasutus_reg_nr) {
				ehisOppeasutusHolder.setRegNr(new String(ch, start, length));
				oppeasutus_reg_nr = false;
			} else if (oppeasutus_nimetus) {
				ehisOppeasutusHolder.setNimetus(new String(ch, start, length));
				oppeasutus_nimetus = false;
			}
		}
		
		if(roll) {
			if (roll_nimetus) {
				ehisRollHolder.setNimetus(new String(ch, start, length));
				roll_nimetus = false;
			} else if (roll_klass) {
				ehisRollHolder.setKlass(new String(ch, start, length));
				roll_klass = false;
			} else if (roll_paralleel) {
				ehisRollHolder.setParalleel(new String(ch, start, length));
				roll_paralleel = false;
			}
		}
		
	}
	
}
