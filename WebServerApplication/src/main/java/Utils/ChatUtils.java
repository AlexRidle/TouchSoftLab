package Utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ChatUtils {
    public static String getTimeStamp() {
        return String.format("[%s]", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")));
    }
}
