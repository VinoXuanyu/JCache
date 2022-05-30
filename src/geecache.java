import java.nio.charset.StandardCharsets;
import java.util.HashMap;



public class geecache {
    public String name;
    public IPeerPicker peers;
    public static HashMap<String, String> mainCache = new HashMap<>();//模拟mainCache
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
        //用来模拟本地cache的缓存数据
        mainCache.put("1","A");
        mainCache.put("2","B");
        mainCache.put("3","C");
        return group;
    }

    public byteview get(String key){
        String ret = mainCache.get(key);
        if (ret == null) {
            return load(key);
        } else {
            return new byteview(ret);
        }
    }

    public void registerPeers(IPeerPicker peerPicker) {//将 实现了 PeerPicker 接口的 HTTPPool 注入到 Group 中。
        if (this.peers != null) {
            System.out.println("Peers already registered");
            return;
        }
        this.peers = peerPicker;
    }

    public byteview load(String key) {//使用 PickPeer() 方法选择节点，若非本机节点，则调用 getFromPeer() 从远程获取。若是本机节点或失败，则回退到 getLocally()
        System.out.println("在查找load peers key是："+key);
        if (this.peers != null) {
            IPeerGetter getter = this.peers.pickPeer(key);
            if (getter == null) {
                System.out.println("哈希为自身"+this.getLocally(key));
                return this.getLocally(key);
            }
            System.out.println("查找load peers结果为："+getter.toString());

            return this.getFromPeer(getter, key);
        } else {
            System.out.println("查找load peers为空"+this.getLocally(key));
            return this.getLocally(key);
        }
    }

    public byteview getLocally(String key){
        return null;
    }

    public byteview getFromPeer(IPeerGetter getter, String key) {
        System.out.println("getFromPeer: "+getter.toString()+"  "+this.name);

        return getter.get(this.name, key);
    }//使用实现了 PeerGetter 接口的 httpGetter 从访问远程节点，获取缓存值。
}
