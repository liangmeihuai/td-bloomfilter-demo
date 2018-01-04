package com.talkingdata.mc;

import com.talkingdata.mc.ds.algorithm.BloomData;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by hailong.chen on 2017/12/04.
 */
public class TestBf {

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {

        long expectedInsertions = (long) 1269357;   //设备ID数量
        double emplifyRate = 0.01;  //误报率，取值区间为 [0,1)
        double b = 10;
        BloomData bloomData = BloomData.create((long)(expectedInsertions + 100),emplifyRate);

        File file = new File("/Users/hailong.chen/Documents/imei.all");
        FileReader fr = new FileReader(file);

        BufferedReader br = new BufferedReader(fr);

        String line  = null;
        File md5File = new File("/Users/hailong.chen/Documents/imei_md5.all");
        FileWriter fw = new FileWriter(md5File);

        while ( (line = br.readLine()) != null){
            bloomData.add(MD5(line.toUpperCase()).toUpperCase());
//            fw.write(MD5(line.toUpperCase()).toUpperCase()+"\n");
        }
        fw.close();
        bloomData.save("/Users/hailong.chen/Documents/bf/","vYzmCRZU."+ b +".bf");

        br.close();
        fr.close();

//        file = new File("/Users/hailong.chen/Documents/imei.all");
        file = new File("/Users/hailong.chen/Documents/10000w.log");
        fr = new FileReader(file);

        br = new BufferedReader(fr);
        line  = null;
        int i = 0;
        while ( (line = br.readLine()) != null){
//            if (bloomData.has(MD5(line.toUpperCase()).toUpperCase())){
            if (bloomData.has(line)){
                i++;
            }
        }

        System.out.println("--------:" + i );


    }


    static long optimalNumOfBits(long n, double p) {
        if (p == 0) {
            p = Double.MIN_VALUE;
        }
        return (long) (-n * Math.log(p) / (Math.log(2) * Math.log(2)));
    }


    static int optimalNumOfHashFunctions(long n, long m) {
        // (m / n) * log(2), but avoid truncation due to division!
        return Math.max(1, (int) Math.round((double) m / n * Math.log(2)));
    }


    public static String MD5(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md;
        byte[] md5 = new byte[64];
        try {
            md = MessageDigest.getInstance("MD5");
            md.update(text.getBytes("iso-8859-1"), 0, text.length());
            md5 = md.digest();
        } catch (Exception e) {
        }
        return convertedToHex(md5);
    }

    public static String convertedToHex(byte[] data) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < data.length; i++) {
            int halfOfByte = (data[i] >>> 4) & 0x0F;
            int twoHalfBytes = 0;
            do {
                if ((0 <= halfOfByte) && (halfOfByte <= 9)) {
                    buf.append((char) ('0' + halfOfByte));
                } else {
                    buf.append((char) ('a' + (halfOfByte - 10)));
                }
                halfOfByte = data[i] & 0x0F;
            } while (twoHalfBytes++ < 1);
        }
        return buf.toString();
    }
}
