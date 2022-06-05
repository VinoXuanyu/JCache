package lru;

import byteview.byteview;

public class lru {
    public int maxBytes;
    public int nbytes;
    public DoubleLinkedList ll;

    public byteview get(int key){
        byteview b=new byteview(String.valueOf(ll.getIndexData(key)));
        return b;
    }; // TODO: Lishengze. LRU的get方法由cache.get调用
    lru(){}
}
