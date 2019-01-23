package ee.hm.dop.rest.errorhandling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityTransaction;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import static ee.hm.dop.utils.DbUtils.getTransaction;

@Provider
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Logger logger = LoggerFactory.getLogger(GenericExceptionMapper.class);

    @Override
    public Response toResponse(Throwable error) {
        logger.error("Handling error", error);

        setTransactionRollbackOnly();
        return getResponse(error);
    }

    private Response getResponse(Throwable error) {
        if (error instanceof WebApplicationException) {
            WebApplicationException webEx = (WebApplicationException) error;
            return webEx.getResponse();
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Internal error")
                .type("text/plain").build();
    }

    private void setTransactionRollbackOnly() {
        EntityTransaction transaction = getTransaction();
        if (transaction.isActive()) {
            transaction.setRollbackOnly();
        }
    }
}