import java.nio.charset.StandardCharsets;


public class byteview {
    byte[] bytes;

    public byte[] stringToBytes(String str) {
        return str.getBytes(StandardCharsets.UTF_8);
    }
    public byte[] bytes() {
        return bytes;
    }

    public byteview(byte[] bytes) {};

    public byteview(String str){
        this.bytes = str.getBytes(StandardCharsets.UTF_8);

    }

}