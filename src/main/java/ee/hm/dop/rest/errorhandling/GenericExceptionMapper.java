package ee.hm.dop.rest.errorhandling;

import static ee.hm.dop.utils.DbUtils.getTransaction;

import javax.persistence.EntityTransaction;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        Response response;
        if (error instanceof WebApplicationException) {
            WebApplicationException webEx = (WebApplicationException) error;
            response = webEx.getResponse();
        } else {
            response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Internal error")
                    .type("text/plain").build();
        }

        return response;
    }

    private void setTransactionRollbackOnly() {
        EntityTransaction transaction = getTransaction();
        if (transaction.isActive()) {
            transaction.setRollbackOnly();
        }
    }
}