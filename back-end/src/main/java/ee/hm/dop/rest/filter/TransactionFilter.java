package ee.hm.dop.rest.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import ee.hm.dop.utils.DbUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manage database transactions.
 */
public class TransactionFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(TransactionFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        beginTransaction();

        chain.doFilter(request, response);

        try {
            closeTransaction();
        } catch (Exception e) {
            logger.error("Error closing transaction", e);
            throw new RuntimeException("Error closing transaction");
        } finally {
            closeEntityManager();
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }

    protected void beginTransaction() {
        DbUtils.getTransaction().begin();
    }

    protected void closeTransaction() {
        DbUtils.closeTransaction();
    }

    protected void closeEntityManager() {
        DbUtils.closeEntityManager();
    }
}