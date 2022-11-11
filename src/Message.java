import java.io.IOException;
import java.util.Arrays;

public class Message {
    private Bitfield bits;
    private MessageType type;
    private int length;

    public Message(MessageType type, byte[] bits) {
        this.type = type;
        this.bits = new Bitfield(bits);
    }

    public Message() {
    }

    public int getLength() {
        return length;
    }

    public Bitfield getBits() {
        return bits;
    }

    public void setBits(Bitfield toSet) {
        bits = toSet;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public void send(TCPPhone to) throws IOException {
        length = bits.getSize() + 1;
        byte[] bytes = new byte[length + 4];
        System.arraycopy(Utils.byteArrFromInt(length), 0, bytes, 0, 4);
        bytes[4] = type.numberValue;
        System.arraycopy(bits.getBytes(), 0, bytes, 5, bits.getSize());
        //System.out.println("Actual message: " + Arrays.toString(bytes));
        to.writeBytes(bytes);
    }

    public void receive(TCPPhone from) throws IOException {
        byte[] bytes = new byte[65535];
        byte[] actualBytes;
        from.readBytes(bytes);
        length = Utils.intFromByteArr(
                Utils.getBytes(bytes, 0, 3)
        );
        type = MessageType.getByValue(bytes[4]);
        actualBytes = new byte[length]; //message bytes without length field
        System.arraycopy(bytes, 4, actualBytes, 0, actualBytes.length);
        if (length != 0 && length != 1)
            bits = new Bitfield(Utils.getBytes(actualBytes, 1, actualBytes.length - 1));
        else
            bits = new Bitfield(0);
    }
}
