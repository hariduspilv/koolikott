package ee.hm.dop.config.soap;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

public interface SOAPConnectionMockI {

    public SOAPMessage call(SOAPMessage request, Object to) throws SOAPException;
}