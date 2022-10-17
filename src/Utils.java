import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Utils {
    public static String readFile(File file) throws IOException {
        String nextLine;
        StringBuilder res = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new FileReader(file))) {
            while ((nextLine = in.readLine()) != null)
                res.append(nextLine).append("\n");
        }
        return res.toString();
    }
}
