package byteview;

import java.nio.charset.StandardCharsets;

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
                "bytes=" + new String(bytes) +
                '}';
    }

    public long len() {
        return this.bytes.length;
    }

    public String string() {
        return new String(bytes);
    }

    public byte[] bytes() {
        return bytes;
    }
}