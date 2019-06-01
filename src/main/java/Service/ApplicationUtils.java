package Service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;

public class ApplicationUtils {
    public static String convertThrowableToString(Throwable throwable) {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw, true);
        throwable.printStackTrace(pw);
        return sw.getBuffer().toString();
    }

    public static HashMap<String, String> convertStringToHashMap(String input) {
        final HashMap<String, String> content = new HashMap<>();
        input = input.substring(1, input.length() - 1);
        final String[] pairs = input.split(", ");
        for (String pair : pairs) {
            final String[] keyValue = pair.split("=");
            content.put(keyValue[0], keyValue[1]);
        }
        return content;
    }
}
