package ee.hm.dop.model.harid;

import org.junit.Test;

import static org.junit.Assert.*;

public class HarIdUserTest {

    @Test
    public void getIdCodeNumbers() {
        assertEquals("898989", new HarIdUser("898989", "M", "Roos").getIdCodeNumbers());
        assertEquals("898989", new HarIdUser("EE:898989", "M", "Roos").getIdCodeNumbers());
    }
}