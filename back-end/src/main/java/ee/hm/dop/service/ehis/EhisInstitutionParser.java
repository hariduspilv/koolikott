package ee.hm.dop.service.ehis;

import ee.hm.dop.config.guice.GuiceInjector;
import ee.hm.dop.dao.InstitutionEhisDao;
import ee.hm.dop.model.ehis.InstitutionEhis;
import org.dom4j.DocumentException;
import org.dom4j.io.DOMWriter;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;

class EhisInstitutionParser {

    private static Logger logger = LoggerFactory.getLogger(EhisInstitutionParser.class);
    private static XPath xpath = XPathFactory.newInstance().newXPath();
    private final String statusClosed = "Suletud";
    private final String instType= "koolieelne lasteasutus";

    private InstitutionEhisDao institutionEhisDao = newInstitutionEhisDao();

    void parseAndUpdateDb(URL url) throws DocumentException {
        SAXReader saxReader = new SAXReader();
        Document document = new DOMWriter().write(saxReader.read(url));

        List<InstitutionEhis> institutionsFromXml = getEhisInstitutions(document);
        logger.info("EHIS institution. Found -" + institutionsFromXml.size() + " institutions");

        int addCounter = 0;
        int removeCounter = 0;

        for (InstitutionEhis ie : institutionsFromXml) {
            if (institutionEhisDao.findByField("ehisId", ie.getEhisId()) == null && !(ie.getType().equalsIgnoreCase(instType))) {
                institutionEhisDao.createOrUpdate(ie);
                addCounter ++;
            } else {
                if (ie.getStatus().equalsIgnoreCase(statusClosed)) {
                    institutionEhisDao.remove(ie);
                    removeCounter++;
                }
            }
        }
        logger.info("EHIS institution.Added " + addCounter + " institutions into DB");
        logger.info("EHIS institution.Removed " + removeCounter + " institutions from DB");
    }

    private List<InstitutionEhis> getEhisInstitutions(Document document) {
        List<InstitutionEhis> institutions = new ArrayList<>();
        NodeList institutionsNode = getNodeList(document, "//*[local-name()='oppeasutus']");
        for (int i = 0; i < institutionsNode.getLength(); i++) {
            InstitutionEhis institution = getInstitution(institutionsNode.item(i));
            institutions.add(institution);
        }
        return institutions;
    }

    private InstitutionEhis getInstitution(Node institutionNode) {
        InstitutionEhis institution = new InstitutionEhis();
        institution.setEhisId(getInstitutionId((Element) institutionNode));
        institution.setName(getInstitutionName((Element) institutionNode));
        institution.setStatus(getInstitutionStatus((Element) institutionNode));
        institution.setType(getInstitutionType((Element) institutionNode));
        institution.setArea(getInstitutionArea((Element) institutionNode));

        return institution;
    }

    private Long getInstitutionId(Element institutionElement) {
        return Long.valueOf(institutionElement.getElementsByTagName("koolId").item(0).getTextContent());
    }

    private String getInstitutionAttr(Element institutionElement,String attr) {
        return institutionElement.getElementsByTagName(attr).item(0).getTextContent();
    }

    private String getInstitutionName(Element institutionElement) {
        return institutionElement.getElementsByTagName("nimetus").item(0).getTextContent();
    }
    private String getInstitutionStatus(Element institutionElement) {
        return institutionElement.getElementsByTagName("staatus").item(0).getTextContent();
    }
    private String getInstitutionType(Element institutionElement) {
        return institutionElement.getElementsByTagName("tyyp").item(0).getTextContent();
    }
    private String getInstitutionArea(Element institutionElement) {
        return institutionElement.getElementsByTagName("maakond").item(0).getTextContent();
    }

    private NodeList getNodeList(Object item, String path) {
        try {
            XPathExpression expr = xpath.compile(path);
            return (NodeList) expr.evaluate(item, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            throw new RuntimeException(e);
        }
    }

    private InstitutionEhisDao newInstitutionEhisDao(){
        return GuiceInjector.getInjector().getInstance(InstitutionEhisDao.class);
    }
}
