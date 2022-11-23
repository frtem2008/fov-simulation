/* Enum to represent message type with */

public enum MessageType {
    INVALID((byte) -1),
    HANDSHAKE((byte) 100),
    CHOKE((byte) 0),
    UNCHOKE((byte) 1),
    INTERESTED((byte) 2),
    NOT_INTERESTED((byte) 3),
    HAVE((byte) 4),
    BITFIELD((byte) 5),
    REQUEST((byte) 6),
    PIECE((byte) 7);
    byte numberValue;

    MessageType(byte numberValue) {
        this.numberValue = numberValue;
    }

    static MessageType getByValue(int i) {
        for (MessageType e : values()) {
            if (e.numberValue == i) {
                return e;
            }
        }
        return null;
    }
}