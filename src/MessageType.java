public enum MessageType {
    INVALID(-1),
    CHOKE(1),
    UNCHOKE(2),
    INTERESTED(3),
    NOT_INTERESTED(4),
    HAVE(5),
    BITFIELD(6),
    REQUEST(7),
    PIECE(8);

    int numberValue;

    MessageType(int numberValue) {
        this.numberValue = numberValue;
    }
}