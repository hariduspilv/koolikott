package ee.hm.dop.service;

import static ee.hm.dop.utils.ConfigurationProperties.MOBILEID_ENDPOINT;
import static ee.hm.dop.utils.ConfigurationProperties.MOBILEID_NAMESPACE_PREFIX;
import static ee.hm.dop.utils.ConfigurationProperties.MOBILEID_NAMESPACE_URI;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.apache.commons.configuration.Configuration;
import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EasyMockRunner.class)
public class MobileIDSOAPServiceTest {

    @TestSubject
    private MobileIDSOAPService mobileIDSOAPService = new MobileIDSOAPService();

    @Mock
    private Configuration configuration;

    @Mock
    private SOAPConnection connection;

    @Test
    public void createSOAPMessage() throws SOAPException {
        String messageName = "TestSoapMessage";

        Map<String, String> childElements = new HashMap<>();
        childElements.put("TestElement", "42");
        childElements.put("TestElement2", "example");

        expect(configuration.getString(MOBILEID_NAMESPACE_PREFIX)).andReturn("prefix");
        expect(configuration.getString(MOBILEID_NAMESPACE_URI))
                .andReturn("http://www.example.com/Service/Service.wsdl");

        replayAll();

        SOAPMessage message = mobileIDSOAPService.createSOAPMessage(messageName, childElements);

        verifyAll();

        SOAPBody body = message.getSOAPPart().getEnvelope().getBody();
        SOAPElement messageElement = (SOAPElement) body.getChildElements().next();
        assertEquals(messageName, messageElement.getElementName().getLocalName());

        @SuppressWarnings("unchecked")
        Iterator<SOAPElement> iterator = messageElement.getChildElements();
        Map<String, String> response = new HashMap<>();
        while (iterator.hasNext()) {
            SOAPElement element = iterator.next();
            response.put(element.getElementName().getLocalName(), element.getValue());
        }

        assertEquals(childElements, response);
    }

    @Test
    public void sendSOAPMessage() throws SOAPException {
        SOAPMessage requestMessage = createMock(SOAPMessage.class);
        SOAPMessage responseMessage = createMock(SOAPMessage.class);
        String endpoint = "https://www.example.com:9876/Service";

        expect(configuration.getString(MOBILEID_ENDPOINT)).andReturn(endpoint);
        expect(connection.call(requestMessage, endpoint)).andReturn(responseMessage);

        replayAll();

        SOAPMessage response = mobileIDSOAPService.sendSOAPMessage(requestMessage);

        verifyAll();

        assertSame(responseMessage, response);
    }

    @Test
    public void parseSOAPResponse() throws IOException, SOAPException {
        String message = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" " //
                + "xmlns:SOAP-ENC=\"http://schemas.xmlsoap.org/soap/encoding/\" " //
                + "xmlns:dig=\"http://www.example.com/Service/Service.wsdl\" " //
                + "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" " //
                + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" //
                + "<SOAP-ENV:Header/>" //
                + "<SOAP-ENV:Body SOAP-ENV:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">" //
                + "<dig:TestResponse>" //
                + "<TestFieldAlpha xsi:type=\"xsd:int\">4001</TestFieldAlpha>" //
                + "<TestFieldBeta xsi:type=\"xsd:string\">onetwothree</TestFieldBeta>" //
                + "</dig:TestResponse>" //
                + "</SOAP-ENV:Body></SOAP-ENV:Envelope>";

        InputStream is = new ByteArrayInputStream(message.getBytes());
        SOAPMessage request = MessageFactory.newInstance().createMessage(null, is);

        replayAll();

        Map<String, String> response = mobileIDSOAPService.parseSOAPResponse(request);

        verifyAll();

        assertEquals(2, response.size());
        assertEquals("4001", response.get("TestFieldAlpha"));
        assertEquals("onetwothree", response.get("TestFieldBeta"));
    }

    @Test
    public void parseSOAPResponseWithFault() throws SOAPException, IOException {
        String message = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">" //
                + "<SOAP-ENV:Header/>" //
                + "<SOAP-ENV:Body>" //
                + "<SOAP-ENV:Fault>" //
                + "<faultcode>SOAP-ENV:Client</faultcode>" //
                + "<faultstring xml:lang=\"en\">301</faultstring>" //
                + "<detail><message>User is not a Mobile-ID client</message></detail>" //
                + "</SOAP-ENV:Fault>" //
                + "</SOAP-ENV:Body>" //
                + "</SOAP-ENV:Envelope>";

        InputStream is = new ByteArrayInputStream(message.getBytes());
        SOAPMessage request = MessageFactory.newInstance().createMessage(null, is);

        replayAll();

        Map<String, String> response = null;
        try {
            response = mobileIDSOAPService.parseSOAPResponse(request);
            fail("Exception expected.");
        } catch (RuntimeException e) {
            assertEquals("SOAPResponse Fault 301: User is not a Mobile-ID client", e.getMessage());
        }

        verifyAll();

        assertNull(response);
    }

    private void replayAll(Object... mocks) {
        replay(configuration, connection);

        if (mocks != null) {
            for (Object object : mocks) {
                replay(object);
            }
        }
    }

    private void verifyAll(Object... mocks) {
        verify(configuration, connection);

        if (mocks != null) {
            for (Object object : mocks) {
                verify(object);
            }
        }
    }

}
