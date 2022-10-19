import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Bitfield implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final List<Byte> bytes;

    public Bitfield(int size) {
        bytes = new ArrayList<>();
        Utils.addValueMultiTimes(bytes, (byte) 0, size);
    }
    public int getSize() {
        return bytes.size();
    }

    public Bitfield(byte[] byteBits) {
        bytes = new ArrayList<>();
        for (int i = 0; i < byteBits.length; i++)
            bytes.add(byteBits[i]);

    }

    public byte[] getBytes() {
        byte[] res = new byte[bytes.size()];
        for (int i = 0; i < res.length; i++)
            res[i] = bytes.get(i);
        return res;
    }


    public byte[] getBytes(int begin, int end) {
        if (begin < 0 || end >= bytes.size() || end <= begin)
            throw new IllegalArgumentException("Can't get bytes from " + begin + " to " + end);
        byte[] res = new byte[end - begin + 1];
        for (int i = 0; i <= end - begin; i++)
            res[i] = bytes.get(i + begin);
        return res;
    }

    public void setAll(byte value) {
        for (int i = 0; i < bytes.size(); i++) {
            bytes.set(i, value);
        }
    }

    public void setBytes(int begin, int end, byte value) {
        if (begin < 0 || end >= bytes.size() || end <= begin)
            throw new IllegalArgumentException("Can't set bytes from " + begin + " to " + end);

        for (int i = begin; i <= end; i++)
            bytes.set(i, value);
    }

    public void setBytes(int begin, int end, byte[] setBits) {
        if (begin < 0 || end >= bytes.size() || end <= begin || setBits.length != end - begin + 1)
            throw new IllegalArgumentException("Can't set bytes from " + begin + " to " + end);

        for (int i = begin; i <= end; i++)
            bytes.set(i, setBits[i - begin]);
    }

    public Byte getByte(int position) {
        if (bytes.size() <= position)
            return null;
        return bytes.get(position);
    }
}
