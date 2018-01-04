package com.talkingdata.mc;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.talkingdata.mc.ds.DataBundle;
import com.talkingdata.mc.ds.algorithm.BloomData;
import com.talkingdata.mc.ds.algorithm.DeviceIdTypeParser;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by hailong.chen on 2017/12/04.
 */
public class TestBfByThreadPool {


    private static BloomDataBean bdb ;

    public static void main(String[] args) throws IOException {

        String zipPath = "/Users/hailong.chen/Documents/494_xidbp6kU.zip";

        Assert.assertNotNull("args[0]",args);
        if (args.length < 2){
            System.out.println("args[0]:bf.zip path . ");
            System.out.println("args[1]:devid file path. ");
            System.out.println("exit ");
            System.exit(0);
        }

        Assert.assertNotNull("args[0]:bf.zip path. is null",args[0]);
        Assert.assertNotNull("args[1]:devid file path. is null",args[1]);

//        loadBundleFile(zipPath);
        loadBundleFile(args[0]);
        checkDevId(args[1]);
        System.exit(0);
//        checkDevId("/Users/hailong.chen/Documents/ODGZvSfB.dev.2");
    }


    public static void loadBundleFile(String zipFilePath) throws IOException {
        System.out.println("zipFilePath=" + zipFilePath);
        ZipFile zipFile = new ZipFile(zipFilePath);
        ZipEntry bundleIndex = zipFile.getEntry(DataBundle.BUNDLE_FILE_NAME);
        BufferedReader br =
                new BufferedReader(new InputStreamReader(zipFile.getInputStream(bundleIndex)));
        StringBuffer buffer = new StringBuffer();
        String s;
        while ((s = br.readLine()) != null) {
            buffer.append(s);
        }
        br.close();
        System.out.println(buffer.toString());

        JsonObject biObj = new JsonParser().parse(buffer.toString()).getAsJsonObject();

        JsonArray auArray = biObj.getAsJsonArray(DataBundle.KEY_AUDIENCES);
        bdb = new BloomDataBean();
        Map<Integer,List<BloomData>> dataMap = bdb.getDataMap();
        Map<Integer,Integer> typeMap = bdb.getTypeMap();
        for (int i = 0; i < auArray.size(); i++) {
            JsonObject auObj = auArray.get(i).getAsJsonObject();

            int devIdType = auObj.get(DataBundle.KEY_DEVICE_ID_TYPE).getAsInt();
            String bfname = auObj.get(DataBundle.KEY_DEVICE_IDS).getAsString();

            ZipEntry devEntry = zipFile.getEntry(bfname);
            BufferedInputStream bis = new BufferedInputStream(zipFile.getInputStream(devEntry));
            BloomData bd = BloomData.load(bis);
            bis.close();

            if (dataMap.containsKey(devIdType)){
                dataMap.get(devIdType).add(bd);
            }else {
                List<BloomData> bds = new ArrayList();
                bds.add(bd);
                dataMap.put(devIdType,bds);
                DeviceIdTypeParser parser = new DeviceIdTypeParser(devIdType);
                typeMap.put(parser.getRawDeviceIdType(),devIdType);
            }
        }
        zipFile.close();
    }


    public static void checkDevId(String path) throws FileNotFoundException, IOException {
        Long startTime = System.currentTimeMillis();
        BufferedReader reader = getReader(new File(path));

        String line;
        long count = 0L;
        int i = 0;
        System.out.println("开始匹配。");
        while ((line = reader.readLine()) != null) {
            if (line.contains(",")){
                String[] ss = line.split(",");
                if (ss != null && ss.length == 2){
                    String deviceId = ss[1];
                    Integer deviceIdType = null;
                    try{
                        deviceIdType = Integer.valueOf(ss[0]);
                    }catch (Exception e){
                        continue;
                    }
                    List<BloomData> bds = bdb.getDataMap().get(deviceIdType);

                    if (bds == null || bds.size() <=0){

                        DeviceIdTypeParser parser = new DeviceIdTypeParser(deviceIdType);
                        int dt = bdb.getTypeMap().get(parser.getRawDeviceIdType());
                        bds = bdb.getDataMap().get(dt);

                        DeviceIdTypeParser that = new DeviceIdTypeParser(dt);
                        deviceId = parser.parseDeviceId(that, deviceId);
                    }

                    if (StringUtils.isNotBlank(deviceId)) {
                        for (BloomData bd : bds) {
                            if (bd.has(deviceId)) {
                                count++;
                                break;
                            }
                        }
                        System.out.print("\r..." + i++);
                    }
                }
            }
        }
        Long estimatedTime = System.currentTimeMillis() - startTime;
        System.out.println("path=" + path);
        System.out.println("\nget true count:" + count);
        System.out.printf("stream Diff: %d ms\n", estimatedTime);
    }

    public static BufferedReader getReader(File f) throws FileNotFoundException, IOException {
        BufferedReader reader = null;
        reader = new BufferedReader(new InputStreamReader(new FileInputStream(f)),1024*10*5);
        return reader;
    }

    public static class BloomDataBean{
        private Map<Integer,List<BloomData>> dataMap = new HashMap<Integer,List<BloomData>>();

        private Map<Integer,Integer> typeMap = new HashMap<Integer,Integer>();

        Map<Integer, List<BloomData>> getDataMap() {
            return dataMap;
        }

        public Map<Integer, Integer> getTypeMap() {
            return typeMap;
        }
    }



}
