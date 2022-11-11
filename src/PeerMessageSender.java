import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

//Class to handle all the logic of messages, that a peer sends
public class PeerMessageSender {
    public boolean sendHandshake(TCPPhone toSend, Peer sender) {
        Bitfield hBits = new Bitfield(32);

        hBits.setBytes(0, 17, "P2PFILESHARINGPROJ".getBytes(StandardCharsets.UTF_8));
        hBits.setBytes(18, 27, (byte) 0);
        hBits.setBytes(28, 31, Utils.byteArrFromInt(sender.getPeerID()));
        byte[] sendBytes = hBits.getBytes();
        try {
            System.out.println("sendBytes = " + Arrays.toString(sendBytes));
            System.out.println("Sending message to " + toSend);
            toSend.writeBytes(sendBytes);
            System.out.println("Message sent to " + toSend);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void sendMessage(Message msg, TCPPhone toSend) throws IOException {
        if (msg.getType() == MessageType.INVALID)
            throw new RuntimeException("Cannot send a message with invalid type!");

        byte[] sendBytes = new byte[4 + 1 + msg.getBits().getSize()];
        sendBytes[4] = msg.getType().numberValue;
        Utils.setBytes(sendBytes, 0, 3, Utils.byteArrFromInt(sendBytes.length - 6));
        Utils.setBytes(sendBytes, 5, sendBytes.length - 6, msg.getBits().getBytes());

        System.out.println("sendBytes = " + Arrays.toString(sendBytes));
        System.out.println("Sending message to " + toSend);
        toSend.writeBytes(sendBytes);
        System.out.println("Message sent to " + toSend);
    }
}

