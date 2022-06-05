import byteview.byteview;
import lru.lru;

import java.util.HashMap;

public class cache {
    public lru l;
    public int cacheByte;
    public byteview get(String key) {
        int num=0;
        num=Integer.valueOf(dic.get(key));
        return l.get(num);
    }; // TODO: Lishengze cache.get方法由geecache.get调用
    public HashMap<String, String> dic=new HashMap<String, String>();
    public void put(String key,String value){
        dic.put(key, value);
        l.ll.addTail(Integer.valueOf(value));

    }
    cache(){}
}
