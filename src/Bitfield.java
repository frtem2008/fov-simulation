/* A class to represent bitfields - byte arrays */

import java.util.ArrayList;
import java.util.List;

public class Bitfield {
    private final List<Byte> bytes; //list of all bytes in a bitfield

    //creates an empty bitfield of size size
    public Bitfield(int size) {
        bytes = new ArrayList<>();
        Utils.addValueMultiTimes(bytes, (byte) 0, size); //set all bytes to zero
    }

    //creates a bitfield from an existing byte array
    public Bitfield(byte[] byteBits) {
        bytes = new ArrayList<>();
        for (byte byteBit : byteBits) bytes.add(byteBit);
    }

    //size of a bitfield
    public int getSize() {
        return bytes.size();
    }

    //get all bits
    public byte[] getBytes() {
        byte[] res = new byte[bytes.size()];
        for (int i = 0; i < res.length; i++)
            res[i] = bytes.get(i);
        return res;
    }

    //get all bits from begin index to end index
    public byte[] getBytes(int begin, int end) {
        if (begin < 0 || end >= bytes.size() || end <= begin)
            throw new IllegalArgumentException("Can't get bytes from " + begin + " to " + end);
        byte[] res = new byte[end - begin + 1];
        for (int i = 0; i <= end - begin; i++)
            res[i] = bytes.get(i + begin);
        return res;
    }

    //set all bits to value
    public void setAll(byte value) {
        for (int i = 0; i < bytes.size(); i++) {
            bytes.set(i, value);
        }
    }

    //set all bits from begin index to end index to value
    public void setBytes(int begin, int end, byte value) {
        if (begin < 0 || end >= bytes.size() || end <= begin)
            throw new IllegalArgumentException("Can't set bytes from " + begin + " to " + end);

        for (int i = begin; i <= end; i++)
            bytes.set(i, value);
    }

    //set bytes from begin index to end index to setBits (array copying to sublist?)
    public void setBytes(int begin, int end, byte[] setBits) {
        if (begin < 0 || end >= bytes.size() || end <= begin || setBits.length != end - begin + 1)
            throw new IllegalArgumentException("Can't set bytes from " + begin + " to " + end);

        for (int i = begin; i <= end; i++)
            bytes.set(i, setBits[i - begin]);
    }

    //get a bit on position position
    public Byte getByte(int position) {
        if (bytes.size() <= position)
            return null;
        return bytes.get(position);
    }
}
