package ee.hm.dop.service.synchronizer.oaipmh;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.HashMap;
import java.util.Map;

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

    public static String valueToLower(Node node) {
        return value(node).toLowerCase();
    }

    public static boolean isSpecialEducation(String context) {
        return context.equals(SPECIALEDUCATION);
    }
}
