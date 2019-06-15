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
}
