import byteview.byteview;

import java.io.File;
import java.io.FileNotFoundException;

public class Testpersistence {
    public static void main(String[] args) throws FileNotFoundException {
        persistence p = new persistence();
        //这部分为将数据缓存到.json文件中，测试时将第二部分隐去，且删除backups_aaa.json

        geecache gee = new geecache("aaa");
        geecache.groups.put(gee.name, gee);
        byteview a = new byteview("1");
        byteview b = new byteview("2");
        byteview c = new byteview("3");
        gee.mainCache.put("a", a);
        gee.mainCache.put("b", b);
        gee.mainCache.put("c", c);
        p.preserve(gee.name);


        //这部分为初始化时写会数据，在测试一结束后注掉测试1即可

        System.out.println(geecache.groups.size());
        File file = new File("backups");
        String[] fileNameLists = file.list();
        for (int i = 0; i < fileNameLists.length; i++) {
            fileNameLists[i] = fileNameLists[i].substring(8);
            fileNameLists[i] = fileNameLists[i].substring(0, fileNameLists[i].length() - 5);
            System.out.println(fileNameLists[i]);
            p.recover(fileNameLists[i]);
        }
        System.out.println(geecache.groups.size());
        System.out.println(geecache.groups.get("aaa").mainCache.lru.get("a"));
        System.out.println(geecache.groups.get("aaa").mainCache.lru.get("b"));
        System.out.println(geecache.groups.get("aaa").mainCache.lru.get("c"));

    }
}
