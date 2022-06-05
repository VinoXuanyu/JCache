package singleflight;

import byteview.Supplier;
import byteview.byteview;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class singleflight {
    private final Lock lock = new ReentrantLock();
    private Map<String, call> callMap;

    public byteview run(String key, Supplier getter) {
        this.lock.lock();
        if (this.callMap == null) {
            this.callMap = new HashMap<>();
        }
        call call = this.callMap.get(key);
        if (call != null) {
            this.lock.unlock();
            call.await();
            return call.getVal();
        }
        call = new call();
        call.lock();
        this.callMap.put(key, call);
        this.lock.unlock();

        call.setVal(getter.get(key));
        call.done();

        this.lock.lock();
        this.callMap.remove(key);
        this.lock.unlock();

        return call.getVal();
    }
}
