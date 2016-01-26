package ee.hm.dop.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.xml.soap.SOAPException;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.xml.security.utils.Base64;
import org.json.JSONObject;
import org.opensaml.common.SAMLObject;
import org.opensaml.common.binding.BasicSAMLMessageContext;
import org.opensaml.saml2.binding.encoding.HTTPRedirectDeflateEncoder;
import org.opensaml.saml2.core.AuthnRequest;
import org.opensaml.ws.message.encoder.MessageEncodingException;

import ee.hm.dop.model.AuthenticatedUser;
import ee.hm.dop.model.mobileid.MobileIDSecurityCodes;
import ee.hm.dop.service.AuthenticatedUserService;
import ee.hm.dop.service.LanguageService;
import ee.hm.dop.service.LoginService;
import ee.hm.dop.service.TaatService;

@Path("login")
public class LogInResource extends BaseResource {

    @Inject
    private LoginService loginService;

    @Inject
    private TaatService taatService;

    @Inject
    private HTTPRedirectDeflateEncoder encoder;

    @Inject
    private AuthenticatedUserService authenticatedUserService;

    @Inject
    private LanguageService languageService;

    @GET
    @Path("/idCard")
    @Produces(MediaType.APPLICATION_JSON)
    public AuthenticatedUser idCardLogin() {
        AuthenticatedUser authenticatedUser = null;

        if (isAuthValid()) {
            authenticatedUser = loginService.logIn(getIdCodeFromRequest(), getNameFromRequest(),
                    getSurnameFromRequest());
        }

        return authenticatedUser;
    }

    @GET
    @Path("/taat")
    @Produces(MediaType.APPLICATION_JSON)
    public void taatLogin() throws MessageEncodingException {
        BasicSAMLMessageContext<SAMLObject, AuthnRequest, SAMLObject> context = taatService
                .buildMessageContext(getResponse());
        encoder.encode(context);
    }

    @POST
    @Path("/taat")
    public Response taatAuthenticate(MultivaluedMap<String, String> formParams) throws URISyntaxException {
        AuthenticatedUser authenticatedUser = taatService.authenticate(formParams.getFirst("SAMLResponse"),
                formParams.getFirst("RelayState"));
        URI location = new URI("../#/loginRedirect?token=" + authenticatedUser.getToken());

        return Response.temporaryRedirect(location).build();
    }

    @GET
    @Path("ekoolLogin")
    public Response ekoolAuthenticate() throws URISyntaxException {
        String baseUrl = "https://chucknorris.ekool.eu/auth/oauth/authorize";
        String client_id = "koolikott";
        String client_secret = "9rIxgey74Ke87OVYhCZfezyJ6g95UeLI9YxIhY0FuH8m";
        String callback = "https://" + getRequest().getServerName() + "/rest/login/ekool";

        URI location = new URI(baseUrl + "?client_id=" + client_id + "&secret=" + client_secret + "&redirect_uri="
                + callback + "&scope=read&response_type=code");
        System.out.println(baseUrl + "?client_id=" + client_id + "&secret=" + client_secret + "&redirect_uri="
                + callback + "&scope=read&response_type=code");
        return Response.temporaryRedirect(location).build();
    }

    @GET
    @Path("ekool")
    public Response ekoolCallback(@QueryParam("code") String code) throws Exception {
        System.out.println("##### ekoolCallback #####");
        if (code == null) {
            throw new RuntimeException("Missing required parameter code");
        }

        HttpResponse response = getToken(code);
        int responseCode = response.getStatusLine().getStatusCode();
        String result = readResponse(response);

        if (responseCode == 200) {
            JSONObject resultJson = new JSONObject(result);
            String token = resultJson.getString("access_token");

            HttpResponse userResponse = readUserdata(token);
            int userResponeResponseCode = userResponse.getStatusLine().getStatusCode();
            if (userResponeResponseCode == 200) {
                String userResponseString = readResponse(userResponse);

                JSONObject userObject = new JSONObject(userResponseString);
                String firstName = userObject.getString("firstName");
                String lastName = userObject.getString("lastName");
                String idCode = userObject.getString("idCode");

                AuthenticatedUser authenticatedUser = loginService.logIn(idCode, firstName, lastName);
                URI location = new URI("../#/loginRedirect?token=" + authenticatedUser.getToken());
                return Response.temporaryRedirect(location).build();
            }

        }

        throw new RuntimeException("Ekool login failed");
    }

