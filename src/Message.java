public class Message {
    public final int MESSAGE_LENGTH = 4;
    private final MessageType type;

    public Message(MessageType type) {
        this.type = type;
    }

    public MessageType getType() {
        return type;
    }

}
