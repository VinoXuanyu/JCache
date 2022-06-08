import byteview.byteview;
import getters.IGetter;

import java.util.HashMap;
class testGetter implements IGetter{
    static HashMap<String, String> map = new HashMap<>();
    {
        map.put("key1", "val2");
        map.put("key2", "key2");
    }
    @Override
    public byteview get(String key) {
        return new byteview(map.get(key));
    }
}

public class TestApi {
    public static void main(String[] args) {
        String[] addrs = new String[]{"http://localhost:8996", "http://localhost:8997", "http://localhost:8998"};
        geecache group =  geecache.newGroup("test", (IGetter) new testGetter());
        APIServer.startMainServer(0, group, addrs);
        for (String addr : addrs) {
            APIServer.startSubServer(addr, group);
        }
    }
}
