import byteview.byteview;
import byteview.Supplier;
import lru.lru;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
public class cache implements Supplier {
    public lru l;
    public int cacheByte;
    public byteview get(String key){
        if (l.lock==1){
            return null;
        }
        int num=0;
        String tempkey;
        int tempvalue=0;
        num=dic.get(key);
        byteview tempb;
        byteview bb=l.get(num);
        Set<Map.Entry<String,Integer>> entrySet=dic.entrySet();
        for(Map.Entry<String,Integer> entry:entrySet){
            if(entry.getValue()==num){
                tempb=l.ll.getIndexData(num);
                l.ll.addTail(tempb);
                tempkey=entry.getKey();
                dic.remove(tempkey);
                dic.put(tempkey,l.ll.length);

            }else if(entry.getValue()>num){
                tempkey= entry.getKey();
                tempvalue=entry.getValue();
                tempvalue-=1;
                dic.remove(tempkey);
                dic.put(tempkey,tempvalue);

            }
        }
        l.lock=1;
        return bb;
    }; // TODO: Lishengze cache.get方法由geecache.get调用
    public byteview put(byteview b){
        String tempkey;
        int tempvalue;
        if(l.lock==1){
            return null;
        }
        while(l.nbytes+b.bytes().length> l.maxBytes){
            Set<Map.Entry<String,Integer>> entrySet=dic.entrySet();
            for(Map.Entry<String,Integer> entry:entrySet){
                if(entry.getValue()==1){
                    dic.remove(entry.getKey());
                }else{
                    tempkey=entry.getKey();
                    tempvalue=entry.getValue();
                    dic.remove(entry.getKey());
                    dic.put(tempkey,tempvalue);
                }
            }
            l.nbytes-=l.ll.getIndexData(1).bytes().length;
            l.ll.deleteHead();
        }
        l.ll.addTail(b);
        String s =new String(b.bytes());
        dic.put(s,l.ll.length);
        l.lock=1;
        return b;
    }
    public HashMap<String, Integer> dic=new HashMap<String,Integer>();

    cache(){}
}


