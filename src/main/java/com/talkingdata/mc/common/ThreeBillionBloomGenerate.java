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
public class ThreeBillionBloomGenerate {
    public static RuntimeException main(String[] args) throws IOException, NoSuchAlgorithmException {
        long expectedInsertions = 3880689033L;   //设备ID数量
        double emplifyRate = 0.01;  //误报率，取值区间为 [0,1)
        double amplification = 1.2;

        String inputPath = null;
        String saveDir = null;
//        if (args == null || args.length == 0){
//            inputPath = "D:\\bloom\\bloom_mac_nocolon\\part-06143";
//            saveDir = ""D:\\bloom\\bloom_mac_nocolon"";
//        }
        if (args == null || args.length < 2){
            System.out.println("args.length <2 ,error");
            return new RuntimeException("args.length <2 ,error\"");
        }
        inputPath = args[0];
        saveDir = args[1];
        System.out.println("inputPath is [" + inputPath + "],saveDir is [" +saveDir+ "]");
        BloomData bloomData = BloomData.create((long)(expectedInsertions * amplification),emplifyRate);
        List<File> fileList = new ArrayList<File>();
        getFileList(inputPath,fileList);
        int index = 0;
        for (File file : fileList){
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line  = br.readLine();
            while ( line != null){
                bloomData.add(line);
                line  = br.readLine();
            }
            br.close();
            System.out.println("index = [" + index ++ + "],fileName is [" + file.getAbsolutePath() + "]");
        }
        bloomData.save(saveDir,"mac_no_colonMd5File");
        System.out.println("generate Bloom Data File End");
//        int i = 0;
//        while ( (line = br.readLine()) != null){
//            if (bloomData.has(line)){
//                i++;
//            }
//        }
//        System.out.println("--------:" + i );
        return null;
    }

    private BloomData loadBloomByFile(String inflateFilePath) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(new File(inflateFilePath));
        BufferedInputStream bis = new BufferedInputStream(fileInputStream);
        BloomData bd = BloomData.load(bis);
        return bd;
    }
//    private BloomData loadBloomByFile(String inflateFilePath) throws IOException {
//        FileInputStream fileInputStream = new FileInputStream(new File(inflateFilePath));
//        GZIPInputStream gzipInputStream = new GZIPInputStream(fileInputStream);
//        BufferedInputStream bis = new BufferedInputStream(gzipInputStream);
//        BloomData bd = BloomData.load(bis);
//        return bd;
//    }

    @Test
    public void testCheckBloom() throws IOException {
        String[] ids = {"ABC","CDG","DFD","FDE","8AEF37341F5F226FCA521F49A084722F","69452FEE98FD37253FEDD057FCD5B921"};
        String inflateFilePath = "D:\\bloom\\bloom_mac_nocolon\\mac_no_colonMd5File";
        BloomData bloomData = loadBloomByFile(inflateFilePath);
        for (String id : ids){
            if (bloomData.has(id)){
                System.out.println("line += ..=" + id);
            }
        }
    }



    public static List<File> getFileList(String strPath, List<File> fileList) {
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
