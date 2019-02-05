package ee.hm.dop.service.ehis;

import org.junit.Test;

import javax.inject.Inject;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class EhisV6ResponseAnalyzerTest {

    @Inject
    private EhisV6ResponseAnalyzer ehisV6ResponseAnalyzer;

    @Test
    public void role_response_returns_person() throws Exception {
        SOAPMessage msg = getSoapMessageFromString(role_response);
        String text = ehisV6ResponseAnalyzer.parseSOAPResponse(msg);
        assertEquals(user, text);
    }

    @Test
    public void fail_response_fails_with_exception() throws Exception {
        SOAPMessage msg = getSoapMessageFromString(fail_response);
        try {
            ehisV6ResponseAnalyzer.parseSOAPResponse(msg);
            fail();
        } catch (Exception e) {
            assertTrue(e instanceof RuntimeException);
            assertEquals("Error retrieving information from EHIS: veakood: NOTFOUND", e.getMessage());
        }
    }


    private SOAPMessage getSoapMessageFromString(String xml) throws SOAPException, IOException {
        MessageFactory factory = MessageFactory.newInstance();
        return factory.createMessage(new MimeHeaders(), new ByteArrayInputStream(xml.getBytes(Charset.forName("UTF-8"))));
    }

    public static String user = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><isik>\n" +
            "                    <isikukood>45805217556</isikukood>\n" +
            "                    <eesnimi>EN_134163</eesnimi>\n" +
            "                    <perenimi>PN_134163</perenimi>\n" +
            "                    <oppeasutused>\n" +
            "                        <oppeasutus>\n" +
            "                            <id>388</id>\n" +
            "                            <reg_nr>75012966</reg_nr>\n" +
            "                            <nimetus>Haapsalu Põhikool</nimetus>\n" +
            "                            <rollid>\n" +
            "                                <roll>\n" +
            "                                    <nimetus>koolijuht</nimetus>\n" +
            "                                </roll>\n" +
            "                                <roll>\n" +
            "                                    <nimetus>õpetaja</nimetus>\n" +
            "                                </roll>\n" +
            "                            </rollid>\n" +
            "                        </oppeasutus>\n" +
            "                    </oppeasutused>\n" +
            "                </isik>";

    public static String role_response = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
            "    <soapenv:Header>\n" +
            "                  <xtee:asutus xmlns:xtee=\"http://x-tee.riik.ee/xsd/xtee.xsd\"\n" +
            "                     xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "                     xsi:type=\"xsd:string\">70000740</xtee:asutus>\n" +
            "                  <xtee:andmekogu xmlns:xtee=\"http://x-tee.riik.ee/xsd/xtee.xsd\">ehis</xtee:andmekogu>\n" +
            "                  <xtee:isikukood xmlns:xtee=\"http://x-tee.riik.ee/xsd/xtee.xsd\"\n" +
            "                        xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "                        xsi:type=\"xsd:string\">EE37804065728</xtee:isikukood>\n" +
            "                  <xtee:id xmlns:xtee=\"http://x-tee.riik.ee/xsd/xtee.xsd\">1d4ec56318419d9b1c265dbab5ee28436166531b</xtee:id>\n" +
            "                  <xtee:nimi xmlns:xtee=\"http://x-tee.riik.ee/xsd/xtee.xsd\">ehis.isiku_rollid.v1</xtee:nimi>\n" +
            "                  <xtee:amet xmlns:xtee=\"http://x-tee.riik.ee/xsd/xtee.xsd\"/>\n" +
            "                  <xtee:toimik xmlns:xtee=\"http://x-tee.riik.ee/xsd/xtee.xsd\"/>\n" +
            "                  <xtee:autentija xmlns:xtee=\"http://x-tee.riik.ee/xsd/xtee.xsd\">ID_CARD</xtee:autentija>\n" +
            "                  <xtee:ametniknimi xmlns:xtee=\"http://x-tee.riik.ee/xsd/xtee.xsd\">TOMMY TOMSON</xtee:ametniknimi>\n" +
            "               </soapenv:Header>\n" +
            "    <soapenv:Body>\n" +
            "        <ns7:isiku_rollidResponse xmlns:ns7=\"http://producers.ehis.xtee.riik.ee/producer/ehis\">\n" +
            "                <isik>\n" +
            "                    <isikukood>45805217556</isikukood>\n" +
            "                    <eesnimi>EN_134163</eesnimi>\n" +
            "                    <perenimi>PN_134163</perenimi>\n" +
            "                    <oppeasutused>\n" +
            "                        <oppeasutus>\n" +
            "                            <id>388</id>\n" +
            "                            <reg_nr>75012966</reg_nr>\n" +
            "                            <nimetus>Haapsalu Põhikool</nimetus>\n" +
            "                            <rollid>\n" +
            "                                <roll>\n" +
            "                                    <nimetus>koolijuht</nimetus>\n" +
            "                                </roll>\n" +
            "                                <roll>\n" +
            "                                    <nimetus>õpetaja</nimetus>\n" +
            "                                </roll>\n" +
            "                            </rollid>\n" +
            "                        </oppeasutus>\n" +
            "                    </oppeasutused>\n" +
            "                </isik>\n" +
            "        </ns7:isiku_rollidResponse>\n" +
            "    </soapenv:Body>\n" +
            "</soapenv:Envelope>";

    public static String fail_response = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
            "    <soapenv:Header>\n" +
            "                  <xtee:asutus xmlns:xtee=\"http://x-tee.riik.ee/xsd/xtee.xsd\"\n" +
            "                     xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "                     xsi:type=\"xsd:string\">70000740</xtee:asutus>\n" +
            "                  <xtee:andmekogu xmlns:xtee=\"http://x-tee.riik.ee/xsd/xtee.xsd\">ehis</xtee:andmekogu>\n" +
            "                  <xtee:isikukood xmlns:xtee=\"http://x-tee.riik.ee/xsd/xtee.xsd\"\n" +
            "                        xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "                        xsi:type=\"xsd:string\">EE37804065728</xtee:isikukood>\n" +
            "                  <xtee:id xmlns:xtee=\"http://x-tee.riik.ee/xsd/xtee.xsd\">1d4ec56318419d9b1c265dbab5ee28436166531b</xtee:id>\n" +
            "                  <xtee:nimi xmlns:xtee=\"http://x-tee.riik.ee/xsd/xtee.xsd\">ehis.isiku_rollid.v1</xtee:nimi>\n" +
            "                  <xtee:amet xmlns:xtee=\"http://x-tee.riik.ee/xsd/xtee.xsd\"/>\n" +
            "                  <xtee:toimik xmlns:xtee=\"http://x-tee.riik.ee/xsd/xtee.xsd\"/>\n" +
            "                  <xtee:autentija xmlns:xtee=\"http://x-tee.riik.ee/xsd/xtee.xsd\">ID_CARD</xtee:autentija>\n" +
            "                  <xtee:ametniknimi xmlns:xtee=\"http://x-tee.riik.ee/xsd/xtee.xsd\">TOMMY TOMSON</xtee:ametniknimi>\n" +
            "               </soapenv:Header>\n" +
            "   <soapenv:Body>" +
            "       <ns3:isikuRollidResponse xmlns:ns3=\"http://producers.ehis.xtee.riik.ee/producer/ehis\">" +
            "           <veakood>NOTFOUND</veakood>" +
            "       </ns3:isikuRollidResponse>" +
            "   </soapenv:Body>" +
            "</soapenv:Envelope>";
}