package lru;

import byteview.byteview;

import java.util.ArrayList;

class Node {
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

public class DoubleLinkedList {

    public Node head = null;//表头
    public Node tail = null;
    public int length = 0;

    //头插法
    public DoubleLinkedList() {
        this.head = new Node(new byteview("1"));
        this.tail = new Node(new byteview("1"));
        head.next = tail;
        tail.prev = head;
    }


    public void addFirst(Node node) {
        Node nodeAfterHead = head.next;

        node.next = nodeAfterHead;
        this.head.next = node;
        node.prev = head;
        nodeAfterHead.prev = node;

        this.length += 1;
    }

    public void addFirst(byteview data) {
        Node node = new Node(data);
        this.addFirst(node);
        this.length += 1;
    }

    public Node popLast() {
        if (this.length <= 0) {
            return null;
        }
        this.length -= 1;
        Node node = this.tail.prev;
        Node nodePrev = node.prev;
        node.prev = null;
        node.next = null;
        nodePrev.next = this.tail;
        tail.prev = nodePrev;
        return node;
    }

    public Node popSelf(Node node){
        this.length -= 1;
        Node nodePrev = node.prev;
        Node nodeNext = node.next;
        node.next = null;
        node.prev = null;
        nodePrev.next = nodeNext;
        nodeNext.prev = nodePrev;
        return node;
    }

    public void moveToFirst(Node node) {
        Node self = this.popSelf(node);
        this.addFirst(self);
    }


    public void addHead(byteview val) {
        Node newNode = new Node(val);
        if (head == null) {
            head = newNode;//如果链表为空，增加新结点
        } else {
            newNode.next = head;
            head.prev = newNode;
            head = newNode;
        }
        length++;
    }

    public byteview[] contents(){
        ArrayList<byteview> arr = new ArrayList<>();
        Node temp = this.head.next;
        while (temp != this.tail){
            arr.add(temp.val);
            temp = temp.next;
        }
        byteview[] ret = new byteview[this.length];
        return arr.toArray(ret);
    }

    //尾插法
    public void addTail(byteview val) {
        Node newNode = new Node(val);
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
    public void deleteHead() {
        if (head == null) {
            System.out.println("该表为空表，删除的结点不存在");
        } else {
            Node curNode = head;
            head = curNode.next;
            head.prev = null;
        }
        length--;
    }


    //在链表尾部删除结点
    public void deleteTail() {
        if (head == null) {
            System.out.println("该表为空表，删除的结点不存在");
        } else {
            Node preNode = head;
            int count = 1;
            while (count < length - 1) {
                preNode = preNode.next;
                count++;
            }
            preNode.next = null;
        }
        length--;
    }


    //正向遍历输出链表
    public void printOrderNode() {
        if (head == null) {
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
    public void printReverseNode() {
        if (head == null) {
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
    public void insertList(byteview val, int index) {
        Node newNode = new Node(val);
        if (head == null) {
            head = newNode;//链表为空，插入
        }
        if (index > length + 1 || index < 1) {
            System.out.println("插入结点的位置不存在");
        }
        if (index == 1) {
            newNode.next = head;
            head.prev = newNode;
            head = newNode;//在链表开头插入
        } else {              //在链表中间或尾部插入
            Node preNode = head;
            int count = 1;
            while (count < index - 1) {
                preNode = preNode.next;
                count++;
            }
            Node curNode = preNode.next;
            newNode.next = curNode;
            newNode.prev = preNode;
            preNode.next = newNode;
            if (curNode != null) {
                curNode.prev = newNode;
            }
        }
        length++;
    }

    //在指定位置删除结点
    public void deleteList(int index) {
        if (index > length || index < 1) {
            System.out.println("删除结点的位置不存在");
        }
        if (index == 1) {
            Node curNode = head;
            head = curNode.next;
            head.prev = null;
            length--;
        } else {
            Node preNode = head;
            int count = 1;
            while (count < index - 1) {
                preNode = preNode.next;
                count++;
            }
            Node curNode = preNode.next;
            Node laterNode = curNode.next;
            preNode.next = laterNode;
            if (laterNode != null) {  //若被删除结点的后继结点不是null结点，那么设置其前驱结点
                laterNode.prev = preNode;//指针指向被删除结点的前驱结点
            }
            length--;
        }
    }

    //查找数据是否存在,与单链表一样
    public boolean containData(byteview val) {
        if (head == null) {
            System.out.println("空表");
            return false;
        }
        Node curNode = head;
        while (curNode.val != val) {
            if (curNode.next == null) {
                System.out.println("结点数据不存在");
                return false;
            }
            curNode = curNode.next;
        }
        System.out.println("结点数据存在");
        return true;
    }

    //获取指定位置的数据
    public byteview getIndexData(int index) {
        if (head == null) {
            System.out.println("空表");
            return null;
        }
        if (index > length || index < 1) {
            System.out.println("结点位置不存在");
            return null;
        }
        Node curNode = head;
        int count = 1;
        while (count != index) {
            curNode = curNode.next;
            count++;
        }
        curNode.display();
        System.out.println();
        return curNode.val;
    }

    //修改指定位置的结点数据
    public void updateIndexData(int index, byteview val) {
        if (head == null) {
            System.out.println("空表");
        }
        if (index > length || index < 1) {
            System.out.println("结点位置不存在");
        }
        Node curNode = head;
        int count = 1;
        while (count != index) {
            curNode = curNode.next;
            count++;
        }
        curNode.val = val;
    }

    //打印链表
    public void printList() {
        Node tmp = head;
        while (tmp != null) {
            System.out.print(tmp.val + " ");
            tmp = tmp.next;
        }
        System.out.println();
    }

}
