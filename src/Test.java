//Testing new features before implementing
import java.util.Arrays;
public class Test {
    public static void main(String[] args) {
        int a = 1001;
        int b;
        byte[] arr = Utils.byteArrFromInt(a);
        b = Utils.intFromByteArr(arr);
        System.out.println(Arrays.toString(arr));
        System.out.println(b);
        assert a == b;
    }
}
