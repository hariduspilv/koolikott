package ee.hm.dop.rest.filter;

import static ee.hm.dop.utils.DbUtils.closeEntityManager;
import static ee.hm.dop.utils.DbUtils.closeTransaction;
import static ee.hm.dop.utils.DbUtils.getTransaction;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * Manage database transactions.
 */
public class TransactionFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        getTransaction().begin();

        chain.doFilter(request, response);

        closeTransaction();
        closeEntityManager();
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }
}