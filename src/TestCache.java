import byteview.byteview;

public class TestCache {
    public static void printList(byteview[] views){
        for (byteview view : views){
            System.out.print(view);
        }
        System.out.println();
    }
    public static void main(String[] args) {
        cache cache = new cache(3);
        cache.put("1", new byteview("1"));
        TestCache.printList(cache.getContents());
        cache.put("2", new byteview("2"));
        TestCache.printList(cache.getContents());
        cache.put("3", new byteview("3"));
        TestCache.printList(cache.getContents());
        cache.put("4", new byteview("4"));
        TestCache.printList(cache.getContents());

        System.out.println(cache.get("1"));;
        TestCache.printList(cache.getContents());
        System.out.println(cache.get("2"));;
        TestCache.printList(cache.getContents());
        System.out.println(cache.get("3"));;
        TestCache.printList(cache.getContents());
        System.out.println(cache.get("4"));;
        TestCache.printList(cache.getContents());
    }
}
