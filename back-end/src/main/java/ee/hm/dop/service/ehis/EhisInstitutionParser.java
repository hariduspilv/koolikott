package ee.hm.dop.service.ehis;

import ee.hm.dop.dao.InstitutionEhisDao;
import ee.hm.dop.model.ehis.InstitutionEhis;
import org.dom4j.DocumentException;
import org.dom4j.io.DOMWriter;
import org.dom4j.io.SAXReader;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.inject.Inject;
import javax.xml.xpath.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class EhisInstitutionParser {

    private static XPath xpath = XPathFactory.newInstance().newXPath();
    private final String statusClosed = "Suletud";

    @Inject
    private InstitutionEhisDao institutionEhisDao;

//    private InstitutionEhisDao institutionEhisDao = new InstitutionEhisDao();

    public void parseAndUpdateDb(URL url) throws DocumentException {
        SAXReader saxReader = new SAXReader();
        org.w3c.dom.Document document = new DOMWriter().write(saxReader.read(url));

        List<InstitutionEhis> institutionsFromXml = getEhisInstitutions(document);

        for (InstitutionEhis ie : institutionsFromXml) {
            if (institutionEhisDao.findByField("ehisId", ie.getEhisId()) == null) {
                institutionEhisDao.createOrUpdate(ie);
            } else {
                if (ie.getStatus().equalsIgnoreCase(statusClosed)) {
                    institutionEhisDao.remove(ie);
                }
            }
        }
    }
    private List<InstitutionEhis> getEhisInstitutions(org.w3c.dom.Document document) {
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

        return institution;
    }

    private Long getInstitutionId(Element institutionElement) {
        return Long.valueOf(institutionElement.getElementsByTagName("koolId").item(0).getTextContent());
    }

    private String getInstitutionName(Element institutionElement) {
        return institutionElement.getElementsByTagName("nimetus").item(0).getTextContent();
    }
    private String getInstitutionStatus(Element institutionElement) {
        return institutionElement.getElementsByTagName("staatus").item(0).getTextContent();
    }

    private NodeList getNodeList(Object item, String path) {
        try {
            XPathExpression expr = xpath.compile(path);
            return (NodeList) expr.evaluate(item, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            throw new RuntimeException(e);
        }
    }
}