    @GET
    @Path("/mobileId")
    @Produces(MediaType.APPLICATION_JSON)
    public MobileIDSecurityCodes mobileIDLogin(@QueryParam("phoneNumber") String phoneNumber,
            @QueryParam("idCode") String idCode, @QueryParam("language") String languageCode) throws Exception {
        return loginService.mobileIDAuthenticate(phoneNumber, idCode, languageService.getLanguage(languageCode));
    }

    @GET
    @Path("/mobileId/isValid")
    @Produces(MediaType.APPLICATION_JSON)
    public AuthenticatedUser mobileIDAuthenticate(@QueryParam("token") String token) throws SOAPException {
        return loginService.validateMobileIDAuthentication(token);
    }

    @GET
    @Path("/getAuthenticatedUser")
    @Produces(MediaType.APPLICATION_JSON)
    public AuthenticatedUser getAuthenticatedUser(@QueryParam("token") String token) {
        return authenticatedUserService.getAuthenticatedUserByToken(token);
    }

    private String getIdCodeFromRequest() {
        String[] values = getRequest().getHeader("SSL_CLIENT_S_DN").split(",");
        return getStringInUTF8(values[0].split("=")[1]);
    }

    private String getNameFromRequest() {
        String[] values = getRequest().getHeader("SSL_CLIENT_S_DN").split(",");
        return getStringInUTF8(values[1].split("=")[1]);
    }

    private String getSurnameFromRequest() {
        String[] values = getRequest().getHeader("SSL_CLIENT_S_DN").split(",");
        return getStringInUTF8(values[2].split("=")[1]);
    }

    private boolean isAuthValid() {
        return "SUCCESS".equals(getRequest().getHeader("SSL_AUTH_VERIFY"));
    }

    private String getStringInUTF8(String item) {
        byte[] bytes = item.getBytes(StandardCharsets.ISO_8859_1);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    private HttpResponse getToken(String code) throws Exception {
        String url = "https://chucknorris.ekool.eu/auth/oauth/token";

        HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(url);

        // Generate HTTP basic auth header
        String client_id = "koolikott";
        String client_secret = "9rIxgey74Ke87OVYhCZfezyJ6g95UeLI9YxIhY0FuH8m";
        String combined = client_id + ":" + client_secret;
        byte[] bytes = combined.getBytes();
        String hash = new String(Base64.encode(bytes));
        post.setHeader("content-type", "application/x-www-form-urlencoded");
        post.setHeader("Authorization", "Basic " + hash);

        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
        urlParameters.add(new BasicNameValuePair("grant_type", "authorization_code"));
        urlParameters.add(new BasicNameValuePair("redirect_uri", "https://localhost/rest/login/ekool"));
        urlParameters.add(new BasicNameValuePair("code", code));
        urlParameters.add(new BasicNameValuePair("client_id", client_id));
        urlParameters.add(new BasicNameValuePair("client_secret", client_secret));

        post.setEntity(new UrlEncodedFormEntity(urlParameters));
        HttpResponse response = client.execute(post);
        return response;
    }

    public HttpResponse readUserdata(String access_token) throws ClientProtocolException, IOException {
        String url = "https://chucknorris.ekool.eu/auth/generaldata";

        HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(url);

        // Generate HTTP basic auth header
        String client_id = "koolikott";
        String client_secret = "9rIxgey74Ke87OVYhCZfezyJ6g95UeLI9YxIhY0FuH8m";
        String combined = client_id + ":" + client_secret;
        byte[] bytes = combined.getBytes();
        String hash = new String(Base64.encode(bytes));
        post.setHeader("content-type", "application/x-www-form-urlencoded");
        post.setHeader("Authorization", "Basic " + hash);

        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
        urlParameters.add(new BasicNameValuePair("access_token", access_token));

        post.setEntity(new UrlEncodedFormEntity(urlParameters));
        HttpResponse response = client.execute(post);
        return response;
    }

    public String readResponse(HttpResponse response) throws UnsupportedOperationException, IOException {
        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        return result.toString();
    }

}
