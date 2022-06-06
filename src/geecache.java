import byteview.byteview;
import getters.IGetter;
import singleflight.singleflight;
import utils.welcome;

import java.util.HashMap;

public class geecache {
    public String name;
    public static long defaultVolume = 10000000;
    public static cache mainCache = new cache(defaultVolume);
    public IPeerPicker peers;
    public singleflight calls = new singleflight();

    public IGetter getter;

    {
        welcome.welcome();
    }

    public static HashMap<String, geecache> groups = new HashMap<>();

    // TODO(persistence): 此为备份工作线程， 此线程每隔固定时间(config.backupInterval),
    //                    扫描groups中的所有geecache， 并保存当前lru内容备份。
    public static Thread backupWorker;

    public static void backup(){
        // TODO (persistence): 备份子线程要执行的runable
    }

    public static void recover(String name){
        // TODO(persistence):
        //  1. 读取config.json， 找到备份文件所在位置
        //  2. 去备份文件夹里找对应的备份文件 backup_name.json
        //  3. 读取备份文件， 调用pupulate方法， 将键值对存进LRU中
    }

    public static geecache newGroup(String name) {
        // TODO(persistence)：
        //  1. 调用Recover 恢复备份
        //  2. 开启备份线程 e.g. geecache.backupWorker = new Thread(geecache.backup); Thread.run();

        geecache group = new geecache(name);
        geecache.groups.put(name, group);
        return group;
    }
    public static geecache getGroup(String groupName) {
        return groups.get(groupName);
    }

    public geecache(String name) {
        this.name = name;
    }

    public geecache(String name, IGetter getter){
        this.name = name;
        this.getter = getter;
    }

    // 这个是geecache里的get方法，暴露给用户 e.g. mycache = new geecache();  byteview val = geecache.get(key)；
    public byteview get(String key) {  // TODO: Yanglichao： 使用singleflight算法包装get方法
        // 1. 本地的miancache里 （cache -> lru ) value ? exist :
        // 2.1 检查是否应该去别的节点查找  是： 那就向其他的节点发起http请求？ 不是/pickpeer找到自己的情况：通过getter从本地获取
        if (this.calls == null) {
            this.calls = new singleflight();
        }
        byteview ret = this.calls.run(key, this.mainCache);
        if (ret == null) {
            return load(key);
        } else {
            return ret;
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
        System.out.println("在查找load peers key是：" + key);
        if (this.peers != null) {
            IPeerGetter getter = this.peers.pickPeer(key);
            if (getter == null) {
                System.out.println("哈希为自身" + this.getLocally(key));
                return this.getLocally(key);
            }
            System.out.println("查找load peers结果为：" + getter.toString());

            return this.getFromPeer(getter, key);
        } else {
            byteview ret = this.getLocally(key);
            System.out.println("查找load peers为空" + ret);
            return ret; // -> getter
        }
    }


    // TODO: Lishengze 调用getter.get 从数据源获取数据
    public byteview getLocally(String key) {
        byteview data = this.getter.get(key);
        populateCache(key, data);
        return data;
    }

    // TODO: Lishengze populateCache 将数据添加到mainCache中
    public void populateCache(String key, byteview data) {
        mainCache.put(key, data);
    }

    public byteview getFromPeer(IPeerGetter getter, String key) {
        System.out.println("getFromPeer: " + getter.toString() + "  " + this.name);

        return getter.get(this.name, key);
    }//使用实现了 PeerGetter 接口的 httpGetter 从访问远程节点，获取缓存值。
}