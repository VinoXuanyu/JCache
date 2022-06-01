package singleflight;

import java.util.concurrent.CountDownLatch;

public class TestSingleFlight {
    public static void main(String[] args) {
        Process callManage = new Process();
        int count = 10;
        CountDownLatch cld = new CountDownLatch(count);
        for (int i = 0; i < count; i++) {
            new Thread(() -> {
                try {
                    cld.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                byte[] value = callManage.run("key", () -> {
                    System.out.println("func");
                    String bar="bar";
                    return null;
                });
                System.out.println(new String(value));
            }).start();
            cld.countDown();
        }
    }

}
