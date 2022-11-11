import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

public class Utils {
    public static byte[] getBytes(byte[] bytes, int begin, int end) {
        if (begin < 0 || end >= bytes.length || end <= begin)
            throw new IllegalArgumentException("Can't get bytes from " + begin + " to " + end);
        byte[] res = new byte[end - begin + 1];
        System.arraycopy(bytes, begin, res, 0, end - begin + 1);
        return res;
    }

    public static byte[] getEmptyByteArr(int len) {
        if (len < 0)
            throw new IllegalArgumentException("Length can't be less than zero, length is: " + len);

        if (len == 0)
            return new byte[0];

        byte[] res = new byte[len];
        Utils.setBytes(res, 0, len - 1, (byte) 0);

        return res;
    }

    public static void setAll(byte[] bytes, byte value) {
        Arrays.fill(bytes, value);
    }

    public static void setBytes(byte[] bytes, int begin, int end, byte value) {
        if (begin < 0 || end >= bytes.length || end <= begin)
            throw new IllegalArgumentException("Can't set bytes from " + begin + " to " + end + " (bytes.length is " + bytes.length + ")");

        for (int i = begin; i <= end; i++)
            bytes[i] = value;
    }

    public static void setBytes(byte[] bytes, int begin, int end, byte[] setBytes) {
        System.out.println("bytes.length = " + bytes.length);
        System.out.println("setBytes.length = " + setBytes.length);
        System.out.println("end - begin + 1 = " + (end - begin + 1));

        if (begin < 0 || end >= bytes.length || end <= begin) {
            throw new IllegalArgumentException("Can't set bytes from " + begin + " to " + end);
        }
        //similar to memcpy for arrays
        if (end + 1 - begin >= 0) {
            System.arraycopy(setBytes, 0, bytes, begin, end + 1 - begin);
        }
    }

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
