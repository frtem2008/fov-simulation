import java.io.IOException;
import java.nio.charset.StandardCharsets;

//Class to handle all the logic of messages, that a peer sends
public class PeerMessageSender {
    public boolean sendHandshake(TCPPhone toSend, Peer sender) {
        Bitfield hBits = new Bitfield(32);

        hBits.setBytes(0, 17, "P2PFILESHARINGPROJ".getBytes(StandardCharsets.UTF_8));
        hBits.setBytes(18, 27, (byte) 0);
        hBits.setBytes(28, 31, Utils.byteArrFromInt(sender.getPeerID()));
        Message handshake = new Message(MessageType.HANDSHAKE, hBits.getBytes());

        try {
            sendMessage(handshake, toSend);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void sendMessage(Message msg, TCPPhone toSend) throws IOException {
        if (msg.getType() == MessageType.INVALID)
            throw new RuntimeException("Cannot send a message with invalid type!");
        System.out.println("Sending message to " + toSend);
        toSend.writeObject(msg);
        System.out.println("Message sent to " + toSend);
    }
}
