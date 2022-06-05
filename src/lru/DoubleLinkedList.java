package lru;
import byteview.byteview;
class Node{
    //数据域
    public byteview b;
    public Node next;//后继指针域
    public Node prev;//前驱指针域

    public Node(byteview b) {
        this.b = b;
        this.next = null;
        this.prev = null;
    }
    //显示该结点的具体数值
    public void display() {
        System.out.print( b + " ");
    }
}

public class DoubleLinkedList {

    public  Node head = null;//表头
    public  int length = 0;


    //头插法
    public DoubleLinkedList(){}


    public  void addHead(byteview b) {
        Node newNode = new Node(b);
        if(head == null) {
            head = newNode;//如果链表为空，增加新结点
        }
        else {
            newNode.next = head;
            head.prev = newNode;
            head = newNode;
        }
        length++;
    }
    //尾插法
    public  void addTail(byteview b) {
        Node newNode = new Node(b);
        if (head == null) {
            head = newNode;
        } else {
            Node curNode = head;
            int count = 1;
            while (count < length) {
                curNode = curNode.next;
                count++;
            }
            newNode.next = null;
            newNode.prev = curNode;
            curNode.next = newNode;
        }
        length++;
    }

    //从头节点删除
    public  void deleteHead() {
        if(head == null){
            System.out.println("该表为空表，删除的结点不存在");
        }
        else {
            Node curNode = head;
            head = curNode.next;
            head.prev = null;
        }
        length--;
    }


    //在链表尾部删除结点
    public  void deleteTail() {
        if(head == null){
            System.out.println("该表为空表，删除的结点不存在");
        }else{
            Node preNode = head;
            int count = 1;
            while(count < length-1) {
                preNode = preNode.next;
                count++;
            }
            preNode.next = null;
        }
        length--;
    }


    //正向遍历输出链表
    public  void printOrderNode() {
        if(head == null) {
            System.out.println("空表");
        }
        Node curNode = head;
        while (curNode != null) {
            curNode.display();
            curNode = curNode.next;
        }
        System.out.println();
    }

    //反向遍历输出链表
    public  void printReverseNode() {
        if(head == null) {
            System.out.println("空表");
        }
        Node curNode = head;
        while (curNode.next != null) {
            curNode = curNode.next;
        }
        while (curNode != null) {
            curNode.display();
            curNode = curNode.prev;
        }
        System.out.println();
    }

    //在指定位置插入结点
    public  void insertList(byteview b, int index) {
        Node newNode = new Node(b);
        if(head == null){
            head = newNode;//链表为空，插入
        }
        if(index > length+1 || index < 1) {
            System.out.println("插入结点的位置不存在");
        }
        if(index == 1) {
            newNode.next = head;
            head.prev = newNode;
            head = newNode;//在链表开头插入
        } else{              //在链表中间或尾部插入
            Node preNode = head;
            int count = 1;
            while(count < index-1) {
                preNode = preNode.next;
                count++;
            }
            Node curNode = preNode.next;
            newNode.next = curNode;
            newNode.prev = preNode;
            preNode.next = newNode;
            if(curNode != null) {
                curNode.prev = newNode;
            }
        }
        length++;
    }

    //在指定位置删除结点
    public  void deleteList(int index) {
        if(index > length || index < 1) {
            System.out.println("删除结点的位置不存在");
        }
        if(index == 1) {
            Node curNode = head;
            head = curNode.next;
            head.prev = null;
            length--;
        } else{
            Node preNode = head;
            int count = 1;
            while(count < index-1) {
                preNode = preNode.next;
                count++;
            }
            Node curNode = preNode.next;
            Node laterNode = curNode.next;
            preNode.next = laterNode;
            if(laterNode != null) {  //若被删除结点的后继结点不是null结点，那么设置其前驱结点
                laterNode.prev = preNode;//指针指向被删除结点的前驱结点
            }
            length--;
        }
    }

    //查找数据是否存在,与单链表一样
    public  boolean containData(byteview b) {
        if(head == null){
            System.out.println("空表");
            return false;
        }
        Node curNode = head;
        while(curNode.b!= b){
            if(curNode.next == null) {
                System.out.println("结点数据不存在");
                return false;
            }
            curNode =curNode.next;
        }
        System.out.println("结点数据存在");
        return true;
    }

    //获取指定位置的数据
    public  byteview getIndexData(int index) {
        if(head == null){
            System.out.println("空表");
            return null;
        }
        if(index > length || index < 1) {
            System.out.println("结点位置不存在");
            return null;
        }
        Node curNode = head;
        int count =1;
        while(count != index) {
            curNode =curNode.next;
            count++;
        }
        curNode.display();
        System.out.println();
        return curNode.b;
    }

    //修改指定位置的结点数据
    public  void updateIndexData(int index, byteview b) {
        if(head == null){
            System.out.println("空表");
        }
        if(index > length || index < 1) {
            System.out.println("结点位置不存在");
        }
        Node curNode = head;
        int count =1;
        while(count != index) {
            curNode =curNode.next;
            count++;
        }
        curNode.b = b;
    }

    //打印链表
    public  void printList(){
        Node tmp = head;
        while(tmp != null){
            System.out.print(tmp.b + " ");
            tmp = tmp.next;
        }
        System.out.println();
    }

}
