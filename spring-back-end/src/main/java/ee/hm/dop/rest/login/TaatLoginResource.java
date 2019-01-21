package ee.hm.dop.rest.login;

import ee.hm.dop.model.AuthenticatedUser;
import ee.hm.dop.rest.BaseResource;
import ee.hm.dop.service.login.TaatService;
import ee.hm.dop.service.login.dto.UserStatus;
import org.opensaml.common.SAMLObject;
import org.opensaml.common.binding.BasicSAMLMessageContext;
import org.opensaml.saml2.binding.encoding.HTTPRedirectDeflateEncoder;
import org.opensaml.saml2.core.AuthnRequest;
import org.opensaml.ws.message.encoder.MessageEncodingException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

import static ee.hm.dop.rest.login.LoginResource.LOGIN_REDIRECT_WITHOUT_TOKEN;
import static ee.hm.dop.rest.login.LoginResource.LOGIN_REDIRECT_WITH_TOKEN;
import static ee.hm.dop.rest.login.LoginResource.LOGIN_REDIRECT_WITH_TOKEN_AGREEMENT;
import static java.lang.String.format;

@RestController
@Deprecated
@RequestMapping("login")
public class TaatLoginResource extends BaseResource {

    public static final String SAML_RESPONSE = "SAMLResponse";
    public static final String RELAY_STATE = "RelayState";
    @Inject
    private TaatService taatService;
    @Inject
    private HTTPRedirectDeflateEncoder encoder;

    @GetMapping("/taat")

    public String taatLogin() throws MessageEncodingException {
        BasicSAMLMessageContext<SAMLObject, AuthnRequest, SAMLObject> context = taatService.buildMessageContext(getResponse());
        encoder.encode(context);
        return null;
    }

    @PostMapping("/taat")
    public Response taatAuthenticate(MultivaluedMap<String, String> params) throws URISyntaxException {
        try {
            UserStatus status = taatService.authenticate(params.getFirst(SAML_RESPONSE), params.getFirst(RELAY_STATE));
            return redirect(redirectSuccess(status));
        } catch (Exception e){
            return redirect(redirectFailure());
        }
    }

    private URI redirectSuccess(UserStatus status) throws URISyntaxException {
        if (status.isStatusOk()) {
            return new URI(format(LOGIN_REDIRECT_WITH_TOKEN, getServerAddress(), status.getAuthenticatedUser().getToken()));
        }
        return new URI(format(LOGIN_REDIRECT_WITH_TOKEN_AGREEMENT, getServerAddress(), status.getToken(), status.getAgreementId().toString(), status.isExistingUser(), status.getLoginFrom().name()));
    }

    private URI redirectFailure() throws URISyntaxException {
        return new URI(format(LOGIN_REDIRECT_WITHOUT_TOKEN, getServerAddress()));
    }
}
