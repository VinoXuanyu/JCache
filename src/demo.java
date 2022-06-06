import byteview.byteview;
import getters.IGetter;
import getters.mysqlGetter;

public class demo {
    public static void main(String[] args) {
        IGetter getter = new mysqlGetter("test", "root", "jin196632");
        geecache cache = new geecache("websites", getter);
        String key = "websites:url:name=\"Google\"";
        byteview val = new byteview("");
        long beginTime = System.currentTimeMillis();
        for (int i = 0; i < 1000; i ++) {
            val = cache.get(key);
        }
        System.out.println("Value for key: " + key + " is " + val.string());
        long finishTime = System.currentTimeMillis();
        System.out.println("Time Cost With Cache: " + Long.toString((finishTime - beginTime)) + "ms");


        beginTime = System.currentTimeMillis();
        for (int i = 0; i < 1000; i ++) {
            val = getter.get("websites:url:name=\"Google\"");
        }
        finishTime = System.currentTimeMillis();
        System.out.println("Time Cost Without Cache: " + Long.toString((finishTime - beginTime)) + "ms");
    }
}
