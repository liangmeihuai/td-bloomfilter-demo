package com.talkingdata.mc.multithreadbloom;

import com.talkingdata.mc.ds.algorithm.BloomData;

/**
 * @author Created by meihuai.liang
 * @version 1.0.0.0
 * @date 2018/4/12 11:20
 * @since 1.0
 */
public class MultiThreadBloomTest {
    private static final int EXPECTED_ROOT_INSERT = 100000;
    private static final int EXPECTED_INSERTIONS = 1000000;
    private static final double AMPLIFICATION = 5;
    private static final double EMPLIFY_RATE = 0.01;
    public static void main(String[] args) throws InterruptedException {
        final BloomData bloomData = BloomData.create((long)(EXPECTED_INSERTIONS * AMPLIFICATION),EMPLIFY_RATE);
        for (int i = 0; i < 10; i ++){
            final int root = i;
            new Thread(new Runnable() {
                public void run() {
                    for (int j = EXPECTED_ROOT_INSERT * root; j < EXPECTED_ROOT_INSERT * (root + 1); j ++){
                        bloomData.add(j + "");
                    }
                }
            }).start();
        }
        Thread.sleep(5000);
        System.out.println("before" + bloomData.getCount());
        System.out.println("before" + bloomData.getMatchCount().get());
        for (int i = 0; i < 10; i ++){
            final int root = i;
            new Thread(new Runnable() {

                public void run() {
                    for (int j = EXPECTED_ROOT_INSERT * root; j < EXPECTED_ROOT_INSERT * (root + 1); j ++){
                       boolean flag =  bloomData.has(j + "");
                        if (flag){
                            bloomData.setCount(bloomData.getCount() + 1);
                            bloomData.getMatchCount().incrementAndGet();
                        }
                    }
                }
            }).start();
        }
        Thread.sleep(5000);
        System.out.println("after" + bloomData.getCount());
        System.out.println("after" + bloomData.getMatchCount().get());
    }

}
