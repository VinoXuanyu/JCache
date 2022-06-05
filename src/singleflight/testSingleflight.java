package singleflight;

import byteview.byteview;

import java.util.concurrent.CountDownLatch;

public class testSingleflight {
    public static void main(String[] args) {
        singleflight callManage = new singleflight();
        int count = 10;
        CountDownLatch cld = new CountDownLatch(count);
        for (int i = 0; i < count; i++) {
            new Thread(() -> {
                try {
                    cld.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                byteview value = callManage.run("key", (String key) -> {
                    System.out.println("func");
                    return new byteview("bar");
                });

                System.out.println(new String(value.bytes()));
            }).start();
            cld.countDown();
        }
    }

}
