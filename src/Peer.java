import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class Peer implements Protocol {
    private final int peerID;
    private final String hostName;
    private final int peerPort;
    private final boolean hasFile;
    private final ArrayList<Message> messages;
    private int numberOfPrefNeighbour;
    private int unchokingInterval;
    private int optUnchokingInterval;
    private String fileName;
    private long fileSize;
    private long pieceSize;

    public Peer(int peerID, String hostName, int peerPort, boolean hasFile, Config config) {
        this.peerID = peerID;
        this.hostName = hostName;
        this.peerPort = peerPort;
        this.hasFile = hasFile;

        messages = new ArrayList<>();

        setConfig(config);
        setupSocket();
    }

    @Override
    public String toString() {
        return "Peer{" +
                "peerID=" + peerID +
                ", hostName='" + hostName + '\'' +
                ", peerPort=" + peerPort +
                ", hasFile=" + hasFile +
                '}';
    }

    //TODO
    public void setupSocket() {
        //setting up a startCommunication on peer host
        startCommunication();


    }

    public void startCommunication() throws IOException {
        ServerSocket server = new ServerSocket(peerPort);
        System.out.println("Server started on port: " + peerPort);

        while (true) {
            //wait for new peers to connect
            //TODO AUTO CONNECTION TO ALL THE PEERS FROM CONFIGS
            TCPPhone client = new TCPPhone(server);
            new Thread(() -> {
                boolean handshaked = false, interested = false;
                System.out.println("Client connected: " + client.getIp());
                //handshake
                //wait for answer
                //handshake checks: right neighbour + peer id + right handshake header
                if (handshaked) {
                    if (hasFile) {
                        //bitfield
                        //wait for answer
                        //if bitFieldCheck() finds out B has the peaces we need
                        //interested = true
                        if (interested) {
                            //send interested message
                        }

                    }
                } else {
                    //leaving client working thread
                    throw new RuntimeException("Handshake failed");
                }
                while (true) {

                }
            }).start();
        }
    }

    public void setConfig(Config config) {
        numberOfPrefNeighbour = config.getNumberOfNeighbours();
        unchokingInterval = config.getUnchokingInterval();
        fileName = config.getFileName();
        fileSize = config.getFileSize();
        pieceSize = config.getPieceSize();
    }


}
