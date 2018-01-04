package com.talkingdata.mc.common;

import java.io.*;

/**
 * Created by tend on 2017/12/25.
 */
public class ReadLargeFile {
    private  static  final  String inputFile = "D:\\bloom\\Gina_trb\\Unknow_Id\\Unknow_Id\\Unknow_Id.csv";
    private  static  final  String outputFile = "D:\\bloom\\Gina_trb\\Unknow_Id\\Unknow_Id\\100w.csv";
    public static void main(String[] args) {
        largeFileIO();
    }

    public  static void largeFileIO() {
        try {
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(new File(inputFile)));
            BufferedReader in = new BufferedReader(new InputStreamReader(bis, "utf-8"), 10 * 1024 * 1024);//100M缓存
            BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile),10 * 1024 * 1024);
            int index = 0;
            String line = in.readLine();
            while (line !=null && index < 1000000) {
                line = in.readLine();
                if (line.contains("imei_md5")){
                    String[] strs = line.split(";");
                    for (String str : strs){
                        if (str.contains("imei_md5")){
                            String md5_line = str.split(":")[1];
                            bw.write(md5_line);
                            bw.newLine();
                            index ++;
                        }
                    }
                }
            }
            in.close();
            bw.flush();
            bw.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
