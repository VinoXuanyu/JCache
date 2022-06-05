import byteview.byteview;

public class TestCache {
    public static void main(String[] args) {
        cache cache = new cache(3);
        System.out.println(cache.remainVolume());
        cache.put("1", new byteview("1"));
        System.out.println(cache.getContents());
        cache.put("2", new byteview("2"));
        System.out.println(cache.getContents());
        cache.put("3", new byteview("3"));
        System.out.println(cache.getContents());
        cache.put("4", new byteview("4"));
        System.out.println(cache.getContents());

        System.out.println(cache.get("1"));;
        System.out.println(cache.getContents());
        System.out.println(cache.get("2"));;
        System.out.println(cache.getContents());
        System.out.println(cache.get("3"));;
        System.out.println(cache.getContents());
        System.out.println(cache.get("4"));;
        System.out.println(cache.getContents());
    }
}
