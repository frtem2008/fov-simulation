import java.util.*;

public class Bitfield {
    private final List<Integer> bits;

    public Bitfield() {
        bits = new ArrayList<>();
    }

    public Bitfield(int[] intBits) {
        bits = new ArrayList<>();
        for (int i = 0; i < intBits.length; i++)
            bits.add(intBits[i]);

    }

    public void setAll(int value) {
        for (int i = 0; i < bits.size(); i++) {
            bits.set(i, value);
        }
    }

    public Integer getBit(int position) {
        if (bits.size() <= position)
            return null;
        return bits.get(position);
    }


}
