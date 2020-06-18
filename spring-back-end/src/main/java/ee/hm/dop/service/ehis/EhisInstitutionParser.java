package ee.hm.dop.service.ehis;

import ee.hm.dop.config.Configuration;
import ee.hm.dop.dao.InstitutionEhisDao;
import ee.hm.dop.dao.UserInstitutionDao;
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

import javax.xml.xpath.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.apache.commons.lang.StringUtils.isNotBlank;

@Service
@Transactional
public class EhisInstitutionParser {

    private static XPath xpath = XPathFactory.newInstance().newXPath();
    @Autowired
    private InstitutionEhisDao institutionEhisDao;
    @Autowired
    private UserInstitutionDao userInstitutionDao;
    @Autowired
    private Configuration configuration;

    private static final Logger logger = LoggerFactory.getLogger(EhisInstitutionParser.class);

    List<Integer> parseAndUpdateDb(URL url) throws DocumentException {
        SAXReader saxReader = new SAXReader();
        Document document = new DOMWriter().write(saxReader.read(url));

        List<InstitutionEhis> institutionsFromXml = getEhisInstitutions(document);

        List<Integer> syncInfo = new ArrayList<>();
        syncInfo.add(0, institutionsFromXml.size());

        int addCounter = 0;
        int removeCounter = 0;

        for (InstitutionEhis ie : institutionsFromXml) {
            InstitutionEhis dbEntity = institutionEhisDao.findByField("ehisId", ie.getEhisId());
            if (dbEntity == null
                    && checkAreaExists(ie)
                    && checkStatusIsNotClosed(ie)
                    && checkTypeIsNotSupplementaryTraining(ie)
                    && checkTypeIsNotYouthCamp(ie)
                    && checkTypeIsNotExtracurricularActivity(ie)) {
                ie.setArea(ie.getArea().trim());
                institutionEhisDao.createOrUpdate(ie);
                addCounter++;
            } else if (dbEntity != null
                    && (!checkAreaExists(ie) || !checkStatusIsNotClosed(ie)
                    || !checkTypeIsNotSupplementaryTraining(ie) || !checkTypeIsNotYouthCamp(ie)
                    || !checkTypeIsNotExtracurricularActivity(ie))) {
                int removedUserInstitutions = userInstitutionDao.removeExpiredUserInstitutions(dbEntity.getId());
                institutionEhisDao.remove(dbEntity);
                logger.info("EHIS institution removed: " + dbEntity.getName() + " with " + removedUserInstitutions + " UserInstitution occurrences");
                removeCounter++;
            }
        }
        syncInfo.add(1, addCounter);
        syncInfo.add(2, removeCounter);

        return syncInfo;
    }

    private boolean checkAreaExists(InstitutionEhis institutionEhis){
        return (isNotBlank(institutionEhis.getArea()) && institutionEhis.getArea() != null);
    }

    private boolean checkStatusIsNotClosed(InstitutionEhis institutionEhis){
        return !(institutionEhis.getStatus().equalsIgnoreCase(configuration.getString(ConfigurationProperties.XROAD_EHIS_INSTITUTIONS_CLOSED)));
    }

    private boolean checkTypeIsNotSupplementaryTraining(InstitutionEhis institutionEhis) {
        return !(institutionEhis.getType().equalsIgnoreCase(configuration.getString(ConfigurationProperties.XROAD_EHIS_INSTITUTIONS_SUPPLEMENTTRAINING)));
    }

    private boolean checkTypeIsNotYouthCamp(InstitutionEhis institutionEhis){
        return !(institutionEhis.getType().equalsIgnoreCase(configuration.getString(ConfigurationProperties.XROAD_EHIS_INSTITUTIONS_YOUTHCAMP)));
    }

    private boolean checkTypeIsNotExtracurricularActivity(InstitutionEhis institutionEhis) {
        return !(institutionEhis.getType().equalsIgnoreCase(configuration.getString(ConfigurationProperties.XROAD_EHIS_INSTITUTIONS_EXTRAACTIVITY)));
    }

    private List<InstitutionEhis> getEhisInstitutions(Document document) {
        NodeList institutionsNode = getNodeList(document, "//*[local-name()='oppeasutus']");
        return IntStream.range(0, institutionsNode.getLength())
                .mapToObj(institutionsNode::item)
                .map(this::getInstitution)
                .filter(this::institutionHasNecessaryAttributes)
                .collect(Collectors.toList());
    }

    private InstitutionEhis getInstitution(Node institutionNode) {
        return new InstitutionEhis(Long.valueOf(getInstitutionAttr(institutionNode, "koolId")),
                getInstitutionAttr(institutionNode, "nimetus"),
                getInstitutionAttr(institutionNode, "maakond"),
                getInstitutionAttr(institutionNode, "staatus"),
                getInstitutionAttr(institutionNode, "tyyp"));
    }

    private String getInstitutionAttr(Node institutionElement, String attr) {
        NodeList institutionAttribute = ((Element)institutionElement).getElementsByTagName(attr);
        if (institutionAttribute.getLength() == 0) {
            return null;
        } else {
            return institutionAttribute.item(0).getTextContent();
        }
    }

    private boolean institutionHasNecessaryAttributes(InstitutionEhis institutionEhis) {
        return institutionEhis.getEhisId() != null && institutionEhis.getName() != null && institutionEhis.getArea() != null;
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
