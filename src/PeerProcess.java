import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class PeerProcess {
    private static final String peerConfigFile = "src/PeerInfo.cfg";
    public static ArrayList<Peer> peers = new ArrayList<>();
    public static int totalPieces;
    private final long fileSize;
    private final long pieceSize;
    public String fileName;

    public PeerProcess(Config config) {
        fileName = config.getFileName();
        fileSize = config.getFileSize();
        pieceSize = config.getPieceSize();
        totalPieces = (int) Math.ceil((double) fileSize / pieceSize);

        try {
            startPeers(config);
        } catch (IOException e) {
            System.err.println("[peer process]Unable to start a peer process: ");
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public ArrayList<Peer> getPeers() {
        return peers;
    }

    public int getTotalPieces() {
        return totalPieces;
    }

    private void startPeers(Config config) throws IOException {
        String peerInfo = Utils.readFile(new File(peerConfigFile));
        String[] split = peerInfo.split("\n");
        int id, port, hasFile;
        String hostname;
        System.out.println("[peer process]Starting " + split.length + " peers");

        for (int i = 0; i < split.length; i++) {
            String[] peerCfg = split[i].split(" ");
            id = Integer.parseInt(peerCfg[0]);
            hostname = peerCfg[1];
            port = Integer.parseInt(peerCfg[2]);
            hasFile = Integer.parseInt(peerCfg[3]);
            System.out.println("\n[peer process]Generating a new peer... ");
            Peer cur = new Peer(id, hostname, port, hasFile == 1, config, peers);
            System.out.println("[peer process]Generated new peer: " + cur);
            peers.add(cur);
        }
        Main.allPeersStarted = true;
    }
}
