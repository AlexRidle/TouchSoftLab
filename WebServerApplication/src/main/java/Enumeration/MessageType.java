package Enumeration;

public enum MessageType {
    LEAVE_MESSAGE,
    RAW_MESSAGE;

    public static MessageType getMessageType(String message) {
        if (message.equalsIgnoreCase("/leave")) {
            return MessageType.LEAVE_MESSAGE;
        } else {
            return MessageType.RAW_MESSAGE;
        }
    }
}
