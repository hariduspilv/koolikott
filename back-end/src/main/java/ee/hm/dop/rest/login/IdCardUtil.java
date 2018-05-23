package ee.hm.dop.rest.login;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;

public class IdCardUtil {

    private static final String SSL_CLIENT_S_DN = "SSL_CLIENT_S_DN";

    public static boolean isAuthValid(HttpServletRequest request) {
        return "SUCCESS".equals(request.getHeader("SSL_AUTH_VERIFY"));
    }

    public static String getIdCode(HttpServletRequest request) {
        return getString(request, 0);
    }

    public static String getName(HttpServletRequest request) {
        return getString(request, 1);
    }

    public static String getSurname(HttpServletRequest request) {
        return getString(request, 2);
    }

    private static String getString(HttpServletRequest request, int i) {
        String[] values = request.getHeader(SSL_CLIENT_S_DN).split(",");
        return getStringInUTF8(values[i].split("=")[1]);
    }

    private static String getStringInUTF8(String item) {
        byte[] bytes = item.getBytes(StandardCharsets.ISO_8859_1);
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
