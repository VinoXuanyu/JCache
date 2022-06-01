package consistenthash;

public class TestConsistentHash {
    public void testHashing(consistenthash test) {
        System.out.println("Testing Hashing");
        System.out.println(test.hash.hash("localhost:8001"));
        System.out.println(test.hash.hash("localhost:8001"));
        System.out.println(test.hash.hash("localhost:8002"));
        System.out.println(test.hash.hash("localhost:8003"));
    }

    public void testAdd(consistenthash test) {
        System.out.println("Testing add ");
        String[] keys = {"1", "2", "3"};
        test.add(keys);
        System.out.println(test.keys);
        System.out.println(test.hashMap);
    }

    public void testGet(consistenthash test ) {
        System.out.println("Testing get");
        String[] keys = {"1", "2", "3"};
        test.add(keys);
        System.out.println(test.get("1"));
        System.out.println(test.get("2"));
        System.out.println(test.get("3"));
        System.out.println(test.get("4"));
        System.out.println(test.get("localhost:8888"));
        System.out.println(test.get("localhost:8888"));
    }

    public static void main(String[] args) {
        TestConsistentHash test = new TestConsistentHash();
        consistenthash ch = new consistenthash(3, null);
        test.testHashing(ch);
        test.testAdd(ch);
        test.testGet(ch);
    }

}
