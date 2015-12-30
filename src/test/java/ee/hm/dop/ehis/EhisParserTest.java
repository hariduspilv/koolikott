package ee.hm.dop.ehis;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ee.hm.dop.ehis.pojo.EhisIsik;


public class EhisParserTest {

    @Test
    public void testParse() {
        String xml = "<isik>\r\n" + 
        		"	<isikukood>ISIKUKOOD</isikukood>\r\n" + 
        		"	<eesnimi>EESNIMI</eesnimi>\r\n" + 
        		"	<perenimi>PERENIMI</perenimi>\r\n" + 
        		"	<oppeasutused>\r\n" + 
        		"		<oppeasutus>\r\n" + 
        		"			<id>OPPEASUTUS_ID_1</id>\r\n" + 
        		"			<reg_nr>OPPEASUTUS_REG_NR_1</reg_nr>\r\n" + 
        		"			<nimetus>OPPEASUTUS_1</nimetus>\r\n" + 
        		"			<rollid>\r\n" + 
        		"				<roll>\r\n" + 
        		"					<nimetus>OPPEASUTUS_1_ROLL_1</nimetus>\r\n" + 
        		"					<klass>OPPEASUTUS_1_KLASS_1</klass>\r\n" + 
        		"					<paralleel>OPPEASUTUS_1_PARALLEEL_1</paralleel>\r\n" + 
        		"				</roll>\r\n" + 
        		"			</rollid>\r\n" + 
        		"		</oppeasutus>\r\n" + 
        		"		<oppeasutus>\r\n" + 
        		"			<id>OPPEASUTUS_ID_2</id>\r\n" + 
        		"			<reg_nr>OPPEASUTUS_REG_NR_2</reg_nr>\r\n" + 
        		"			<nimetus>OPPEASUTUS_2</nimetus>\r\n" + 
        		"			<rollid>\r\n" + 
        		"				<roll>\r\n" + 
        		"					<nimetus>OPPEASUTUS_2_ROLL_1</nimetus>\r\n" + 
        		"					<klass>OPPEASUTUS_2_KLASS_1</klass>\r\n" + 
        		"					<paralleel>OPPEASUTUS_2_PARALLEEL_1</paralleel>\r\n" + 
        		"				</roll>\r\n" + 
        		"				<roll>\r\n" + 
        		"					<nimetus>OPPEASUTUS_2_ROLL_2</nimetus>\r\n" + 
        		"					<klass>OPPEASUTUS_2_KLASS_2</klass>\r\n" + 
        		"					<paralleel>OPPEASUTUS_2_PARALLEEL_2</paralleel>\r\n" + 
        		"				</roll>\r\n" + 
        		"			</rollid>\r\n" + 
        		"		</oppeasutus>\r\n" + 
        		"		<oppeasutus>\r\n" + 
        		"			<id>OPPEASUTUS_ID_3</id>\r\n" + 
        		"			<reg_nr>OPPEASUTUS_REG_NR_3</reg_nr>\r\n" + 
        		"			<nimetus>OPPEASUTUS_3</nimetus>\r\n" + 
        		"			<rollid>\r\n" + 
        		"				<roll>\r\n" + 
        		"					<nimetus>OPPEASUTUS_3_ROLL_1</nimetus>\r\n" + 
        		"					<klass>OPPEASUTUS_3_KLASS_1</klass>\r\n" + 
        		"					<paralleel>OPPEASUTUS_3_PARALLEEL_1</paralleel>\r\n" + 
        		"				</roll>\r\n" + 
        		"			</rollid>\r\n" + 
        		"		</oppeasutus>\r\n" + 
        		"	</oppeasutused>\r\n" + 
        		"</isik>";
        
        
        EhisIsik ehisObject = EhisParser.parse(xml);
        
        assertEquals(ehisObject.getEesnimi(), "EESNIMI");
        assertEquals(ehisObject.getPerenimi(), "PERENIMI");
        assertEquals(ehisObject.getOppeasutused().get(0).getNimetus(), "OPPEASUTUS_1");
        assertEquals(ehisObject.getOppeasutused().get(1).getRollid().get(1).getNimetus(), "OPPEASUTUS_2_ROLL_2");
    }
    
}
