import byteview.byteview;
import java.util.HashMap;

public class geecache {
    public String name;
    public IPeerPicker peers;
    //public static HashMap<String, String> mainCache = new HashMap<>();//模拟mainCache TODO: Lishengze， 修改为cache类
    public  static cache mainCache=new cache();
    interface IGetter{
        public byteview get(String key);
    }
    public IGetter getter;
    // TODO: Lishengze. 添加新的成员变量 getter i.e. public IGetter getter = xxxxx; 其中getter为实现了IGetter接口的类  IGetter定义：{public byteview get(String);}
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
    }
    public byteview get(String key){  // TODO: Yanglichao： 使用singleflight算法包装get方法
        // 1. 本地的miancache里 （cache -> lru ) value ? exist :
        // 2.1 检查是否应该去别的节点查找  是： 那就向其他的节点发起http请求？ 不是/pickpeer找到自己的情况：通过getter从本地获取
        String ret = String.valueOf(mainCache.get(key));
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
            return this.getLocally(key); // -> getter
        }
    }


    // TODO: Lishengze 调用getter.get 从数据源获取数据
    public byteview getLocally(String key){
        populateCache(key,getter.get(key));
        return getter.get(key);
    }

    // TODO: Lishengze populateCache 将数据添加到mainCache中
    public void populateCache(String key,byteview b){
        String str= new String(b.bytes());
        mainCache.put(key,str);
    }
    public byteview getFromPeer(IPeerGetter getter, String key) {
        System.out.println("getFromPeer: "+getter.toString()+"  "+this.name);

        return getter.get(this.name, key);
    }//使用实现了 PeerGetter 接口的 httpGetter 从访问远程节点，获取缓存值。
}