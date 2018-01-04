package com.talkingdata.mc;

import com.google.common.math.LongMath;
import com.google.common.primitives.Ints;
import com.talkingdata.mc.ds.algorithm.BloomData;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Created by hailong.chen on 2017/11/29.
 */
public class Main {

    public static void main(String[] args) throws IOException {
        long expectedInsertions = (long)  Math.pow(1,9);   //设备ID数量
        double emplifyRate = 0.01;  //误报率，取值区间为 [0,1)

        String path = "/Users/hailong.chen/Documents/bf/";
        for (int i = 2; i<= 5; i++){
            expectedInsertions = 100000 * (long) Math.pow(10,i);
            for (int j = 2; j <= 5; j++){
                emplifyRate =  1.0/ Math.pow(10,j);
                BloomData bloomData = BloomData.create(expectedInsertions,emplifyRate);
                bloomData.save(path,expectedInsertions+"_"+emplifyRate+".bf" );
                bloomData = null;
            }
        }

//        expectedInsertions =  1000000;   //设备ID数量
////        emplifyRate = 0.0000001;  //误报率，取值区间为 [0,1)
//        emplifyRate = 0.01;  //误报率，取值区间为 [0,1)
//
//        int b = 10;
//        BloomData bloomData = BloomData.create(expectedInsertions * b ,emplifyRate);
////        bloomData.save(path,expectedInsertions+"_"+emplifyRate+".bf" );
//
//        //创建  1000000000  100000000   1000000000  10000000
//
//        System.out.println("getEmplifyRate:" + bloomData.getEmplifyRate());
//////        //创建测试数据
//        for (int i = 0; i < expectedInsertions  ; i++){
//            bloomData.add(String.valueOf(i));
//        }
////
////        //验证设备ID是否在
//        int  j = 50;
//        long c = 0;
//        for (long i = 0 , n = expectedInsertions * j; i < n; i++){
//            if (bloomData.has(String.valueOf(i)))
//                c++;
//        }
//        System.out.println(c + "/" + expectedInsertions*j);


    }
//
//


//    public static void main(String[] args) throws IOException {
//        BloomData bloomData = BloomData.load("/Users/hailong.chen/Downloads/","imei_123251512025488679");
//
//        System.out.println(bloomData.getEmplifyRate());
//
//        int sum = 0;
//
//
//        File file = new File("/Users/hailong.chen/Documents/campaign_v.1");
//        FileReader fr = new FileReader(file);
//
//        BufferedReader br  = new BufferedReader(fr);
//        String str = null;
//        int all = 0;
//        Set<String> set = new HashSet<String>();
//        while ( (str = br.readLine()) != null ){
//            if (bloomData.has(str)){
//                sum++;
//            }
//            all++;
//            set.add(str);
//        }
//        System.out.println(sum);
//        System.out.println(set.size());
//        System.out.println(all);
//
//
//    }


}
