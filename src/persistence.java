import com.google.gson.*;
import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import lru.DoubleLinkedList.*;
import byteview.byteview;
import lru.Node;

public class persistence {
    static String base="backups";
    persistence(){}

    //根据文件名将json文件中的内容恢复到lru中
    void recover(String name) throws FileNotFoundException {
        String key;
        byteview data;
        String file_name=base+"_"+name+".json";
        String file_location=base+"/"+file_name;
        geecache tempcache=geecache.newGroup(name);
        geecache.groups.put(name, tempcache);
        JsonParser parse=new JsonParser();
        JsonObject json=(JsonObject) parse.parse(new FileReader(file_location));
        Iterator iter = json.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            key=entry.getKey().toString();
            data=(byteview) entry.getValue();
            tempcache.mainCache.put(key,data);
        }
    }
    //
    void preserve(String name){
        String file_name=base+"_"+name+".json";
        String file_location=base+"/"+file_name;
        geecache tempgeecache=geecache.groups.get(name);
        JsonObject json = new JsonObject();
        HashMap<String, Node> keyNodeMap = tempgeecache.mainCache.lru.keyNodeMap;
        for (String key:keyNodeMap.keySet()){
            String data=new String(keyNodeMap.get(key).val.bytes());
            json.addProperty(key,data);
        }
        String l =json.toString();
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(file_location));
            out.write(l);
            out.close();
        } catch (IOException e) {
        }


    }
}
