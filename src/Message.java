import java.io.Serial;
import java.io.Serializable;

public class Message implements Serializable {
    @Serial
    private static final long serialVersionUID  = 1L;

    private int length;
    private final MessageType type;

    private final Bitfield bits;

    public Message(MessageType type, byte[] bits) {
        this.type = type;
        this.bits = new Bitfield(bits);
    }

    public int getLength() {
        return length;
    }

    public Bitfield getBits() {
        return bits;
    }

    public MessageType getType() {
        return type;
    }
}
