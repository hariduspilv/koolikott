package ee.hm.dop.service.synchronizer.oaipmh;

import ee.hm.dop.model.Material;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class MaterialParserUtil {

    public static final String PRESCHOOL_TAXON = "preschoolTaxon";
    public static final String VOCATIONAL_TAXON = "vocationalTaxon";
    public static final String SPECIALEDUCATION = "SPECIALEDUCATION";

    public static Map<String, String> getTaxonMap() {
        Map<String, String> taxonMap = new HashMap<>();
        taxonMap.put(PRESCHOOL_TAXON, "preschoolEducation");
        taxonMap.put("basicSchoolTaxon", "basicEducation");
        taxonMap.put("gymnasiumTaxon", "secondaryEducation");
        taxonMap.put(VOCATIONAL_TAXON, "vocationalEducation");
        return taxonMap;
    }

    public static boolean notEmpty(NodeList nl) {
        return nl != null && nl.getLength() > 0;
    }

    public static String value(Node node) {
        return node.getTextContent().trim();
    }

    public static String valueToUpper(Node node) {
        return value(node).toUpperCase();
    }

    public static boolean isSpecialEducation(String context) {
        return context.equals(SPECIALEDUCATION);
    }

    public static Node getFirst(Element element, String description) {
        return element.getElementsByTagName(description).item(0);
    }

    public static Stream<Node> nodeStreamOf(NodeList nl) {
        return IntStream.range(0, nl.getLength()).mapToObj(nl::item);
    }

    public static Material buildDeletedMaterial(String identifier) {
        Material material = new Material();
        material.setRepositoryIdentifier(identifier);
        material.setDeleted(true);
        return material;
    }
}
