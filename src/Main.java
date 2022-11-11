public class Main {
    public static PeerProcess process;
    public static boolean allPeersStarted = false; //for proper peer start(in multiple piers case may cause problems, that they try to communicate, when others haven't started yet)

    public static void main(String[] args) {
        Config config = new Config("src/Common.cfg");
        System.out.println("[config]config = " + config + "\n");
        process = new PeerProcess(config);
        System.out.println("process = " + process);
        System.out.println("process.getPeers() = " + process.getPeers());
    }
}
