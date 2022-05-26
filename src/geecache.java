import javax.print.attribute.HashPrintJobAttributeSet;
import java.util.HashMap;

public class geecache {
    public String name;
    public static HashMap<String, geecache> groups = new HashMap<>();

    public static geecache getGroup(String groupName){
        return groups.get(groupName);
    }

    public geecache(String name) {
        this.name = name;
    };

    public static geecache newGroup(String name) {
        geecache group = new geecache(name);
        geecache.groups.put(name, group);
        return group;
    };

    public byteview get(String key){
        return null;
    };


}
