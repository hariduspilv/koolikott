package ee.hm.dop.rest.login;

import ee.hm.dop.service.login.dto.IdCardInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;

import static org.apache.commons.lang3.StringUtils.substringBetween;

public class IdCardUtil {

    public static final String SSL_CLIENT_S_DN = "SSL_CLIENT_S_DN";

    public static boolean isAuthValid(HttpServletRequest request) {
        return "SUCCESS".equals(request.getHeader("SSL_AUTH_VERIFY"));
    }

    public static IdCardInfo getInfo(HttpServletRequest request, Logger logger) {
        Enumeration<String> headers = request.getHeaders(SSL_CLIENT_S_DN);
        while (headers.hasMoreElements()){
            logger.info(headers.nextElement());
        }
        return getIdCardInfo(request.getHeader(SSL_CLIENT_S_DN));
    }

    public static IdCardInfo getIdCardInfo(String header) {
        String between = getUserInfoFromCN(header);
        String[] strings = between.split("\\\\,");
        return new IdCardInfo(utf8(strings[1]), utf8(strings[0]), utf8(strings[2]));
    }

    private static String getUserInfoFromCN(String header) {
        return header.contains(",OU=") ? substringBetween(header, "CN=", ",OU=") : substringBetween(header, "CN=", ",C=");
    }

    private static String utf8(String item) {
        return StringUtils.toEncodedString(item.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
    }
}
