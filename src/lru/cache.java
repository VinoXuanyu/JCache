package lru;

public class cache {
    public lru l;
    public int cacheByte;
    public int lock;
    cache(){
        lock=1;
        l=new lru();
    }
}
