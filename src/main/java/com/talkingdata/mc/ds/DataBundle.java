package com.talkingdata.mc.ds;

import com.google.gson.*;
import com.talkingdata.mc.ds.algorithm.BloomData;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class DataBundle implements BundleConstants {

  private static Logger logger = LoggerFactory.getLogger(DataBundle.class);

  private String mediaId;
  private String campaignId;
  private List<Map<String, String>> subCampaigns = new ArrayList<Map<String, String>>();
  private String callbackURL;
  private Integer switchFlag =1;
  private List<Audience> audienceList = new ArrayList<Audience>();
  private Integer dataSource = DataSourceEnum.ASSIGN_DATA.toInt();// 数据来源1投放数据，2回补数据
  private final static int BUFFER = 2048;

  public Integer getSwitchFlag() {
    return switchFlag;
  }

  public void setSwitchFlag(Integer switchFlag) {
    this.switchFlag = switchFlag;
  }

  public List<Audience> getAudienceList() {
    return audienceList;
  }

  public Integer getDataSource() {
    return dataSource;
  }

  public void setDataSource(Integer dataSource) {
    this.dataSource = dataSource;
  }

  public DataBundle() {}

  public void setAudienceList(List<Audience> audienceList) {
    this.audienceList = audienceList;
  }

  public DataBundle(String mediaId, String campaignId) {
    this.mediaId = mediaId;
    this.campaignId = campaignId;
  }

  public String getMediaId() {
    return mediaId;
  }

  public void setMediaId(String mediaId) {
    this.mediaId = mediaId;
  }

  public String getCampaignId() {
    return campaignId;
  }

  public void setCampaignId(String campaignId) {
    this.campaignId = campaignId;
  }

  public List<Map<String, String>> getSubCampaigns() {
    return subCampaigns;
  }

  public void setSubCampaigns(List<Map<String, String>> subCampaigns) {
    this.subCampaigns = subCampaigns;
  }

  public String getCallbackURL() {
    return callbackURL;
  }

  public void setCallbackURL(String callbackURL) {
    this.callbackURL = callbackURL;
  }

  public void addAll(List<Audience> list) {
    if (CollectionUtils.isNotEmpty(list)) {
      audienceList.addAll(list);
    }
  }

  public void addCampaigns(List<Map<String, String>> campaigns) {
    this.subCampaigns.addAll(campaigns);
  }

  public void addSubCampaign(Map<String, String> subCampaign) {
//    if (MapUtils.isNotEmpty(subCampaign)) {
      this.subCampaigns.add(subCampaign);
//    }
  }

  public void clearSubCampaigns() {
    subCampaigns.clear();
  }

  public void add(Audience au) {
    if (au != null) {
      audienceList.add(au);
    }
  }

  public List<Audience> listAudience() {
    return audienceList;
  }

  public List<Map<String, String>> listSubCampaigns() {
    return subCampaigns;
  }

  public void clearAudience() {
    audienceList.clear();
  }

  // public void initialize() {
  // mediaId = null;
  // campaignId = null;
  // clearAudience();
  // }

  public String reloadBundleIndexString() {
    return generateBundleString(true).toString();
  }

  public JsonObject generateBundleString(boolean reload) {
    JsonObject bundleObj = new JsonObject();

    bundleObj.addProperty(KEY_MEDIA_ID, mediaId);
    bundleObj.addProperty(KEY_CAMPAIGN_ID, campaignId);
    bundleObj.addProperty(KEY_CALLBACK_URL, this.callbackURL);
    bundleObj.addProperty(KEY_DATA_SOURCE, this.dataSource);
    bundleObj.addProperty(KEY_SWITCH_FLAG, this.switchFlag);

    if (subCampaigns.size() > 0) {
      JsonArray jsonArray = new JsonArray();
      for (Map<String, String> map : subCampaigns) {
        JsonObject subObj = new JsonObject();
        subObj.addProperty(KEY_MEDIA_ID, map.get(KEY_MEDIA_ID));
        subObj.addProperty(KEY_CAMPAIGN_ID, map.get(KEY_CAMPAIGN_ID));
        jsonArray.add(subObj);
      }
      if (jsonArray.size() > 0) {
        bundleObj.add(KEY_SUB_CAMPAIGNS, jsonArray);
      }
    }

    JsonArray audiences = new JsonArray();

    for (Audience au : audienceList) {
      JsonObject audience = new JsonObject();
      audience.addProperty(KEY_AUDIENCE_ID, au.getAudienceId());
      audience.addProperty(KEY_AUDIENCE_TYPE, au.getAudienceType());

      audience.addProperty(KEY_AUDIENCE_COUNT, au.getCount());
      audience.addProperty(KEY_DEVICE_ID_TYPE, au.getDeviceIdType());
      audience.addProperty(KEY_START_TIME, au.getStart());
      audience.addProperty(KEY_END_TIME, au.getEnd());

      if (au.getSegmentIds() != null) {
        JsonArray jSegIds = new JsonArray();
        for (int segId : au.getSegmentIds()) {
          jSegIds.add(new JsonPrimitive(segId));
        }
        audience.add(KEY_SEGMENT_IDS, jSegIds);
      }

      if (reload) {
        audience.addProperty(KEY_DEVICE_IDS, au.getDeviceIdFile());
      } else {
        String filepath = au.getDeviceIdFile();
        File file = new File(filepath);
        audience.addProperty(KEY_DEVICE_IDS, file.getName());
      }

      audiences.add(audience);
    }
    bundleObj.add(KEY_AUDIENCES, audiences);

    return bundleObj;
  }

  public File generateDataBundle(String dir) throws IOException {
    if (dir != null && !dir.endsWith("/")) {
      dir = dir + "/";
    }
    File fileDir = new File(dir);
    if (fileDir.exists()) {
      fileDir.mkdirs();
    }
    if (fileDir.exists() && fileDir.isDirectory()) {
      try {
        JsonObject bundleObj = generateBundleString(false);
        String tmpDir = mediaId + "_" + campaignId;
        logger.info("campaignId={}, bundle info:{}", campaignId, new Gson().toJson(bundleObj));
        File bundleIndexFile = generateBundleIndex(dir + tmpDir, bundleObj.toString());

        File bundleFile = zip(dir, tmpDir + ".zip", bundleIndexFile);
        return bundleFile;
      } catch (IOException jsone) {
        throw new IOException("Composing bundle json failed: " + jsone.toString());
      }
    } else {
      throw new IOException("Invalid directory");
    }
  }

  private File generateBundleIndex(String dir, String bundleObj) throws IOException {

    File bundleIndexDir = new File(dir);
    if (!bundleIndexDir.exists()) {
      bundleIndexDir.mkdirs();
    }
    File bundleIndexFile = new File(dir, BUNDLE_FILE_NAME);
    FileWriter writer = null;
    try {
      writer = new FileWriter(bundleIndexFile);
      writer.write(bundleObj);
      writer.flush();
    } catch (IOException e) {
      throw new IOException("Create bundle index file faild: " + e.toString());
    } finally {
      if (writer != null) {
        try {
          writer.close();
        } catch (IOException e) {}
      }
    }
    return bundleIndexFile;
  }

  private File zip(String dir, String zipName, File bundleIndexFile) throws IOException {
    File zipFile = new File(dir, zipName);
    ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile));
    BufferedOutputStream bos = new BufferedOutputStream(out);

    // zip bundle index file
    out.putNextEntry(new ZipEntry(bundleIndexFile.getName()));
    BufferedInputStream bisIndex = new BufferedInputStream(new FileInputStream(bundleIndexFile));
    byte data[] = new byte[BUFFER];
    int count;
    while ((count = bisIndex.read(data, 0, BUFFER)) != -1) {
      bos.write(data, 0, count);
    }

    bisIndex.close();
    bos.flush();

    // zip audience files
    for (Audience au : audienceList) {
      String auFileName = au.getDeviceIdFile();
      File auFile = new File(auFileName);
      out.putNextEntry(new ZipEntry(auFile.getName()));
      BufferedInputStream bisAu = new BufferedInputStream(new FileInputStream(auFile));
      data = new byte[BUFFER];
      while ((count = bisAu.read(data, 0, BUFFER)) != -1) {
        bos.write(data, 0, count);
      }

      bos.flush();
      bisAu.close();
    }
    bos.close();

    return zipFile;
  }

  public BloomData get(int audienceId, File bundleFile, Integer idType) throws IOException {
    if (bundleFile.exists() && bundleFile.isFile()) {
      ZipFile zipFile = new ZipFile(bundleFile);
      String auFile = "";
      for (Audience au : audienceList) {
        if (au.getAudienceId() == audienceId && au.getDeviceIdType() == idType) {
          auFile = au.getDeviceIdFile();
          break;
        }
      }

      if (auFile != null && !auFile.trim().equals("")) {
        ZipEntry auEntry = zipFile.getEntry(auFile);
        BufferedInputStream bis = new BufferedInputStream(zipFile.getInputStream(auEntry));
        BloomData bd = BloomData.load(bis);
        bis.close();
        zipFile.close();
        return bd;
      } else {
        zipFile.close();
        throw new IOException("Unable to find audience file with audience ID: " + audienceId);
      }
    } else {
      throw new IOException("Bundle file not exists or audienceId is null");
    }
  }

  public void loadBundleIndex(File bundleFile) throws IOException {
    if (bundleFile.exists() && bundleFile.isFile()) {
      ZipFile zipFile = new ZipFile(bundleFile);
      ZipEntry bundleIndex = zipFile.getEntry(BUNDLE_FILE_NAME);
      BufferedReader br =
          new BufferedReader(new InputStreamReader(zipFile.getInputStream(bundleIndex)));
      StringBuffer buffer = new StringBuffer();
      String s;
      while ((s = br.readLine()) != null) {
        buffer.append(s);
      }

      zipFile.close();
      br.close();
      // 将dataBundle中的audiences中的数据清空
      clearAudience();
      // 将dataBundle中的subCampaigns中的数据清空
      clearSubCampaigns();

      try {

        JsonObject biObj = new JsonParser().parse(buffer.toString()).getAsJsonObject();
        logger.info("biObj.....loadBundleIndex.... is {}", buffer.toString());
        mediaId = biObj.get(KEY_MEDIA_ID).getAsString();// 这里的mediaId就是channelId啦
        campaignId = biObj.get(KEY_CAMPAIGN_ID).getAsString();
        if(biObj.get(KEY_SWITCH_FLAG) != null){
          switchFlag = biObj.get(KEY_SWITCH_FLAG).getAsInt();
        }else {
          switchFlag = 1; //默认是可以查询的
        }
        // 看下是否有subcampaigns
        if (biObj.has(KEY_SUB_CAMPAIGNS)) {
          // 这里是往dataBundle里面的subCampaigns添加属性
          JsonArray subCampaignArray = biObj.getAsJsonArray(KEY_SUB_CAMPAIGNS);
          if (subCampaignArray != null) {
            for (int i = 0; i < subCampaignArray.size(); i++) {
              JsonObject subCampaignObj = subCampaignArray.get(i).getAsJsonObject();
              Map<String, String> subCampaign = new HashMap<String,String>();
              subCampaign.put("MediaId", subCampaignObj.get("MediaId").getAsString());
              subCampaign.put("CampaignId", subCampaignObj.get("CampaignId").getAsString());
              addSubCampaign(subCampaign);
            }
          }
        } else {// 兼容td检测的老数据
          Map<String, String> subCampaign = new HashMap<String,String>();
          subCampaign.put("MediaId", mediaId);
          subCampaign.put("CampaignId", campaignId);
          addSubCampaign(subCampaign);
        }
        // 看是否有KEY_CALLBACK_URL[回调的url]
        if (biObj.has(KEY_CALLBACK_URL)) {
          callbackURL = biObj.get(KEY_CALLBACK_URL).getAsString();
          logger.info("GET the callbackURL is {}...", callbackURL);
        }

        // 看是否有数据来源dataSource
        if (biObj.has(KEY_DATA_SOURCE)) {
          dataSource = biObj.get(KEY_DATA_SOURCE).getAsInt();
          logger.info("GET the dataSource is {}...", dataSource);
        }

        JsonArray auArray = biObj.getAsJsonArray(KEY_AUDIENCES);
        for (int i = 0; i < auArray.size(); i++) {
          JsonObject auObj = auArray.get(i).getAsJsonObject();
          Audience au = new Audience();
          au.setAudienceId(auObj.get(KEY_AUDIENCE_ID).getAsInt());
          // au.setAudienceName(auObj.getString(KEY_AUDIENCE_NAME));
          au.setAudienceType(auObj.get(KEY_AUDIENCE_TYPE).getAsInt());
          au.setCount(auObj.get(KEY_AUDIENCE_COUNT).getAsLong());
          au.setDeviceIdFile(auObj.get(KEY_DEVICE_IDS).getAsString());
          au.setDeviceIdType(auObj.get(KEY_DEVICE_ID_TYPE).getAsInt());
          if (auObj.has(KEY_END_TIME)) {
            au.setEnd(auObj.get(KEY_END_TIME).getAsLong());
          } else {
            au.setEnd(-1);
          }

          if (auObj.has(KEY_START_TIME)) {
            au.setStart(auObj.get(KEY_START_TIME).getAsLong());
          } else {
            au.setStart(-1);
          }
          if (auObj.has(KEY_SEGMENT_IDS)) {
            Set<Integer> segmemtIds = new HashSet<Integer>();
            Iterator<JsonElement> iterator = auObj.get(KEY_SEGMENT_IDS).getAsJsonArray().iterator();
            while (iterator.hasNext()) {
              JsonElement jsonElement = iterator.next();
              segmemtIds.add(jsonElement.getAsInt());
            }
            au.setSegmentIds(segmemtIds);
          }


          add(au);
        }
      } catch (Exception e) {
        throw new IOException("Invalid bundle index file: " + e.toString());
      }

    } else {
      throw new IOException("Bundle file not exists");
    }
  }

  public void updateZipFile(String filePath) throws Exception {
    String tmpFilePath = filePath + ".closed";
    File file = new File(tmpFilePath);
    if (file.exists()) {
      file.delete();
    }

    ZipFile zipFile = new ZipFile(filePath);
    FileOutputStream outputStream = new FileOutputStream(tmpFilePath);
    ZipOutputStream zos = new ZipOutputStream(outputStream);

    for (Enumeration<? extends ZipEntry> e = zipFile.entries(); e.hasMoreElements();) {
      ZipEntry entry = e.nextElement();
      if (!entry.getName().equalsIgnoreCase(BUNDLE_FILE_NAME)) {
        // 生成新的entry，预防=>ZipException: invalid entry compressed size (expected 217193 but got 217228
        // bytes)
        entry = new ZipEntry(entry.getName());
        zos.putNextEntry(entry);
        InputStream is = zipFile.getInputStream(entry);
        byte[] buf = new byte[1024];
        int len;
        while ((len = is.read(buf)) > 0) {
          zos.write(buf, 0, len);
        }
      } else {
        zos.putNextEntry(new ZipEntry(BUNDLE_FILE_NAME));

        InputStream is = zipFile.getInputStream(entry);
        long size = entry.getSize();
        byte[] bytes = new byte[(int) size];
        is.read(bytes);

        zos.write(reloadBundleIndexString().getBytes());
      }
      zos.closeEntry();
    }
    zos.close();
    outputStream.close();
    zipFile.close();

    File newFile = new File(tmpFilePath);
    File oldFile = new File(filePath);
    newFile.renameTo(oldFile);
  }



}
