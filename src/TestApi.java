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
    public static String[] addrs = new String[]{"http://localhost:8996", "http://localhost:8997", "http://localhost:8998"};
    public static geecache group =  geecache.newGroup("test", (IGetter) new testGetter());
    public static void helper(){
        APIServer.addrs = addrs;
        APIServer.startSubServer(addrs[2], group);
    }
    public static void helperMain() {
        APIServer.startMainServer(0, group, addrs);
        for (int i = 0; i < 2; i++) {
            APIServer.startSubServer(addrs[i], group);
        }
    }
    public static void main(String[] args) {
        helperMain();
        helper();
    }
}
