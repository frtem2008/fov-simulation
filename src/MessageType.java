public enum MessageType {
    INVALID(-1),
    HANDSHAKE(100),
    CHOKE(0),
    UNCHOKE(1),
    INTERESTED(2),
    NOT_INTERESTED(3),
    HAVE(4),
    BITFIELD(5),
    REQUEST(6),
    PIECE(7);
    int numberValue;

    MessageType(int numberValue) {
        this.numberValue = numberValue;
    }
}