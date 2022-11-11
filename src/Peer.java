import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class Peer implements Protocol {
    private final int peerID;
    private final String hostName;
    private final int peerPort;
    private final boolean hasFile;
    private final PeerMessageSender peerMessageSender;
    private final ArrayList<Message> messages;

    private final ArrayList<Bitfield> neighbourFields = new ArrayList<>();
    private final Bitfield bitfield = new Bitfield(new byte[]{});
    private final ArrayList<TCPPhone> unchokedPeers = new ArrayList<>();
    private final ArrayList<Peer> peers;
    private int numberOfPrefNeighbour;
    private int unchokingInterval;
    private int optUnchokingInterval;
    private String fileName;
    private long fileSize;
    private long pieceSize;
    private File peerDirectory;

    public Peer(int peerID, String hostName, int peerPort, boolean hasFile, Config config, ArrayList<Peer> peers) {
        this.peerID = peerID;
        this.hostName = hostName;
        this.peerPort = peerPort;
        this.hasFile = hasFile;

        messages = new ArrayList<>();
        peerMessageSender = new PeerMessageSender();
        this.peers = peers;

        setConfig(config);
        createFiles();
        setupSocket();
    }

    public int getPeerID() {
        return peerID;
    }

    private void createFiles() {
        peerDirectory = new File("peer_" + peerID);
        System.out.println("[peer" + peerID + "] " + "Peer " + peerID + " directory " + (peerDirectory.delete() ? "deleted" : "not deleted"));

        if (!peerDirectory.mkdir())
            throw new RuntimeException("Unable to create a directory for a peer: " + peerDirectory.getAbsolutePath());
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

    private void setupSocket() {
        //setting up a startCommunication on peer host
        try {
            startCommunication();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to start a server");
        }
    }

    private void startCommunication() throws IOException {
        System.out.println("[peer" + peerID + "] " + "Starting a server on port: " + peerPort);
        ServerSocket server = new ServerSocket(peerPort);
        System.out.println("[peer" + peerID + "] " + "Server started on port: " + peerPort);

        for (int i = 0; i < peers.size(); i++) {
            startPeerThread(peers.get(i));
        }

        new Thread(() -> startServerThread(server)).start();
    }

    private void startServerThread(ServerSocket server) {
        //peers choke-unchoke thread
        new Thread(() -> {
            while (true) {
                //calculate download rate from each in peers, or if hasACompleteFile, randomly
                //choose numberOfPrefNeighbour peers to unchoke
                //choose the best unchoked neighbour
                //adding all them to a list
                //unchokedPeers.addAll(getBestDownloadingRatePeers());
                //send "unchoke" message to each of them, except peers, which are already unchoked
                //receiving a request message from all
                //updating bitfields
                //send "choke" message to all other peers, except the optimistically unchoked neighbour

                //Unchoking interval sleep
                try {
                    Thread.sleep(unchokingInterval * 1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        //wait for peers to connect in an infinite loop 
        while (true) {
            TCPPhone client = new TCPPhone(server);
            new Thread(() -> {
                handleMessages(client, null);
            }).start();
        }
    }


    private boolean checkHandshake(byte[] bytes, Peer peer) {
        int receivedId;
        String header = new String(Utils.getBytes(bytes, 0, 17));
        //handshake checks: right neighbour + peer id + right handshake header
        if (header.equals("P2PFILESHARINGPROJ")) {
            receivedId = Utils.intFromByteArr(Utils.getBytes(bytes, 28, 31));
            return peer == null || receivedId == peer.peerID;
        }
        return false;
    }

    private boolean handshake(TCPPhone client, Peer toHandshake) {
        boolean handshaked;
        handshaked = peerMessageSender.sendHandshake(client, this);
        if (!handshaked)
            throw new RuntimeException("Failed to send a handshake message to: " + client);
        //wait for answer
        byte[] receivedHandshake = new byte[32];
        System.out.println("[peer" + peerID + "] " + "Waiting for a message from a peer: " + client.getIp());
        try {
            if (client.readBytes(receivedHandshake) != 32)
                throw new RuntimeException("[peer" + peerID + "] " + "Failed to receive a handshake message from: " + client.getIp() + " received not 32 bytes");
        } catch (IOException e) {
            throw new RuntimeException("[peer" + peerID + "] " + "Failed to receive a handshake message from: " + client.getIp());
        }
        System.out.println("[peer" + peerID + "] " + "Received a message from peer: " + client.getIp());
        //handshake checks
        handshaked = checkHandshake(receivedHandshake, toHandshake);
        return handshaked;
    }

    private void handleMessages(TCPPhone client, Peer peer) {
        boolean handshaked, interested = false, unchoked = false;
        Message mess = new Message(MessageType.INVALID, new byte[]{});

        if (peer == null)
            System.out.println("[peer" + peerID + "] " + "peer connected: " + client.getIp());
        else
            System.out.println("[peer" + peerID + "] " + "Connected to the peer: " + peer.hostName + ":" + peer.peerPort);
        //handshake
        handshaked = handshake(client, peer);
        System.out.println("[peer" + peerID + "] " + "handshaked = " + handshaked + " with peer " + client.getIp());
        if (handshaked) {
            if (hasFile) {
                //bitfield
                //wait for answer
                //if no bitfield, than setBitfield 0, else - a received bitfield
                //if bitFieldCheck() finds out B has the peaces we need
                //interested = true
                if (interested) {
                    //send interested message
                } else {
                    //send not-interested message
                }
            }

            //now, we know the connection is established and we can start
            //infinite messaging loop
            while (true) {
                //get a message
                //mess = getMessage();
                switch (mess.getType()) {
                    case CHOKE -> {
                        // no more pieces would be received ===> start checking messages until unchoking
                    }
                    case UNCHOKE -> {
                        //send a request message, using a client bitfield and our own bitfield
                    }
                    case INTERESTED -> {
                        //remember, that this peer is interested in peaces, we have
                    }
                    case NOT_INTERESTED -> {
                        //remember, that this peer is not interested in peaces, we have
                    }
                    case HAVE -> {
                        //check, if peer needs any pieces from the have message
                        //send interested or not interested
                    }
                    case BITFIELD, INVALID -> {
                        //INCORRECT CASE, SHOULDN'T BE RECEIVED AFTER HANDSHAKING
                    }
                    case REQUEST -> {
                        if (unchoked) {
                            //send a peace message, corresponding to the request
                        } else {
                            //SOMETHING STRANGE (REQUEST MESSAGES SHOULD BE SENT ONLY IF UNCHOKED)
                            throw new IllegalStateException("Received a request message, but not unchoked yet");
                        }
                    }
                    case PIECE -> {
                        //checks if it is a requested peace, and if the peer had actually requested any peaces
                        //download a peace from a message
                    }
                    default -> throw new IllegalArgumentException("WRONG MESSAGE TYPE RECEIVED: " + mess.getType());
                }
            }
        } else {
            //leaving client working thread
            throw new RuntimeException("Handshake failed");
        }
    }

    private void startPeerThread(Peer peer) {
        //connecting to a peer
        TCPPhone client = new TCPPhone(peer.hostName, peer.peerPort);
        new Thread(() -> {
            handleMessages(client, peer);
        }).start();
    }

    private void setConfig(Config config) {
        numberOfPrefNeighbour = config.getNumberOfNeighbours();
        unchokingInterval = config.getUnchokingInterval();
        fileName = config.getFileName();
        fileSize = config.getFileSize();
        pieceSize = config.getPieceSize();

        if (hasFile)
            bitfield.setAll((byte) 1);
        else
            bitfield.setAll((byte) 0);
    }
}
