package ee.hm.dop.service.ehis;

import static ee.hm.dop.model.ehis.Role.InstitutionalRole.PRINCIPAL;
import static ee.hm.dop.model.ehis.Role.InstitutionalRole.STUDENT;
import static ee.hm.dop.model.ehis.Role.InstitutionalRole.TEACHER;
import static ee.hm.dop.utils.DOPFileUtils.readFileAsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;

import ee.hm.dop.model.ehis.Institution;
import ee.hm.dop.model.ehis.Person;
import ee.hm.dop.model.ehis.Role;
import ee.hm.dop.service.ehis.EhisParser;
import org.junit.Test;

public class EhisParserTest {

    private EhisParser ehisParser = new EhisParser();

    @Test
    public void testParse() {
        String xml = readFileAsString("ehis/person_39201210144.xml");

        Person person = ehisParser.parse(xml);

        List<Institution> institutions = person.getInstitutions();
        assertEquals(3, institutions.size());

        Institution institution1 = institutions.get(0);
        Role roleInstitution1 = institution1.getRoles().get(0);
        assertEquals("OPPEASUTUS_ID_1", institution1.getEhisId());
        assertEquals(STUDENT, roleInstitution1.getInstitutionalRole());
        assertEquals("5", roleInstitution1.getSchoolYear());
        assertEquals("B", roleInstitution1.getSchoolClass());

        Institution institution2 = institutions.get(1);
        Role role1Institution2 = institution2.getRoles().get(0);
        Role role2Institution2 = institution2.getRoles().get(1);
        assertEquals("OPPEASUTUS_ID_2", institution2.getEhisId());
        // Role 1
        assertEquals(TEACHER, role1Institution2.getInstitutionalRole());
        assertNull(role1Institution2.getSchoolYear());
        assertNull(role1Institution2.getSchoolClass());
        // Role 2
        assertEquals(STUDENT, role2Institution2.getInstitutionalRole());
        assertEquals("12", role2Institution2.getSchoolYear());
        assertNull(role2Institution2.getSchoolClass());

        Institution institution3 = institutions.get(2);
        Role roleInstitution3 = institution3.getRoles().get(0);
        assertEquals("OPPEASUTUS_ID_3", institution3.getEhisId());
        assertEquals(PRINCIPAL, roleInstitution3.getInstitutionalRole());
        assertNull(roleInstitution3.getSchoolYear());
        assertNull(roleInstitution3.getSchoolClass());
    }
}
