import byteview.Supplier;
import byteview.byteview;
import lru.lru;

import java.util.concurrent.locks.ReentrantLock;

public class cache implements Supplier {
    public lru lru;
    public long cacheByte;
    public ReentrantLock mu;

    public cache(long maxBytes) {
        this.cacheByte = maxBytes;
        this.lru = new lru(maxBytes);
        this.mu = new ReentrantLock();
    }

    public byteview get(String key) {
        this.mu.lock();
        byteview ret = this.lru.get(key);
        this.mu.unlock();
        return ret;
    }

    public void put(String key, byteview data) {
        this.mu.lock();
        this.lru.put(key, data);
        this.mu.lock();
    }

    public long remainVolume(){
        return this.lru.nbytes;
    }
    ; // TODO: Lishengze cache.get方法由geecache.get调用

    public byteview[] getContents(){
        return this.lru.getContents();
    }
}


