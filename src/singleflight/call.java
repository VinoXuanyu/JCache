package singleflight;

import byteview.byteview;

import java.util.concurrent.CountDownLatch;

public class call {
    private byteview val;
    private CountDownLatch cld;

    public byteview getVal() {
        return val;
    }

    public void setVal(byteview val) {
        this.val = val;
    }

    public void await() {
        try {
            this.cld.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void lock() {
        this.cld = new CountDownLatch(1);
    }

    public void done() {
        this.cld.countDown();
    }
}
