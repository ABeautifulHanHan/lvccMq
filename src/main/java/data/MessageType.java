package data;

import java.util.Optional;

/**
 * @author lvcc
 */
public enum MessageType {

    REGISTER_TOPIC(1, "register_topic"),

    PUBLISH_MESSAGE(2, "publish_message"),

    REPLY_EXPECTED(3, "reply_expect"),

    PULL_MESSAGE(4, "pull_message"),

    REQUEST_QUEUE(5, "request_queue")
    ;

    private int value;

    private String name;

    MessageType(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public static MessageType findByInt(int value) throws IllegalArgumentException {
        for (MessageType item : MessageType.values()) {
            if (item.value == value) {
                return item;
            }
        }

        throw new IllegalArgumentException("Invalid MessageType value: " + value);
    }

    public static Optional<MessageType> findByString(String name) throws IllegalArgumentException {
        for (MessageType item : MessageType.values()) {
            if (item.name.equals(name)) {
                return Optional.of(item);
            }
        }

        return Optional.empty();
    }

    @Override
    public String toString() {
        return this.name;
    }

    public int toInt() {
        return this.value;
    }
}
