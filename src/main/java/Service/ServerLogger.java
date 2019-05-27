package Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerLogger {

    private static Logger LOGGER = LoggerFactory.getLogger(ServerLogger.class);

    public static void logInfo(String line) {
        LOGGER.info(line);
    }

    public static void logWarn(String line) {
        LOGGER.warn(line);
    }

    public static void logError(String line) {
        LOGGER.error(line);
    }

}
