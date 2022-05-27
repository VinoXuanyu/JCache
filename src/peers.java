public class peers {
}

interface IPeerPicker {
    public IPeerGetter pickPeer(String key);
}

interface IPeerGetter {
    public byteview get(String group, String key);
}
