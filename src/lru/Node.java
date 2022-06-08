package lru;

import byteview.byteview;

public class Node {
    //数据域
    public byteview val;
    public Node next;//后继指针域
    public Node prev;//前驱指针域

    public Node(byteview val) {
        this.val = val;
        this.next = null;
        this.prev = null;
    }

    //显示该结点的具体数值
    public void display() {
        System.out.print(val + " ");
    }
}
