package singleflight;
import java.util.concurrent.CountDownLatch;
public class call {
    private byte[] val;
    private CountDownLatch cld;

    public byte[] getVal() {
        return val;
    }

    public void setVal(byte[] val) {
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
