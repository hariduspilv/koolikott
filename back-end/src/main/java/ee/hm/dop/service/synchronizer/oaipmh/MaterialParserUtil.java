package ee.hm.dop.service.synchronizer.oaipmh;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.HashMap;
import java.util.Map;

public class MaterialParserUtil {

    public static Map<String, String> getTaxonMap() {
        Map<String, String> taxonMap = new HashMap<>();
        taxonMap.put("preschoolTaxon", "preschoolEducation");
        taxonMap.put("basicSchoolTaxon", "basicEducation");
        taxonMap.put("gymnasiumTaxon", "secondaryEducation");
        taxonMap.put("vocationalTaxon", "vocationalEducation");
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
}
