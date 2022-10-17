public class Main {
    public static void main(String[] args) {
        Config config = new Config("src/Common.cfg");
        System.out.println("config = " + config);
        PeerProcess process = new PeerProcess(config);
        System.out.println("process = " + process);
        System.out.println("process.getPeers() = " + process.getPeers());
    }
}
