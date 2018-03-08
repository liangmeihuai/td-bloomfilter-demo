package com.talkingdata.mc.common;
import com.talkingdata.mc.ds.algorithm.BloomData;
import org.junit.Test;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hailong.chen on 2017/12/04.
 */
public class BloomMatch {
    public static void main(String[] args) throws IOException {
        if (args == null || args.length < 2){
            System.out.println("args.length <2,error");
            throw  new RuntimeException("args.length <2 ,error");
        }
        String bloomPath = args[0];
        String matchPath = args[1];// 需要被匹配的文件的路径[dir目录]
        List<File> fileList = new ArrayList<File>();
        getFileList(matchPath, fileList);
        System.out.println("fileList matchpath is [" +fileList+ "]");
        for (File file : fileList){
            String filePath = file.getPath();
            String resultPath = filePath.substring(0,filePath.lastIndexOf("/")) + "/" + "result.log";
            System.out.println("resultPath = [" +resultPath+ "]");
            BloomData bloomData = loadBloomByFile(bloomPath);
            testCheckBloom(bloomData,filePath,resultPath);
        }
        System.out.println("bloom--match is done....");
    }

    private static BloomData loadBloomByFile(String inflateFilePath) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(new File(inflateFilePath));
        BufferedInputStream bis = new BufferedInputStream(fileInputStream);
        BloomData bd = BloomData.load(bis);
        return bd;
    }


    private static void testCheckBloom(BloomData bloomData, String matchPath, String finalPath) throws IOException {
        File finalFile = new File(finalPath);
        if (!finalFile.exists()){
            finalFile.createNewFile();
        }
        BufferedReader bufferedReader = new BufferedReader(new FileReader(matchPath));
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(finalPath));
        String id = bufferedReader.readLine();
        int index = 0;
        int matchCount = 0;
        while (id != null){
            if (bloomData.has(id)){
                bufferedWriter.write(id);
                bufferedWriter.newLine();
//                System.out.println("each line index = [" +index+ "],id is [" +id+ "]");
               if (matchCount % 10000 == 0){
                   System.out.println("10000w matchCount = [" +matchCount+ "]");
                   bufferedWriter.flush();
               }
                matchCount ++;
            }
            id = bufferedReader.readLine();
            index ++;
        }
        bufferedWriter.close();
        bufferedReader.close();
        System.out.println("finally index is [" +index+ "]");
        System.out.println("finally matchCount is [" +matchCount+ "]");
    }

    public static List<File> getFileList(String strPath, List<File> fileList) {
        File dir = new File(strPath);
        File[] files = dir.listFiles(new FileFilter() {
            public boolean accept(File file) {
                if (file.isDirectory()){
                    return true;
                }else {
//                    String name = file.getName();
//                    if (name.contains("252")){
//                        return true;
//                    }else {
//                        return false;
//                    }
                    return true;
              }

            }
        }); // 该文件目录下文件全部放入数组
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
