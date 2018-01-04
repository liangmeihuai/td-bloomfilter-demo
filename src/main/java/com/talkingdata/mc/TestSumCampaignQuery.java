package com.talkingdata.mc;

import com.google.gson.Gson;
import org.junit.Assert;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.zip.GZIPInputStream;

/**
 * Created by hailong.chen on 2017/12/04.
 */
public class TestSumCampaignQuery {

    private static final int BUFFER_SIZE = 1024*10*5;

    static String[] campaignIds;
    final static Map<String,Map<String,AtomicLong>> result = new ConcurrentHashMap<String, Map<String, AtomicLong>>();

    static{
        String[] campaignIds = new String[]{"M1sZbWeV","lJ5izkeP","YPIwd21Q","o1gjxjA5","4o9qsbCO","ontEAaAG","HY4vF3bR","qjNVy5jB","Mp6cYy8o","CvgSV6ln","GjlvoAeN","HN3HxqRf","naaeDbP2","nYifYyMH","oBLnM8jJ","Da6R2fdO","25EeQAG4","WYxgPOSD","sHeIc3hY","rndWIjIu","xX8vSDBw","Fe7J5dNa","OWPQNKSt","QomirJpm","cGdW5Gf8","WNXpVqbW","ATIxMnDE","iNYaZGc1","Qc1y6wkQ","y5zecjZ2","5ZqekJYc","0dWkrItm","nHKhIHD8","0fhM0WU7","uMqrg5K9","STvumpcS","nV3iGnf7","jH3n00iY","lbUAzcDq","qvocsnSQ","uaj8rKFp","Sw1Zpdlw","j3ORJkPB","bjDgPa1N","kyqLyjmZ"};
        for (String campaignId : campaignIds){
            result.put(campaignId,new HashMap<String, AtomicLong>());
        }
        System.out.println(new Gson().toJson(result));
    }


    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {

        Assert.assertNotNull("args[0]",args);
        if (args.length < 1){
            System.out.println("args[0]:bf.zip path . ");
            System.out.println("exit ");
            System.exit(0);
        }

        Assert.assertNotNull("args[0]:bf.zip path. is null",args[0]);

        Path path = Paths.get(args[0]);

        final ExecutorService executorService = Executors.newFixedThreadPool(10);
        CompletionService cs = new ExecutorCompletionService(executorService);

        long count = 0L;
        List<File> fileList = new ArrayList<File>();
        getFileList(path.toString(),fileList);

        for (final File file : fileList){
            System.out.print(file.getName());
        }

        System.out.print("-------------------------------------");

        for (final File file : fileList){
            cs.submit(new Callable<Integer>(){
                public Integer call() {
                    try {
                        return sumTrue(file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return 0;
                }
            });
        }

        for (int i = 0; i < fileList.size(); i++) {
            cs.take().get();
            System.out.print("\r 已完成" + i + "/" + fileList.size());
        }

        executorService.shutdown();

        System.out.println("\nget true count:" + new Gson().toJson(result));

        System.exit(0);
    }

    public static int sumTrue(File path) throws IOException {
        Long startTime = System.currentTimeMillis();

        if (!path.exists()){
            System.out.println("file not find");
            return 0;
        }

        BufferedReader reader = getReader(path);

        String line;
        long count = 0L;
        int i = 0;
        System.out.println("开始匹配。");
        while ((line = reader.readLine()) != null) {
//            2017-07-27_00:00:00,m:491,c:HY4vF3bR,t:243,id:A82ADC5187BE787D15D7C9146EF59E0F,F,Y
            if (line.contains(",")){
                String[] ss = line.split(",");
                String campaignaid= ss[2].split(":")[1];
                if (result.containsKey(campaignaid)){
                    Map<String,AtomicLong> m = result.get(campaignaid);
                    String day = line.substring(0,10);
                    if(ss[5].equalsIgnoreCase("T")){
                        if (m.containsKey(day)){
                            m.get(day).getAndIncrement();
                        }else {
                            AtomicLong atomicLong = new AtomicLong(1);
                            m.put(day,atomicLong);
                        }
                    }
                }
            }
        }
//        Long estimatedTime = System.currentTimeMillis() - startTime;
//        System.out.println("path=" + path.getAbsolutePath());
//        System.out.println("\nget true count:" + count);
//        System.out.printf("stream Diff: %d ms\n", estimatedTime);
//        return new HashMap<String,Map<String,AtomicLong>>();
        return 1;
    }

    public static BufferedReader getReader(File f) throws IOException {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        new GZIPInputStream(new FileInputStream(f),BUFFER_SIZE)
                ),BUFFER_SIZE);
        return reader;
    }


    public static List<File> getFileList(String strPath,List<File> fileList) {
        File dir = new File(strPath);
        File[] files = dir.listFiles(); // 该文件目录下文件全部放入数组
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                String fileName = files[i].getName();
                if (files[i].isDirectory()) { // 判断是文件还是文件夹
                    getFileList(files[i].getAbsolutePath(),fileList); // 获取文件绝对路径
                } else {
                    String strFileName = files[i].getAbsolutePath();
//                    System.out.println("---" + strFileName);
                    fileList.add(files[i]);
                }
            }

        }
        return fileList;
    }

}
