/* A class to represent peers and their communication */

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Arrays;

public class Peer implements Protocol {
    private final int peerID;
    private final String hostName;
    private final int peerPort;
    private final boolean hasFile;
    private final PeerMessageSender peerMessageSender; //a message sender for cleaner code
    private final ArrayList<Message> messages; //a list of messages for later

    private final ArrayList<Bitfield> neighbourFields = new ArrayList<>(); //other peer's bitfields
    private final ArrayList<TCPPhone> unchokedPeers = new ArrayList<>(); //a list of unchoked peers
    private final ArrayList<Peer> peers; //all peers
    private final Bitfield fileBitfield;
    private int numberOfPrefNeighbour;
    private int unchokingInterval;
    private int optUnchokingInterval;

    private String fileName;
    private long fileSize;
    private long pieceSize;
    private File peerDirectory;

    //creates a peer with given properties
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
        fileBitfield = new Bitfield(PeerProcess.totalPieces); //a bitfield with a bit for each piece

        new Thread(this::setupSocket).start(); //a separate thread for waiting for all peers to setup
    }

    public int getPeerID() {
        return peerID;
    }

    //create all needed peer files before communication
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

    //setting up a startCommunication on peer host
    private void setupSocket() {
        try {
            startCommunication();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to start a peer server");
        }
    }

    //starts peer-to-peer communication
    private void startCommunication() throws IOException {
        System.out.println("[peer" + peerID + "] " + "Starting a server on port: " + peerPort);
        ServerSocket server = new ServerSocket(peerPort);
        System.out.println("[peer" + peerID + "] " + "Server started on port: " + peerPort);

        //waiting until all peers have started (because of sync issues)
        while (!Main.allPeersStarted) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //start a separate thread and make a connection with other peers
        for (Peer peer : peers) {
            if (peer.peerID != peerID) {
                System.out.println("[peer" + peerID + "] " + "CONNECTING TO " + peer.peerID);
                startPeerThread(peer);
            }
        }
        //separate thread for choking-unchoking etc
        new Thread(() -> startServerThread(server)).start();
    }

    //peers choke-unchoke thread
    private void startServerThread(ServerSocket server) {
        new Thread(() -> {
            while (true) {
                //TODO
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

    //check if a handshake message from a peer is correct
    private boolean checkHandshake(byte[] bytes, Peer peer) {
        int receivedId;
        boolean handshaked = false;

        String header = new String(Utils.getBytes(bytes, 0, 17)); //getting peer message header
        //handshake checks: right neighbour + peer id + right handshake header
        if (header.equals("P2PFILESHARINGPROJ")) {
            receivedId = Utils.intFromByteArr(Utils.getBytes(bytes, 28, 31));
            handshaked = peer == null || receivedId == peer.peerID;
            System.out.println("[peer" + peerID + "] " + "handshaked = " + handshaked + " with peer " + receivedId);
            handshaked = true;
        }
        return handshaked;
    }

    //actual handshake method
    private boolean handshake(TCPPhone client, Peer toHandshake) {
        boolean handshaked;

        handshaked = peerMessageSender.sendHandshake(client, this);
        if (!handshaked)
            throw new RuntimeException("Failed to send a handshake message to: " + client);
        //wait for answer
        byte[] receivedHandshake = new byte[32];
        //System.out.println("[peer" + peerID + "] " + "Waiting for a handshake from a peer: " + client.getIp());
        try {
            if (client.readBytes(receivedHandshake) != 32)
                throw new RuntimeException("[peer" + peerID + "] " + "Failed to receive a handshake message from: " + client.getIp() + " received not 32 bytes");
        } catch (IOException e) {
            throw new RuntimeException("[peer" + peerID + "] " + "Failed to receive a handshake message from: " + client.getIp());
        }
        //System.out.println("[peer" + peerID + "] " + "Received a handshake from peer: " + client.getIp());
        //handshake checks
        handshaked = checkHandshake(receivedHandshake, toHandshake);

        return handshaked;
    }

    //sending a bitfield message to client
    private void sendBitfield(TCPPhone client) {
        byte[] bits = new byte[Main.process.getTotalPieces()]; //one bit corresponds to a piece in a file
        Message bitfieldMessage = new Message(); //actual message to send

        if (hasFile)
            Arrays.fill(bits, (byte) 1);
        else
            bits = fileBitfield.getBytes();

        //setup message properties
        bitfieldMessage.setBits(new Bitfield(bits));
        bitfieldMessage.setType(MessageType.BITFIELD);

        try {
            bitfieldMessage.send(client); //sending a bitfield message
            System.out.println("[peer" + peerID + "] " + "sending bitfield message to " + client + " done");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("[peer" + peerID + "] " + "sending bitfield message to " + client + " failed");
        }
    }

    //check if bitfields differ in an interesting way
    private boolean BitfieldsDiffer(Bitfield toCompare) {
        System.out.println("[peer" + peerID + "] " + "Comparing bitfields");
        if (toCompare.getSize() != fileBitfield.getSize()) {
            System.out.println("[peer" + peerID + "] " + "Bitfields lengths are different");
            return true; //wrong input to peer
        }
        for (int i = 0; i < fileBitfield.getSize(); i++) {
            if (fileBitfield.getByte(i).equals((byte) 0) && toCompare.getByte(i).equals((byte) 1)) {
                System.out.println("[peer" + peerID + "] " + "Bitfields don't match");
                return true; //interested
            }
        }

        System.out.println("[peer" + peerID + "] " + "Bitfields match");
        return false; //not interested
    }

    //sending an interested message to client
    private void sendInterested(TCPPhone client, boolean interested) throws IOException {
        System.out.println("[peer" + peerID + "] " + "Sending interest message...");
        Message toSend = new Message(interested ? MessageType.INTERESTED : MessageType.NOT_INTERESTED, Utils.getEmptyByteArr(0));
        toSend.send(client);
        System.out.println("[peer" + peerID + "] " + "Sending interest message done");
    }

    //a main method for peer communication
    private void handleMessages(TCPPhone client, Peer peer) {
        boolean 
                handshaked, //whether 2 peers had successfully handshaked 
                interested = false, //whether a peer is interested in the other peer's pieces
                hasBitfield, //whether a peer has received a bitfield message
                unchoked = false, //whether is a peer chocked or unchoked
                flag; //temporary variable for future usage
        Message mess = new Message(MessageType.INVALID, new byte[]{}); //a message object to be sent
        Bitfield receivedBitfield = new Bitfield(fileBitfield.getSize()); // should be the same size for each peer
        
        System.out.println();
        if (peer == null)
            System.out.println("[peer" + peerID + "] " + "peer connected: " + client.getIp()); //peer connected to us
        else
            System.out.println("[peer" + peerID + "] " + "Connected to the peer: " + peer.hostName + ":" + peer.peerPort); //we connected to a peer
        //handshake
        handshaked = handshake(client, peer); 
        System.out.println();

        if (handshaked) {
            //bitfield
            if (hasFile) {
                fileBitfield.setAll((byte) 1);
                sendBitfield(client);
                //wait for interested or not interested message
                try {
                    mess.receive(client);
                    if (!mess.getType().equals(MessageType.INTERESTED) && !mess.getType().equals(MessageType.NOT_INTERESTED)) {
                        System.out.println("[peer" + peerID + "] " + "Failed to receive an interest message from " + client + ", actual message type was " + mess.getType());
                        //if we get a bitfield as an answer, it means each of 2 peers has some file pieces
                        if (mess.getType().equals(MessageType.BITFIELD)) {
                            //send interest message
                            receivedBitfield.setBytes(0, fileBitfield.getSize() - 1, mess.getBits().getBytes());
                            try {
                                if (BitfieldsDiffer(receivedBitfield))
                                    interested = true;
                                sendInterested(client, interested);
                            } catch (IOException e) {
                                System.out.println("[peer" + peerID + "] " + "Sending interested" + " message to " + client + " failed: ");
                                throw new RuntimeException(e);
                            }
                            //get answer to your own bitfield message
                            mess.receive(client);
                            if (!mess.getType().equals(MessageType.INTERESTED) && !mess.getType().equals(MessageType.NOT_INTERESTED))
                                System.out.println("[peer" + peerID + "] " + "Failed to receive an interest message from " + client + ", actual message type was " + mess.getType());
                            //TODO Actually add them as interested and not interested clients
                            if (mess.getType() == MessageType.INTERESTED)
                                System.out.println("[peer" + peerID + "] " + "Client: " + client + " is interested");
                            else
                                System.out.println("[peer" + peerID + "] " + "Client: " + client + " is not interested");
                        }
                    } else {
                        //TODO Actually add them as interested and not interested clients
                        if (mess.getType() == MessageType.INTERESTED)
                            System.out.println("[peer" + peerID + "] " + "Client: " + client + " is interested");
                        else
                            System.out.println("[peer" + peerID + "] " + "Client: " + client + " is not interested");
                    }
                } catch (IOException e) {
                    System.out.println("[peer" + peerID + "] " + "Failed to receive an interest message from client: " + client);
                    throw new RuntimeException(e);
                }
            } else {
                try {
                    //we don't have any pieces
                    fileBitfield.setAll((byte) 0);
                    mess.receive(client); //getting a message (can be bitfield or just nothing)
                    
                    if (mess.getType().equals(MessageType.BITFIELD)) {
                        System.out.println("[peer" + peerID + "] " + "Got a bitfield:" + Arrays.toString(mess.getBits().getBytes()) + " from " + client);
                        receivedBitfield.setBytes(0, fileBitfield.getSize() - 1, mess.getBits().getBytes());
                        try {
                            if (BitfieldsDiffer(receivedBitfield))
                                interested = true;
                            sendInterested(client, interested);
                        } catch (IOException e) {
                            System.out.println("[peer" + peerID + "] " + "Sending interested" + " message to " + client + " failed: ");
                            throw new RuntimeException(e);
                        }
                    } else {
                        System.out.println("[peer" + peerID + "] " + "Peer " + (peer != null ? peer.peerID : "null peer") + " has nothing to exchange");
                        sendInterested(client, false);
                    }
                } catch (IOException e) {
                    System.out.println("[peer" + peerID + "] " + "Failed to receive a bitfield message from " + client);
                    throw new RuntimeException(e);
                }
            }
            
            //now, we know the connection is established and we can start
            //infinite messaging loop
            System.out.println("[peer" + peerID + "] " + "Entered an infinite messaging loop");

            while (true) {
                //get a message
                try {
                    mess.receive(client);
                } catch (IOException e) {
                    System.out.println("Failed to receive a message from peer" + (peer != null ? peer.peerID : "null peer") + " client: " + client);
                    throw new RuntimeException(e);
                }
                
                switch (mess.getType()) {
                    case CHOKE -> {
                        // no more pieces would be received ===> start checking messages until unchoking
                    }
                    case UNCHOKE -> {
                        //send a request message, using a client bitfield and our own bitfield
                    }
                    case INTERESTED -> {
                        System.out.println("[peer" + peerID + "] " + "Client: " + client + " is interested");
                        //remember, that this peer is interested in pieces that we have
                    }
                    case NOT_INTERESTED -> {
                        System.out.println("[peer" + peerID + "] " + "Client: " + client + " is not interested");
                        //remember, that this peer is not interested in pieces that we have
                    }
                    case HAVE -> {
                        //check, if peer needs any pieces from the have message
                        //send interested or not interested message
                    }
                    case BITFIELD -> {
                        //case, when other peer didn't have anything, just ignore
                        System.out.println("[peer" + peerID + "] " + "Received an empty bitfield from " + (peer != null ? peer.peerID : "null peer"));
                    }
                    case REQUEST -> {
                        if (unchoked) {
                            //send a piece message, corresponding to the request
                        } else {
                            //SOMETHING STRANGE (REQUEST MESSAGES SHOULD BE SENT ONLY IF UNCHOKED)
                            throw new IllegalStateException("Received a request message, but not unchoked yet");
                        }
                    }
                    case PIECE -> {
                        //checks if it is a requested piece, and if the peer had actually requested any pieces
                        //download a piece from a message
                    }
                    case INVALID -> {
                        //INCORRECT CASE, SHOULDN'T BE RECEIVED AFTER HANDSHAKING
                    }
                    default -> throw new IllegalArgumentException("WRONG MESSAGE TYPE RECEIVED: " + mess.getType());
                }
            }
        } else {
            //leaving client working thread
            throw new RuntimeException("Handshake failed");
        }
    }

    //connecting to a peer
    private void startPeerThread(Peer peer) {
        TCPPhone client = new TCPPhone(peer.hostName, peer.peerPort);
        new Thread(() -> {
            handleMessages(client, peer);
        }).start();
    }

    //setting private variables from a config
    private void setConfig(Config config) {
        numberOfPrefNeighbour = config.getNumberOfNeighbours();
        unchokingInterval = config.getUnchokingInterval();
        fileName = config.getFileName();
        fileSize = config.getFileSize();
        pieceSize = config.getPieceSize();
    }
}
