import byteview.byteview;
import java.io.FileNotFoundException;

public class TestPersistence {
    public static void testSave(){
        try {
            // 测试一：程序退出时 应有backups/TestPersistence.json文件
            geecache gee = geecache.newGroup("Test1");
            byteview a = new byteview("1");
            byteview b = new byteview("2");
            byteview c = new byteview("3");
            gee.populateCache("a", a);
            gee.populateCache("b", b);
            gee.populateCache("c", c);
            geecache ge = geecache.newGroup("Test2");
            ge.populateCache("a", a);
            ge.populateCache("b", b);
            ge.populateCache("c", c);
        }catch (Exception  e){
            e.printStackTrace();
        }
    }

    public static void testRecover(){
        try {
            // 测试二： 应打印a b c
            geecache gee = geecache.newGroup("Test1");
            System.out.println(gee.get("a"));
            System.out.println(gee.get("b"));
            System.out.println(gee.get("c"));
        } catch(Exception e){
            e.printStackTrace();
        }

    }
    public static void main(String[] args) throws FileNotFoundException {
        // 先运行测试一， 在运行测试二
        //testSave();
        testRecover();
    }
}
