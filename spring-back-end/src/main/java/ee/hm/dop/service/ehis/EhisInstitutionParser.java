package ee.hm.dop.service.ehis;

import ee.hm.dop.config.Configuration;
import ee.hm.dop.dao.InstitutionEhisDao;
import ee.hm.dop.model.ehis.InstitutionEhis;
import ee.hm.dop.utils.ConfigurationProperties;
import org.dom4j.DocumentException;
import org.dom4j.io.DOMWriter;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Transactional
public class EhisInstitutionParser {

    private static XPath xpath = XPathFactory.newInstance().newXPath();
    @Autowired
    private InstitutionEhisDao institutionEhisDao;
    @Autowired
    private Configuration configuration;

    public List<Integer>  parseAndUpdateDb(URL url) throws DocumentException {
        SAXReader saxReader = new SAXReader();
        Document document = new DOMWriter().write(saxReader.read(url));

        List<InstitutionEhis> institutionsFromXml = getEhisInstitutions(document);
        return updateDb(institutionsFromXml);
    }

    private List<Integer> updateDb(List<InstitutionEhis> institutionsFromXml) {
        List<Integer> syncInfo = new ArrayList<>();
        syncInfo.add(0, institutionsFromXml.size());
        int addCounter = 0;
        int removeCounter = 0;
        for (InstitutionEhis ie : institutionsFromXml) {
            if (institutionEhisDao.findByField("ehisId", ie.getEhisId()) == null
                    && checkAreaExists(ie)
                    && checkStatusIsNotClosed(ie)
                    && checkInstTypeIsNotPreSchool(ie)) {
                institutionEhisDao.createOrUpdate(ie);
                addCounter++;
            } else if (institutionEhisDao.findByField("ehisId", ie.getEhisId()) != null
                    && (!checkAreaExists(ie) || !checkStatusIsNotClosed(ie) || !checkInstTypeIsNotPreSchool(ie))) {
                institutionEhisDao.remove(ie);
                removeCounter++;
            }
        }
        syncInfo.add(1, addCounter);
        syncInfo.add(2, removeCounter);

        return syncInfo;
    }

    private boolean checkAreaExists(InstitutionEhis institutionEhis){
        return !(institutionEhis.getArea().equalsIgnoreCase("") || institutionEhis.getArea() == null);
    }
    private boolean checkStatusIsNotClosed(InstitutionEhis institutionEhis){
        return !(institutionEhis.getStatus().equalsIgnoreCase(configuration.getString(ConfigurationProperties.XROAD_EHIS_INSTITUTION_STATUS)));
    }
    private boolean checkInstTypeIsNotPreSchool(InstitutionEhis institutionEhis){
        return !(institutionEhis.getType().equalsIgnoreCase(configuration.getString(ConfigurationProperties.XROAD_EHIS_INSTITUTION_TYPE)));
    }

    private List<InstitutionEhis> getEhisInstitutions(Document document) {
        NodeList institutionsNode = getNodeList(document, "//*[local-name()='oppeasutus']");
        return IntStream.range(0, institutionsNode.getLength())
                .mapToObj(institutionsNode::item)
                .map(this::getInstitution)
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
