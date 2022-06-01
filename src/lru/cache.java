package lru;

import byteview.byteview;

public class cache {
    public lru l;
    public int cacheByte;
    public byteview get(String key) {return null;}; // TODO: Lishengze cache.get方法由geecache.get调用
    public int lock;
    cache(){
        lock=1;
        l=new lru();
    }
}

