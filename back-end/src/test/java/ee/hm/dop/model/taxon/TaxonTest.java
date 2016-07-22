package ee.hm.dop.model.taxon;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TaxonTest {

    @Test
    public void equals() {
        Taxon taxon = new TaxonImpl();

        assertTrue(taxon.equals(taxon));
        assertFalse(taxon.equals(null));

        Taxon taxon2 = new TaxonImpl();
        assertTrue(taxon.equals(taxon2));

        taxon.setId((long) 4);
        assertTrue(taxon.equals(taxon2));

        taxon.setName("taxon");
        assertFalse(taxon.equals(taxon2));

        taxon2.setName("taxon2");
        assertFalse(taxon.equals(taxon2));

        taxon2.setName("taxon");
        assertTrue(taxon.equals(taxon2));

        Taxon taxon3 = new Taxon() {
            @Override
            public Taxon getParent() {
                return null;
            }
        };

        assertFalse(taxon.equals(taxon3));

        taxon3.setId((long) 4);
        taxon3.setName("taxon");
        assertFalse(taxon.equals(taxon3));

        assertFalse(taxon.equals(new Object()));
    }

    @Test
    public void testHashCode() {
        Taxon taxon = new TaxonImpl();
        Taxon taxon2 = new TaxonImpl();
        assertEquals(taxon.hashCode(), taxon2.hashCode());

        taxon.setId((long) 4);
        assertEquals(taxon.hashCode(), taxon2.hashCode());

        taxon.setName("taxon");
        assertNotEquals(taxon.hashCode(), taxon2.hashCode());

        taxon2.setName("taxon");
        assertEquals(taxon.hashCode(), taxon2.hashCode());

        taxon2.setName("taxon2");
        assertNotEquals(taxon.hashCode(), taxon2.hashCode());

        Taxon taxon3 = new Taxon() {
            @Override
            public Taxon getParent() {
                return null;
            }
        };
        assertNotEquals(taxon.hashCode(), taxon3.hashCode());

        taxon3.setId((long) 4);
        taxon3.setName("taxon");
        assertNotEquals(taxon.hashCode(), taxon3.hashCode());
    }

    private class TaxonImpl extends Taxon {
        @Override
        public Taxon getParent() {
            return null;
        }
    }

}
