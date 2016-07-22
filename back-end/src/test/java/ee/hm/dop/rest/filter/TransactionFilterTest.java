package ee.hm.dop.rest.filter;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createMockBuilder;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.lang.reflect.Method;

import javax.persistence.RollbackException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.easymock.EasyMockRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EasyMockRunner.class)
public class TransactionFilterTest {

    private TransactionFilter filter;

    @Before
    public void setup() throws NoSuchMethodException {
        filter = getTransactionFilterMock();
    }

    /**
     * When an exception is thrown while closing the transaction, the EntityManager
     * must still be closed afterwards.
     */
    @Test
    public void testExceptionClosingTransaction() throws IOException, ServletException {
        ServletRequest request = createMock(ServletRequest.class);
        ServletResponse response = createMock(ServletResponse.class);
        FilterChain chain = createMock(FilterChain.class);

        filter.beginTransaction();
        chain.doFilter(request, response);
        filter.closeTransaction();
        expectLastCall().andThrow(new RollbackException("Failed to commit transaction"));
        filter.closeEntityManager();

        replay(filter, request, response, chain);

        try {
            filter.doFilter(request, response, chain);
            fail("Exception expected");
        } catch (RuntimeException e) {
            assertEquals("Error closing transaction", e.getMessage());
        }

        verify(filter, request, response, chain);
    }

    private TransactionFilter getTransactionFilterMock() throws NoSuchMethodException {
        Method beginTransaction = TransactionFilter.class.getDeclaredMethod("beginTransaction");
        Method closeTransaction = TransactionFilter.class.getDeclaredMethod("closeTransaction");
        Method closeEntityManager = TransactionFilter.class.getDeclaredMethod("closeEntityManager");
        return createMockBuilder(TransactionFilter.class).addMockedMethods(beginTransaction, closeTransaction,
                closeEntityManager).createMock();
    }

}
