package ee.hm.dop.service.ehis;

import ee.hm.dop.config.Configuration;
import ee.hm.dop.dao.InstitutionEhisDao;
import ee.hm.dop.model.ehis.InstitutionEhis;
import ee.hm.dop.utils.ConfigurationProperties;
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
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Transactional
public class EhisInstitutionParser {

    private static Logger logger = LoggerFactory.getLogger(EhisInstitutionParser.class);
    private static XPath xpath = XPathFactory.newInstance().newXPath();
    private static long ehisStartOfSync = 0L;
    private static long ehisEndOfSync = 0L;
    @Autowired
    private InstitutionEhisDao institutionEhisDao;
    @Autowired
    private Configuration configuration;

    public void parseAndUpdateDb(URL url) throws DocumentException {
        SAXReader saxReader = new SAXReader();
        Document document = new DOMWriter().write(saxReader.read(url));

        ehisStartOfSync = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
        List<InstitutionEhis> institutionsFromXml = getEhisInstitutions(document);
        logger.info("EHIS institution.Found - " + institutionsFromXml.size() + " institutions");
        updateDb(institutionsFromXml);
    }

    private void updateDb(List<InstitutionEhis> institutionsFromXml) {
        int addCounter = 0;
        int removeCounter = 0;
        for (InstitutionEhis ie : institutionsFromXml) {
            if (institutionEhisDao.findByField("ehisId", ie.getEhisId()) == null
                    && getInstAreaCondition(ie)
                    && getInstStatusCondition(ie)
                    && getInstTypeCondition(ie)) {
                institutionEhisDao.createOrUpdate(ie);
                addCounter++;
            } else if (institutionEhisDao.findByField("ehisId", ie.getEhisId()) != null
                    && (!getInstAreaCondition(ie) || !getInstStatusCondition(ie) || !getInstTypeCondition(ie))) {
                institutionEhisDao.remove(ie);
                removeCounter++;
            }
        }
        ehisEndOfSync = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
        long syncDuration= ehisEndOfSync - ehisStartOfSync;
        logger.info("EHIS institution.Added " + addCounter + " institutions into DB");
        logger.info("EHIS institution.Removed " + removeCounter + " institutions");
        logger.info("EHIS institution sync took " + syncDuration + " seconds");
    }

    private boolean getInstAreaCondition(InstitutionEhis institutionEhis){
        return !(institutionEhis.getArea().equalsIgnoreCase("") || institutionEhis.getArea() == null);
    }
    private boolean getInstStatusCondition(InstitutionEhis institutionEhis){
        return !(institutionEhis.getStatus().equalsIgnoreCase(configuration.getString(ConfigurationProperties.XROAD_EHIS_INSTITUTION_STATUS)));
    }
    private boolean getInstTypeCondition(InstitutionEhis institutionEhis){
        return !(institutionEhis.getType().equalsIgnoreCase(configuration.getString(ConfigurationProperties.XROAD_EHIS_INSTITUTION_TYPE)));
    }

    private List<InstitutionEhis> getEhisInstitutions(Document document) {
        NodeList institutionsNode = getNodeList(document, "//*[local-name()='oppeasutus']");
        return IntStream.range(0, institutionsNode.getLength()).mapToObj(institutionsNode::item).map(this::getInstitution)
                .collect(Collectors.toList());
    }

    private InstitutionEhis getInstitution(Node institutionNode) {
        return new InstitutionEhis(
                Long.valueOf(getInstitutionAttr(institutionNode, "koolId")),
                getInstitutionAttr(institutionNode, "nimetus"),
                getInstitutionAttr(institutionNode, "maakond"),
                getInstitutionAttr(institutionNode, "staatus"),
                getInstitutionAttr(institutionNode, "tyyp"));
    }

    private String getInstitutionAttr(Node institutionElement, String attr) {
        return ((Element)institutionElement).getElementsByTagName(attr).item(0).getTextContent();
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
