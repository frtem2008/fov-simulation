import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;

public class Message implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private Bitfield bits;
    private MessageType type;
    private int length;

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

    public Message() {
    }

    public void receive(TCPPhone from) throws IOException {
        byte[] bytes = new byte[65526];
        byte[] actualBytes;
        from.readBytes(bytes);
        length = Utils.intFromByteArr(Utils.getBytes(bytes, 0, 3));
        actualBytes = new byte[length];
        System.arraycopy(bytes, 4, actualBytes, 0, actualBytes.length);
        type = MessageType.getByValue(Utils.intFromByteArr(Utils.getBytes(actualBytes, 0, 3)));
        bits = new Bitfield(Utils.getBytes(actualBytes, 1, actualBytes.length - 1));
    }
}
