package ee.hm.dop.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class IssueDateTest {

    @Test
    public void equals() {
        IssueDate issueDate = new IssueDate();

        assertTrue(issueDate.equals(issueDate));
        assertFalse(issueDate.equals(null));

        IssueDate issueDate2 = new IssueDate();
        assertTrue(issueDate.equals(issueDate2));

        issueDate.setId((long) 4);
        assertTrue(issueDate.equals(issueDate2));

        issueDate.setYear(1236);
        assertFalse(issueDate.equals(issueDate2));

        issueDate2.setYear(1236);
        assertTrue(issueDate.equals(issueDate2));

        issueDate.setMonth((short) 1);
        assertFalse(issueDate.equals(issueDate2));

        issueDate2.setMonth((short) 1);
        assertTrue(issueDate.equals(issueDate2));

        issueDate.setDay((short) 27);
        assertFalse(issueDate.equals(issueDate2));

        issueDate2.setDay((short) 27);
        assertTrue(issueDate.equals(issueDate2));

        assertFalse(issueDate.equals(new Material()));
    }

    @Test
    public void testHashCode() {
        IssueDate issueDate = new IssueDate();
        IssueDate issueDate2 = new IssueDate();
        assertEquals(issueDate.hashCode(), issueDate2.hashCode());

        issueDate.setId((long) 4);
        assertEquals(issueDate.hashCode(), issueDate2.hashCode());

        issueDate.setYear(1236);
        assertNotEquals(issueDate.hashCode(), issueDate2.hashCode());

        issueDate2.setYear(1236);
        assertEquals(issueDate.hashCode(), issueDate2.hashCode());

        issueDate.setMonth((short) 1);
        assertNotEquals(issueDate.hashCode(), issueDate2.hashCode());

        issueDate2.setMonth((short) 1);
        assertEquals(issueDate.hashCode(), issueDate2.hashCode());

        issueDate.setDay((short) 27);
        assertNotEquals(issueDate.hashCode(), issueDate2.hashCode());

        issueDate2.setDay((short) 27);
        assertEquals(issueDate.hashCode(), issueDate2.hashCode());
    }

}
