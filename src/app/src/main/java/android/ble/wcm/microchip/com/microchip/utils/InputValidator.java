package android.ble.wcm.microchip.com.microchip.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by: WillowTree
 * Date: 11/19/14
 * Time: 1:45 PM.
 */
public class InputValidator {

    private static final String VALID_IP_ADDRESS_REGEX = "^http(s)*://(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$";
    private static final String VALID_HOSTNAME_REGEX = "^http(s)*://(([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]*[a-zA-Z0-9])\\.)*([A-Za-z0-9]|[A-Za-z0-9][A-Za-z0-9\\-]*[A-Za-z0-9])$";

    public static boolean IsUrl(String s) {
        try {
            Pattern ipPattern = Pattern.compile(VALID_IP_ADDRESS_REGEX);
            Matcher ipMatcher = ipPattern.matcher(s);

            Pattern hostnamePatter = Pattern.compile(VALID_HOSTNAME_REGEX);
            Matcher hostnameMatcher = hostnamePatter.matcher(s);

            return ipMatcher.matches() || hostnameMatcher.matches();
        } catch (RuntimeException e) {
            return false;
        }

    }
}
