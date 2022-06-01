package consistenthash;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.zip.CRC32;

interface IHashFunc{
    public int hash(String key);
}


public class consistenthash {
    public IHashFunc hash;
    public int replicas;
    public ArrayList<Integer> keys;
    public HashMap<Integer, String> hashMap;
    public consistenthash(int replicas, IHashFunc fn){
        this.replicas = replicas;
        this.hashMap = new HashMap<>();
        this.keys = new ArrayList<>();
        if (fn == null) {
            this.hash = new IHashFunc() {
                @Override
                public int hash(String key) {
                    CRC32 temp = new CRC32();
                    temp.update(key.getBytes(StandardCharsets.UTF_8));
                    int ret = (int) temp.getValue();
                    return ret;
                }
            };
        }
    }

    public void add(String[] keys) {
        for (String key : keys) {
            for (int i = 0; i < this.replicas; i++) {
                String hashKey = new String(key + Integer.toString(i));
                int hashVal = this.hash.hash(hashKey);
                this.keys.add(hashVal);
                this.hashMap.put(hashVal, key);
            }
        }
        this.keys.sort(Comparator.naturalOrder());
    }

    private int searchInsertPos(int val){
        int start =  0;
        int end =  this.keys.size();
        int mid = 0;
        while (start < end) {
            mid = (start + end) / 2;
            int midVal = this.keys.get(mid);
            if (midVal == val) {
                return mid;
            } else if(midVal < val) {
                start = mid + 1;
            } else {
                end = mid;
            }
        }
        return start;
    }

    public String get(String key){
        if (this.keys.isEmpty()) {
            return "";
        }
        int hashVal = this.hash.hash(key);
        int idx = searchInsertPos(hashVal);
        return this.hashMap.get(this.keys.get(idx % this.keys.size()));
    }
}
