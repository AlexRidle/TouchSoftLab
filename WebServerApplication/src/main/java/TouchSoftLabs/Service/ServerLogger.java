package TouchSoftLabs.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerLogger {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerLogger.class);

    public static void logInfo(final String line) {
        LOGGER.info(line);
    }

    public static void logWarn(final String line) {
        LOGGER.warn(line);
    }

    public static void logError(final String line) {
        LOGGER.error(line);
    }

}
