package ee.hm.dop.service.ehis;

import ee.hm.dop.dao.InstitutionEhisDao;
import ee.hm.dop.model.ehis.InstitutionEhis;
import org.dom4j.DocumentException;
import org.dom4j.io.DOMWriter;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
@Transactional
public class EhisInstitutionParser {

    private static Logger logger = LoggerFactory.getLogger(EhisInstitutionParser.class);
    private static XPath xpath = XPathFactory.newInstance().newXPath();
    private final String statusClosed = "Suletud";
    private final String instType= "koolieelne lasteasutus";

    @Autowired
    private InstitutionEhisDao institutionEhisDao;

    public void parseAndUpdateDb(URL url) throws DocumentException {
        SAXReader saxReader = new SAXReader();
        Document document = new DOMWriter().write(saxReader.read(url));

        List<InstitutionEhis> institutionsFromXml = getEhisInstitutions(document);
        logger.info("EHIS institution.Found - " + institutionsFromXml.size() + " institutions");
        updateDb(institutionsFromXml);
    }

    private void updateDb(List<InstitutionEhis> institutionsFromXml) {
        int addCounter = 0;
        int removeCounter = 0;
        for (InstitutionEhis ie : institutionsFromXml) {
            if (institutionEhisDao.findByField("ehisId", ie.getEhisId()) == null && !(ie.getType().equalsIgnoreCase(instType))
                    && !(ie.getStatus().equalsIgnoreCase(statusClosed)) && !(ie.getArea().equalsIgnoreCase("") || ie.getArea() == null)) {
                institutionEhisDao.createOrUpdate(ie);
                addCounter++;
            } else {
                if (ie.getStatus().equalsIgnoreCase(statusClosed)) {
                    institutionEhisDao.remove(ie);
                    removeCounter++;
                }
            }
        }
        logger.info("EHIS institution.Added " + addCounter + " institutions into DB");
        logger.info("EHIS institution.Removed/not added " + removeCounter + " institutions");
    }

    private List<InstitutionEhis> getEhisInstitutions(Document document) {
        NodeList institutionsNode = getNodeList(document, "//*[local-name()='oppeasutus']");
        Stream<Node> nodeStream = IntStream.range(0, institutionsNode.getLength()).mapToObj(institutionsNode::item);
        return nodeStream.map(this::getInstitution)
                .collect(Collectors.toList());
    }

    private InstitutionEhis getInstitution(Node institutionNode) {
        return new InstitutionEhis(Long.valueOf(getInstitutionAttr((Element) institutionNode, "koolId")),
                getInstitutionAttr((Element) institutionNode, "nimetus"),
                getInstitutionAttr((Element) institutionNode, "maakond"),
                getInstitutionAttr((Element) institutionNode, "staatus"),
                getInstitutionAttr((Element) institutionNode, "tyyp"));
    }

    private String getInstitutionAttr(Element institutionElement, String attr) {
        return institutionElement.getElementsByTagName(attr).item(0).getTextContent();
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
