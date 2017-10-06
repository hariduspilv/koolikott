package ee.hm.dop.service.ehis;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import ee.hm.dop.model.ehis.Institution;
import ee.hm.dop.model.ehis.Person;
import ee.hm.dop.model.ehis.Role;
import ee.hm.dop.model.ehis.Role.InstitutionalRole;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class EhisParser {

    private static XPath xpath = XPathFactory.newInstance().newXPath();

    public Person parse(String input) {
        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                    .parse(new InputSource(new StringReader(input)));
            doc.getDocumentElement().normalize();
            return parse(doc);
        } catch (Exception ignored) {
            return null;
        }
    }

    private Person parse(Document doc) {
        Person person = new Person();
        person.setInstitutions(getInstitutions(doc));
        return person;
    }

    private List<Institution> getInstitutions(Document doc) {
        List<Institution> institutions = new ArrayList<>();

        NodeList institutionsNode = getNodeList(doc,
                "//*[local-name()='isik']/*[local-name()='oppeasutused']//*[local-name()='oppeasutus']");

        for (int i = 0; i < institutionsNode.getLength(); i++) {
            Institution institution = getInstitution(institutionsNode.item(i));
            institutions.add(institution);
        }

        return institutions;
    }

    private Institution getInstitution(Node institutionNode) {
        Institution institution = new Institution();
        institution.setEhisId(getInstitutionId((Element) institutionNode));
        institution.setRoles(getRoles(institutionNode));
        return institution;
    }

    private List<Role> getRoles(Node institutionNode) {
        List<Role> roles = new ArrayList<>();

        NodeList roleNodes = getNodeList(institutionNode, "./*[local-name()='rollid']//*[local-name()='roll']");
        for (int i = 0; i < roleNodes.getLength(); i++) {
            roles.add(getRole((Element) roleNodes.item(i)));
        }

        return roles;
    }

    private Role getRole(Element roleElement) {
        Role role = new Role();
        role.setInstitutionalRole(getInstitutionalRole(roleElement));
        role.setSchoolYear(getSchoolYear(roleElement));
        role.setSchoolClass(getSchoolClass(roleElement));
        return role;
    }

    private InstitutionalRole getInstitutionalRole(Element roleElement) {
        String estonianRoleName = roleElement.getElementsByTagName("nimetus").item(0).getTextContent();
        return InstitutionalRole.byEstonianName(estonianRoleName);
    }

    private String getSchoolYear(Element roleElement) {
        NodeList schoolYearNodeList = roleElement.getElementsByTagName("klass");
        if (schoolYearNodeList.getLength() > 0) {
            return schoolYearNodeList.item(0).getTextContent();
        }
        return null;
    }

    private String getSchoolClass(Element roleElement) {
        NodeList schoolClassNodeList = roleElement.getElementsByTagName("paralleel");
        if (schoolClassNodeList.getLength() > 0) {
            return schoolClassNodeList.item(0).getTextContent();
        }
        return null;
    }

    private String getInstitutionId(Element institutionElement) {
        return institutionElement.getElementsByTagName("id").item(0).getTextContent();
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
