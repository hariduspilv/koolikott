package ee.hm.dop.service.content;

import ee.hm.dop.utils.UrlUtil;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.util.URIUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.UnknownHostException;

import static java.lang.String.format;

public class MaterialProxy {

    private static final String PDF_EXTENSION = ".pdf\"";
    private static final String PDF_MIME_TYPE = "application/pdf";
    public static final String CONTENT_DISPOSITION = "Content-Disposition";
    public static final String CONTENT_TYPE = "Content-Type";
    private Logger logger = LoggerFactory.getLogger(getClass());

    public Response getProxyUrl(String url_param) throws IOException {
        HttpClient client = new HttpClient();
        client.setHostConfiguration(hostConfig(url_param));
        GetMethod get = getMethod(url_param);

        try {
            client.executeMethod(get);
        } catch (UnknownHostException | IllegalArgumentException e) {
            logger.info("Could not contact host, returning empty response: " + e.getMessage());
            return Response.noContent().build();
        }

        String attachmentLocation = attachmentLocation(get, PDF_EXTENSION, PDF_MIME_TYPE);
        if (attachmentLocation.equals(CONTENT_DISPOSITION)) {
            String contentDisposition = get.getResponseHeaders(CONTENT_DISPOSITION)[0].getValue().replace("attachment", "Inline");
            return buildContentDispositionResponse(get.getResponseBody(), contentDisposition);
        }
        if (attachmentLocation.equals(CONTENT_TYPE)) {
            // Content-Disposition is missing, try to extract the filename from url instead
            String fileName = url_param.substring(url_param.lastIndexOf("/") + 1, url_param.length());
            String contentDisposition = format("Inline; filename=\"%s\"", fileName);
            return buildContentDispositionResponse(get.getResponseBody(), contentDisposition);
        }
        return Response.noContent().build();
    }

    private Response buildContentDispositionResponse(byte[] responseBody, String contentDisposition) throws IOException {
        return Response.ok(responseBody, PDF_MIME_TYPE).header(CONTENT_DISPOSITION,
                contentDisposition).build();
    }

    private HostConfiguration hostConfig(String url_param) {
        HostConfiguration hostConfiguration = new HostConfiguration();
        hostConfiguration.setHost(UrlUtil.getHost(url_param));
        return hostConfiguration;
    }

    private GetMethod getMethod(String url_param) throws URIException {
        try {
            return new GetMethod(url_param);
        } catch (IllegalArgumentException e) {
            return new GetMethod(URIUtil.encodePath(url_param));
        }
    }

    private String attachmentLocation(GetMethod get, String extension, String mime_type) {
        Header[] contentDisposition = get.getResponseHeaders(CONTENT_DISPOSITION);
        Header[] contentType = get.getResponseHeaders(CONTENT_TYPE);
        if (contentDisposition.length > 0 && contentDisposition[0].getValue().toLowerCase().endsWith(extension)) {
            return CONTENT_DISPOSITION;
        }
        if (contentType.length > 0 && contentType[0].getValue().toLowerCase().endsWith(mime_type)) {
            return CONTENT_TYPE;
        }
        return "Invalid";
    }
}
