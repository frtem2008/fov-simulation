import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

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

    public static byte[] byteArrFromInt(int a) {
        //byte order is big endian by default
        ByteBuffer b = ByteBuffer.allocate(4);
        b.putInt(a);
        return b.array();
    }

    public static int intFromByteArr(byte[] bytes) {
        //byte order is big endian by default
        ByteBuffer b = ByteBuffer.wrap(bytes);
        return b.getInt();
    }

    public static void addValueMultiTimes(List<Byte> a, byte value, int count) {
        if (count < 0)
            throw new IllegalArgumentException("Unable to fill a list with " + count + " elements");
        for (int i = 0; i < count; i++) {
            a.add(value);
        }
    }
}
