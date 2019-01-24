package ee.hm.dop.rest.login;

import ee.hm.dop.service.login.dto.IdCardInfo;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class IdCardUtilTest {

    public static final String PRE_2018_ID_HEADER = "serialNumber=39906089999,GN=EHE,SN=SAAR,CN=SAAR\\,EHE\\,39906089999,OU=authentication,O=ESTEID,C=EE";
    public static final String _2018_ID_HEADER_WITH_REGULAR_CHARS = "serialNumber=PNOEE-38001085718,GN=JAAK-KRISTJAN,SN=JOEORG,CN=JOEORG\\,JAAK-KRISTJAN\\,38001085718,C=EE";
    public static final String _2018_ID_HEADER_WITH_SPECIAL_CHARS = "serialNumber=PNOEE-38001085718,GN=JAAK-KRISTJAN,SN=JÃ\u0095EORG,CN=JÃ\u0095EORG\\,JAAK-KRISTJAN\\,38001085718,C=EE";

    @Test
    public void you_can_get_person_info_from_pre_2018_id_card_header() {
        IdCardInfo info = IdCardUtil.getIdCardInfo(PRE_2018_ID_HEADER);
        assertEquals("EHE", info.getFirstName());
        assertEquals("SAAR", info.getSurName());
        assertEquals("39906089999", info.getIdCode());
    }

    @Test
    public void you_can_get_person_info_from_2018_id_card_header_without_special_chars() {
        IdCardInfo info = IdCardUtil.getIdCardInfo(_2018_ID_HEADER_WITH_REGULAR_CHARS);
        assertEquals("JAAK-KRISTJAN", info.getFirstName());
        assertEquals("JOEORG", info.getSurName());
        assertEquals("38001085718", info.getIdCode());
    }
    
    @Test
    public void you_can_get_person_info_from_2018_id_card_header_with_special_chars() {
        IdCardInfo info = IdCardUtil.getIdCardInfo(_2018_ID_HEADER_WITH_SPECIAL_CHARS);
        assertEquals("JAAK-KRISTJAN", info.getFirstName());
        assertEquals("JÕEORG", info.getSurName());
        assertEquals("38001085718", info.getIdCode());
    }
}