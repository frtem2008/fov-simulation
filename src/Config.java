import java.io.File;
import java.io.IOException;

public class Config {
    //common configs
    private int numberOfNeighbours;
    private int unchokingInterval;
    private int optUnchokingInterval;
    private String fileName;
    private long fileSize;
    private long pieceSize;
    //config file text
    private String configFile;

    public Config(String configFile) {
        try {
            this.configFile = Utils.readFile(new File(configFile));
        } catch (IOException e) {
            System.err.println("[config]Unable to read a config file");
            e.printStackTrace();
            System.exit(-1);
        }

        parseConfig();
    }

    @Override
    public String toString() {
        return "Config{" +
                "numberOfNeighbours=" + numberOfNeighbours +
                ", unchokingInterval=" + unchokingInterval +
                ", optUnchokingInterval=" + optUnchokingInterval +
                ", fileSize=" + fileSize +
                ", fileName=" + fileName +
                ", pieceSize=" + pieceSize +
                '}';
    }

    public int getNumberOfNeighbours() {
        return numberOfNeighbours;
    }

    public int getUnchokingInterval() {
        return unchokingInterval;
    }

    public int getOptUnchokingInterval() {
        return optUnchokingInterval;
    }

    public long getFileSize() {
        return fileSize;
    }

    public long getPieceSize() {
        return pieceSize;
    }

    public String getFileName() {
        return fileName;
    }

    private void parseConfig() {
        int i = 0;
        configFile = configFile.replaceAll("\n", " ");
        String[] token = configFile.split(" ");
        for (String tok : token) {
            i++;
            if (i % 2 == 0) {
                switch (i) {
                    case 2 -> numberOfNeighbours = Integer.parseInt(tok);
                    case 4 -> unchokingInterval = Integer.parseInt(tok);
                    case 6 -> optUnchokingInterval = Integer.parseInt(tok);
                    case 8 -> fileName = tok;
                    case 10 -> fileSize = Long.parseLong(tok);
                    case 12 -> pieceSize = Long.parseLong(tok);
                    default -> throw new IllegalStateException("Unexpected value: " + i);
                }
            }
        }
    }
}
