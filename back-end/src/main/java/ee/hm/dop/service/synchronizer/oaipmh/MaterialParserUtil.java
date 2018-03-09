package ee.hm.dop.service.synchronizer.oaipmh;

import org.w3c.dom.NodeList;

public class MaterialParserUtil {

    public static boolean notEmpty(NodeList nl) {
        return nl != null && nl.getLength() > 0;
    }
}
