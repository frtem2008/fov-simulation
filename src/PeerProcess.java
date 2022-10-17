import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class PeerProcess {
    private static final String peerConfigFile = "src/PeerInfo.cfg";
    private final long fileSize;
    private final long pieceSize;
    private final int totalPieces;
    public String fileName;
    public ArrayList<Peer> peers = new ArrayList<>();

    public PeerProcess(Config config) {
        fileName = config.getFileName();
        fileSize = config.getFileSize();
        pieceSize = config.getPieceSize();
        totalPieces = (int) Math.ceil((double) fileSize / pieceSize);

        try {
            startPeers(config);
        } catch (IOException e) {
            System.err.println("Unable to start a peer process: ");
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public ArrayList<Peer> getPeers() {
        return peers;
    }

    private void startPeers(Config config) throws IOException {
        String peerInfo = Utils.readFile(new File(peerConfigFile));
        String[] split = peerInfo.split("\n");
        int id, port, hasFile;
        String hostname;
        for (String s : split) {
            String[] peerCfg = s.split(" ");
            id = Integer.parseInt(peerCfg[0]);
            hostname = peerCfg[1];
            port = Integer.parseInt(peerCfg[2]);
            hasFile = Integer.parseInt(peerCfg[3]);
            Peer cur = new Peer(id, hostname, port, hasFile == 1, config);
            peers.add(cur);
        }
    }
}