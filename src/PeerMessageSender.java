//Class to handle all the logic of messages, that a peer sends

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class PeerMessageSender {
    //send a handshake to toSend from a peer sender
    public boolean sendHandshake(TCPPhone toSend, Peer sender) {
        Bitfield hBits = new Bitfield(32); //32 bit size bitfield

        hBits.setBytes(0, 17, "P2PFILESHARINGPROJ".getBytes(StandardCharsets.UTF_8)); //handshake header
        hBits.setBytes(18, 27, (byte) 0); //zero bits
        hBits.setBytes(28, 31, Utils.byteArrFromInt(sender.getPeerID())); //peer id

        byte[] sendBytes = hBits.getBytes();
        try {
            toSend.writeBytes(sendBytes); //sending bytes
            return true; //sending successfully
        } catch (IOException e) {
            e.printStackTrace();
        }
        //sending failed
        return false;
    }

    //send any message msg to toSend
    public void sendMessage(Message msg, TCPPhone toSend) throws IOException {
        if (msg.getType() == MessageType.INVALID)
            throw new RuntimeException("Cannot send a message with invalid type!");

        //calculating total bytes size (length(4) + message type(1) + message payload)
        byte[] sendBytes = new byte[4 + 1 + msg.getBits().getSize()];

        sendBytes[4] = msg.getType().numberValue;
        Utils.setBytes(sendBytes, 0, 3, Utils.byteArrFromInt(sendBytes.length - 6));
        Utils.setBytes(sendBytes, 5, sendBytes.length - 6, msg.getBits().getBytes());

        toSend.writeBytes(sendBytes);
    }
}

