import java.util.HashMap;

interface IPeerPicker {
    public IPeerGetter pickPeer(String key);
}

interface IPeerGetter {
    public byteview get(String group, String key);
}

public class geecache {
    public String name;
    public IPeerPicker peers;

    public static HashMap<String, geecache> groups = new HashMap<>();

    public static geecache getGroup(String groupName){
        return groups.get(groupName);
    }

    public geecache(String name) {
        this.name = name;
    };

    public static geecache newGroup(String name) {
        geecache group = new geecache(name);
        geecache.groups.put(name, group);
        return group;
    };

    public byteview get(String key){
        return null;
    };

    public void registerPeers(IPeerPicker peerPicker) {
        if (this.peers != null) {
            System.out.println("Peers already registered");
            return;
        }
        this.peers = peerPicker;
    }

    public byteview load(String key) {
        if (this.peers != null) {
            IPeerGetter getter = this.peers.pickPeer(key);
            return this.getFromPeer(getter, key);
        } else {
            return this.getLocally(key);
        }
    }

    public byteview getLocally(String key){
        return null;
    }

    public byteview getFromPeer(IPeerGetter getter, String key) {
        return getter.get(this.name, key);
    }
}
