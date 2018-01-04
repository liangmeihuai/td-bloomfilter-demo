package com.talkingdata.mc.backup;
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
        String finalPath = args[2];// 把匹配的id和最终的个数输出到一个文件中去[dir目录]
        System.out.println("bloomPath is [" + bloomPath + "],matchPath is [" +matchPath+ "],finalPath is [" +finalPath+ "]");

        List<File> fileList = new ArrayList<File>();
        getFileList(matchPath, fileList);
        testCheckBloom(bloomPath,matchPath,finalPath);
        System.out.println("bloom--match is done....");
    }

    private static BloomData loadBloomByFile(String inflateFilePath) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(new File(inflateFilePath));
        BufferedInputStream bis = new BufferedInputStream(fileInputStream);
        BloomData bd = BloomData.load(bis);
        return bd;
    }


    private static void testCheckBloom(String bloomPath,String matchPath,String finalPath) throws IOException {
        BloomData bloomData = loadBloomByFile(bloomPath);
        BufferedReader bufferedReader = new BufferedReader(new FileReader(matchPath));
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(finalPath));
        String id = bufferedReader.readLine();
        int index = 0;
        while (id != null){
            if (bloomData.has(id)){

                bufferedWriter.write(id);
                bufferedWriter.newLine();
//                System.out.println("each line index = [" +index+ "],id is [" +id+ "]");
               if (index % 10000 == 0){
                   System.out.println("10000w index = [" +index+ "]");
                   bufferedWriter.flush();
               }
            }
            id = bufferedReader.readLine();
            index ++;
        }
        bufferedWriter.close();
        bufferedReader.close();
        System.out.println("finally index is [" +index+ "]");
    }

    public static List<File> getFileList(String strPath, List<File> fileList) {
        File dir = new File(strPath);
        File[] files = dir.listFiles(new FileFilter() {
            public boolean accept(File file) {
                if (file.isDirectory()){
                    return true;
                }else {
                    String name = file.getName();
                    if (name.contains("252")){
                        return true;
                    }else {
                        return false;
                    }
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
