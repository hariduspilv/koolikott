package ee.hm.dop.rest.login;

import ee.hm.dop.service.login.dto.IdCardInfo;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

public class IdCardUtil {

    public static final String SSL_CLIENT_S_DN = "SSL_CLIENT_S_DN";
    public static final String SSL_AUTH_VERIFY = "SSL_AUTH_VERIFY";
    public static final String SUCCESS = "SUCCESS";

    public static boolean isAuthValid(HttpServletRequest request) {
        return SUCCESS.equals(request.getHeader(SSL_AUTH_VERIFY));
    }

    public static IdCardInfo getInfo(HttpServletRequest request) {
        return getIdCardInfo(request.getHeader(SSL_CLIENT_S_DN));
    }

    public static IdCardInfo getIdCardInfo(String header) {
        return getString(header);
    }

    private static String utf8(String item) {
        return StringUtils.toEncodedString(item.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
    }

    private static IdCardInfo getString(String header) {
        String[] values = header.split(",");

        String idCodeValue = getValue(getPiece(values, "serialNumber"));
        String idCodeString = idCodeValue.contains("-") ? idCodeValue.split("-")[1] : idCodeValue;
        String firstName = getValue(getPiece(values, "GN"));
        String surname = getValue(getPiece(values, "SN"));
        return new IdCardInfo(utf8(firstName), utf8(surname), utf8(idCodeString));
    }

    private static String getValue(String idCodePart) {
        return idCodePart.split("=")[1];
    }

    private static String getPiece(String[] values, String serialNumber) {
        return Stream.of(values).filter(s -> s.startsWith(serialNumber)).findAny().orElseThrow(RuntimeException::new);
    }
}
