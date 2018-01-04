//package com.talkingdata.mc;
//
//import com.talkingdata.mc.ds.algorithm.BloomData;
//import javafx.util.Pair;
//
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.util.List;
//import java.util.Optional;
//import java.util.concurrent.*;
//import java.util.concurrent.atomic.AtomicLong;
//import java.util.stream.Collectors;
//
///**
// * Created by hailong.chen on 2017/12/04.
// */
//public class SearchStringByThreadPool {
//
//    public static void main(String[] args) throws IOException {
//
////        BloomData bloomData = BloomData.load("","");
//
//        try {
//            //创建5个固定线程的线程池
//            ExecutorService executorService = Executors.newFixedThreadPool(5);
//            List<Future<Long>> listFile =
//                    //这里是取所传入目录的最多四层，如果不知道这个API的话需要递归去做。
//                    Files.walk(Paths.get("/aaa/aaa/aa"), 1)
//                            .filter(file -> !Files.isDirectory(file) && file.toString().endsWith("java"))//文件文件夹和不是java的文件
//                            .map(path -> (Callable<Long>) () -> {//创建N多个Callable实现类
//                                AtomicLong matchCount = new AtomicLong();
//                                try {
//                                    Optional<Long> optional = Files.lines(path).map(str -> {
//                                        String[] ss = str.split(",");
//
//                                        if (true){
////                                        if (bloomData.has(ss[1])){
//                                            matchCount.getAndIncrement();
//                                        }
//                                        return matchCount.get();
//                                    }).reduce(Long :: sum);//合并最终的计算结果
//                                    long count = optional.isPresent() ?  optional.get() :0L;
//                                    return  count;
//                                } catch (IOException e) {
//                                    e.printStackTrace();
//                                }
//                                return matchCount.get();
//                            })
//                            .map(callable -> executorService.submit(callable))//提交给线程池进行处理
//                            .collect(Collectors.toList());
//
//            listFile.stream().mapToLong(file -> {
//                Long atomicLong = 0L;
//                try {
//                    atomicLong = file.get();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                } catch (ExecutionException e) {
//                    e.printStackTrace();
//                }
//                return atomicLong;
//            }).sum();
//            //关闭线程池
//            executorService.shutdown();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void test() {
//        String str = "mainmainmainmainmain";
//    }
//
//}
