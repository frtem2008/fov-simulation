/* Entry point for the program */

public class Main {
    public static PeerProcess process;
    //for proper peer start(in multiple piers case may cause problems, that they try to communicate, when others haven't started yet)
    public static boolean allPeersStarted = false;

    public static void main(String[] args) {
        //getting configs
        Config config = new Config("src/Common.cfg");
        System.out.println("[config]config = " + config + "\n");
        //stating all peers
        process = new PeerProcess(config);
        System.out.println("process = " + process);
        System.out.println("process.getPeers() = " + process.getPeers());
    }
}
