package ee.hm.dop.utils;

import org.apache.http.util.TextUtils;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.regex.Pattern;

public class UrlUtil {
    public static final String WWW_PREFIX = "www.";
    public static final String DEFAULT_PROTOCOL = "http://";
    public static final Pattern VALID_URL_PATTERN = Pattern.compile("^(?:https?://)?(?:\\S+(?::\\S*)?@)?(?:(?!(?:10|127)(?:\\.\\d{1,3}){3})(?!(?:169\\.254|192\\.168)(?:\\.\\d{1,3}){2})(?!172\\.(?:1[6-9]|2\\d|3[0-1])(?:\\.\\d{1,3}){2})(?:[1-9]\\d?|1\\d\\d|2[01]\\d|22[0-3])(?:\\.(?:1?\\d{1,2}|2[0-4]\\d|25[0-5])){2}(?:\\.(?:[1-9]\\d?|1\\d\\d|2[0-4]\\d|25[0-4]))|(?:(?:[a-z\\u00a1-\\uffff0-9]-*)*[a-z\\u00a1-\\uffff0-9]+)(?:\\.(?:[a-z\\u00a1-\\uffff0-9]-*)*[a-z\\u00a1-\\uffff0-9]+)*(?:\\.(?:[a-z\\u00a1-\\uffff]{2,}))\\.?)(?::\\d{2,5})?(?:[/?#]\\S*)?$");

    /**
     * Removes protocol (http, https..) form url
     * Removes 'www.' prefix from host
     *
     * @param materialSource url
     * @return materialSource without schema and www
     */
    public static String getURLWithoutProtocolAndWWW(String materialSource) {
        if (TextUtils.isBlank(materialSource)) return null;

        try {
            URL url = new URL(materialSource);
            String hostName = url.getHost();

            if (hostName.startsWith(WWW_PREFIX) && isValidURL(hostName.substring(4))) {
                hostName = hostName.substring(4);
            }

            return String.format("%s%s", hostName, url.getFile());
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Source has no protocol");
        }
    }


    public static String getDomainName(String url) throws URISyntaxException {
        URI uri = new URI(url);
        if (uri.getScheme() == null) {
            uri = new URI("http://" + url);
        }

        String domain = uri.getHost().trim();
        return domain.startsWith("www.") ? domain.substring(4) : domain;
    }

    public static String getHost(String materialSource) {
        if (TextUtils.isBlank(materialSource)) return null;

        try {
            URL url = new URL(materialSource);
            return url.getHost();
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Source has no protocol");
        }
    }

    /**
     * Removes trailing slash
     * Adds protocol if missing
     *
     * @param materialSource url
     * @return url with protocol and without trailing slash
     */
    public static String processURL(String materialSource) {
        if (TextUtils.isBlank(materialSource)) return null;

        try {
            // Removes trailing slash
            materialSource = materialSource.replaceAll("/$", "");

            // Throws exception if protocol is not specified
            return new URL(materialSource).toString();
        } catch (MalformedURLException e) {
            return DEFAULT_PROTOCOL + materialSource;
        }
    }

    private static boolean isValidURL(String url) {
        return VALID_URL_PATTERN.matcher(url).matches();
    }
}
