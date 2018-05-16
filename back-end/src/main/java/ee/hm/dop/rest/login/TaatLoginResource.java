package ee.hm.dop.rest.login;

import ee.hm.dop.model.AuthenticatedUser;
import ee.hm.dop.rest.BaseResource;
import ee.hm.dop.service.login.TaatService;
import org.opensaml.common.SAMLObject;
import org.opensaml.common.binding.BasicSAMLMessageContext;
import org.opensaml.saml2.binding.encoding.HTTPRedirectDeflateEncoder;
import org.opensaml.saml2.core.AuthnRequest;
import org.opensaml.ws.message.encoder.MessageEncodingException;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;

import static ee.hm.dop.rest.LoginResource.LOGIN_REDIRECT_WITH_TOKEN;

@Deprecated
@Path("login")
public class TaatLoginResource extends BaseResource{

    @Inject
    private TaatService taatService;
    @Inject
    private HTTPRedirectDeflateEncoder encoder;

    @GET
    @Path("/taat")
    @Produces(MediaType.APPLICATION_JSON)
    public String taatLogin() throws MessageEncodingException {
        BasicSAMLMessageContext<SAMLObject, AuthnRequest, SAMLObject> context = taatService.buildMessageContext(getResponse());
        encoder.encode(context);
        return null;
    }

    @POST
    @Path("/taat")
    public Response taatAuthenticate(MultivaluedMap<String, String> formParams) throws URISyntaxException {
        AuthenticatedUser authenticatedUser = taatService.authenticate(
                formParams.getFirst("SAMLResponse"),
                formParams.getFirst("RelayState"));
        return redirect(new URI(LOGIN_REDIRECT_WITH_TOKEN + authenticatedUser.getToken()));
    }
}
