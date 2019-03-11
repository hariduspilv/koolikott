package ee.hm.dop.service.ehis;

import ee.hm.dop.dao.InstitutionEhisDao;
import ee.hm.dop.model.ehis.InstitutionEhis;
import org.dom4j.DocumentException;
import org.dom4j.io.DOMWriter;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


@Service
public class EhisInstitutionService {

    @Autowired
    private InstitutionEhisDao institutionEhisDao;

    private static XPath xpath = XPathFactory.newInstance().newXPath();

    RestTemplate restTemplate = new RestTemplate();
    String url = "http://enda.ehis.ee/avaandmed/rest/oppeasutused/74000624/-/-/-/-/-/-/-/-/0/0/XML";
    ResponseEntity<String> exchange = restTemplate.getForEntity(url, String.class);

    SAXReader saxReader = new SAXReader();
    String respons = exchange.getBody();

    URL docUrl;

    {
        try {
//          docUrl = new URL("http://enda.ehis.ee/avaandmed/rest/oppeasutused/74000624/-/-/-/-/-/-/-/-/0/0/XML");
            docUrl = new URL("http://enda.ehis.ee/avaandmed/rest/oppeasutused/-/-/-/-/-/-/-/-/-/0/0/XML");
            org.dom4j.Document document = saxReader.read(docUrl);
            org.w3c.dom.Document document1 = new DOMWriter().write(document);

            List<InstitutionEhis> listFromRemote = getEhisInstitutions(document1);

            List<InstitutionEhis> listFromDb = institutionEhisDao.findAll();



        } catch (MalformedURLException | DocumentException e) {
            e.printStackTrace();
        }
    }

    private List<InstitutionEhis> getEhisInstitutions(org.w3c.dom.Document document) {
        List<InstitutionEhis> institutions = new ArrayList<>();

        NodeList institutionsNode = getNodeList(document, "//*[local-name()='oppeasutused']//*[local-name()='oppeasutus']");

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
        return institution;
    }

    private Long getInstitutionId(Element institutionElement) {
        return Long.valueOf(institutionElement.getElementsByTagName("koolId").item(0).getTextContent());
    }

    private String getInstitutionName(Element institutionElement) {
        return institutionElement.getElementsByTagName("nimetus").item(0).getTextContent();
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
