package ee.hm.dop.oaipmh;

import org.w3c.dom.Document;

import ee.hm.dop.model.Material;

public interface MaterialParser {

    Material parse(Document doc) throws ParseException;
}
