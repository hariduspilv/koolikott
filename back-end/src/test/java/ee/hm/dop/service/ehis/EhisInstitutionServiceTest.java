package ee.hm.dop.service.ehis;

import org.dom4j.DocumentException;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;

public class EhisInstitutionServiceTest {

    private EhisInstitutionService ehisInstitutionService = new EhisInstitutionService();

    private EhisInstitutionParser ehisInstitutionParser = new EhisInstitutionParser();

//    @Inject
//    private EhisInstitutionParser ehisInstitutionParser;

    private final String ehisSchoolUrl = "http://enda.ehis.ee/avaandmed/rest/oppeasutused/-/-/-/-/-/-/-/-/-/0/0/XML";

    @Test
    public void getInstitutionsAndUpdateDb() throws MalformedURLException, DocumentException {
        URL url = new URL(ehisSchoolUrl);
        ehisInstitutionParser.parseAndUpdateDb(url);
    }
}