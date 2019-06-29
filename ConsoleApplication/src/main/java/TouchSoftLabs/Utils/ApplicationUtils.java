package TouchSoftLabs.Utils;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ApplicationUtils {
    public static String convertThrowableToString(Throwable throwable) {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw, true);
        throwable.printStackTrace(pw);
        return sw.getBuffer().toString();
    }
}
