package lru;

import byteview.byteview;

import java.util.HashMap;

public class lru {
    public long maxBytes;
    public int nbytes;

    public DoubleLinkedList ll;
    public HashMap<String, Node> keyNodeMap;
    public HashMap<Node, String> nodeKeyMap;

    public lru(long maxBytes) {
        this.maxBytes = maxBytes;
        this.keyNodeMap = new HashMap<>();
        this.nodeKeyMap = new HashMap<>();
        this.ll = new DoubleLinkedList();
    }

    public byteview get(String key) {
        Node node = this.keyNodeMap.get(key);
        if (node == null){
            return null;
        }
        this.ll.moveToFirst(node);
        return node.val;
    }

    public void put(String key, byteview data) {
        while (this.nbytes + data.len() > maxBytes) {
            Node node = this.ll.popLast();
            String keyOfNode = this.nodeKeyMap.get(node);
            this.nodeKeyMap.remove(node);
            this.keyNodeMap.remove(keyOfNode);
            this.nbytes -= node.val.len();
        }
        Node node = new Node(data);
        this.keyNodeMap.put(key, node);
        this.nodeKeyMap.put(node, key);
        this.ll.addFirst(node);
        this.nbytes += node.val.len();
    }

    public byteview[] getContents(){
        return this.ll.contents();
    }
    // TODO: Lishengze. LRU的get方法由cache.get调用

}