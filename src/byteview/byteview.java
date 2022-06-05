package byteview;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class byteview {
    byte[] bytes;

    public byteview(byte[] bytes) {
        this.bytes = bytes;
    }

    public byteview(String str) {
        this.bytes = str.getBytes(StandardCharsets.UTF_8);
    }


    @Override
    public String toString() {
        return "byteview{" +
                "bytes=" + Arrays.toString(bytes) +
                '}';
    }

    public long len() {
        return this.bytes.length;
    }

    public byte[] toString(String str) {
        return str.getBytes(StandardCharsets.UTF_8);
    }

    public byte[] bytes() {
        return bytes;
    }
}