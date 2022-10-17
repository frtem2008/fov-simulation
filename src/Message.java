public class Message {
    final int MESSAGE_LENGTH = 4;
    private final MessageType type;

    public MessageType getType() {
        return type;
    }

    public Message(MessageType type) {
        this.type = type;
    }

}
