package com.talkingdata.mc.ds.algorithm;

import com.google.gson.Gson;
import com.talkingdata.mc.ds.DeviceIdType;
import org.apache.commons.lang.StringUtils;

import java.security.MessageDigest;

/**
 *  <br>根据设计传入的DeviceIdType转成二进制后如下：
 *  <br>x x x x | x x x x
 *  <br>1 2 3 4   5 6 7 8
 *  
 *  <li>4 －－ 是否加密 （1:加密，0:未加密）
 *  <li>3 －－ 加密算法 （1:MD5， 0:SHA1）
 *  <li>2 －－ 明文大小写 （1:大写，0:小写）
 *  <li>1 －－ 加密后大小写 （1:大写，0:小写）
 *   public static int  parseWoleDeviceIdType(int deviceIdType,boolean encrypted,boolean md5,boolean rawUpper,boolean encryptUpper) {
 int k = 0x00;
 k |= encryptUpper ? 0x80 : 0x00;
 k |= rawUpper ? 0x40 : 0x00;
 k |= md5 ? 0x20 : 0x00;
 k |= encrypted ? 0x10 : 0x00;
 k |= deviceIdType;
 return k;
 }
 *
 *  {"wholeDeviceIdType":178,"deviceIdType":2,"encrypted":true,"md5":true,"rawUpper":false,"encryptUpper":true}
 *  <li>5,6,7,8 -- 原始IdType
 *  
 *  <br>
 * @author xiaolongli
 *
 */
public class DeviceIdTypeParser {
  
  private int wholeDeviceIdType;
  private int deviceIdType; //后四位，原始的IdType
  private boolean encrypted;
  private boolean md5;
  private boolean rawUpper;
  private boolean encryptUpper;
  public DeviceIdTypeParser(int wholeDeviceIdType) {
    this.wholeDeviceIdType = wholeDeviceIdType;
    this.deviceIdType = wholeDeviceIdType & 15; //00001111
    this.encrypted = (wholeDeviceIdType & 0x10) > 0 ? true : false;
    this.md5 = (wholeDeviceIdType & 0x20) > 0 ? true : false;
    this.rawUpper = (wholeDeviceIdType & 0x40) > 0 ? true : false;
    this.encryptUpper = (wholeDeviceIdType & 0x80) > 0 ? true : false;
  }

  public boolean isEncrypted() {
    return this.encrypted;
  }
  
  public boolean isMD5() {
    return this.md5;
  }

  public boolean isRawUpper() {
    return this.rawUpper;
  }
  
  public boolean isEncryptUpper() {
    return this.encryptUpper;
  }
  
  public int getRawDeviceIdType() {
    return this.deviceIdType;
  }

  /**
   *
   * @param deviceIdType
   * @param encrypted 是否加密
   * @param md5 md5 时为ture ,sha-1 为 false
   * @param rawUpper 原文是否大写
   * @param encryptUpper 密文是否大写
   * @return
   */
  public static int  parseWoleDeviceIdType(int deviceIdType,boolean encrypted,boolean md5,boolean rawUpper,boolean encryptUpper) {
    int k = 0x00;
    k |= encryptUpper ? 0x80 : 0x00;
    k |= rawUpper ? 0x40 : 0x00;
    k |= md5 ? 0x20 : 0x00;
    k |= encrypted ? 0x10 : 0x00;
    k |= deviceIdType;
    return k;
  }
  
  public String parseDeviceId(DeviceIdTypeParser that, String deviceId) {
    if(this.isEncrypted()) {
      if(!that.isEncrypted() || !(this.isMD5() == that.isMD5()) || !(this.isRawUpper() == that.isRawUpper()))
        return null;
      return that.isEncryptUpper() ? deviceId.toUpperCase() : deviceId.toLowerCase();
    } else {
      String newDeviceId = deviceId;
      
      newDeviceId = that.isRawUpper() ? newDeviceId.toUpperCase() : newDeviceId.toLowerCase();
      if(that.isEncrypted()) {
        newDeviceId = that.isMD5() ? MD5(newDeviceId) : SHA1(newDeviceId);
        newDeviceId = that.isEncryptUpper() ? newDeviceId.toUpperCase() : newDeviceId.toLowerCase();
      }
      
      return newDeviceId;
    }
  }
  
  public  String MD5(String text) {
    MessageDigest md;
    byte[] md5 = new byte[64];
    try {
      md = MessageDigest.getInstance("MD5");
      md.update(text.getBytes("iso-8859-1"), 0, text.length());
      md5 = md.digest();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return convertedToHex(md5);
  }

  public  static  String MD5Static(String text) {
    MessageDigest md;
    byte[] md5 = new byte[64];
    try {
      md = MessageDigest.getInstance("MD5");
      md.update(text.getBytes("iso-8859-1"), 0, text.length());
      md5 = md.digest();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return convertedToHex(md5);
  }
  
  public String SHA1(String text) {
    MessageDigest md;
    byte[] sha1 = new byte[64];
    try {
      md = MessageDigest.getInstance("SHA1");
      md.update(text.getBytes("iso-8859-1"), 0, text.length());
      sha1 = md.digest();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return convertedToHex(sha1);
  }

  private static String convertedToHex(byte[] data) {
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


  @Override
  public String toString() {
    return "DeviceIdTypeParser:[input IDType: " + wholeDeviceIdType + "; deviceIdType:" + deviceIdType +"]";
  }


  public static void main(String[] args) {

//    System.out.println("66=" + StringUtils.leftPad(Integer.toBinaryString(66),8,'0'));
//    System.out.println("android:4="+StringUtils.leftPad(Integer.toBinaryString(DeviceIdType.AndroidID.getId()),8,'0'));
//
//    System.out.println(DeviceIdTypeParser.parseWoleDeviceIdType(DeviceIdType.AndroidID.getId(),false,false,true,false) + "=" +StringUtils.leftPad(Integer.toBinaryString(DeviceIdTypeParser.parseWoleDeviceIdType(DeviceIdType.AndroidID.getId(),false,false,true,false)),8,'0'));

    System.out.println(DeviceIdTypeParser.parseWoleDeviceIdType(DeviceIdType.MAC.getId(),false,false,true,false) == 70);
    System.out.println(StringUtils.leftPad(Integer.toBinaryString(214),8,'0'));
    System.out.println(DeviceIdTypeParser.parseWoleDeviceIdType(DeviceIdType.MAC.getId(),true,true,true,true) == 246);
    System.out.println(DeviceIdTypeParser.parseWoleDeviceIdType(DeviceIdType.MAC.getId(),true,false,true,true) == 214);

    System.out.println(DeviceIdTypeParser.parseWoleDeviceIdType(DeviceIdType.IDFA.getId(),false,false,true,false) == 67);
    System.out.println(DeviceIdTypeParser.parseWoleDeviceIdType(DeviceIdType.IDFA.getId(),true,true,true,true) == 243);
    System.out.println(DeviceIdTypeParser.parseWoleDeviceIdType(DeviceIdType.IDFA.getId(),true,false,true,true) == 211);


    System.out.println(DeviceIdTypeParser.parseWoleDeviceIdType(DeviceIdType.AndroidID.getId(),true,false,false,false) == 66);
    System.out.println(DeviceIdTypeParser.parseWoleDeviceIdType(DeviceIdType.AndroidID.getId(),true,true,true,true) == 244);
    System.out.println(DeviceIdTypeParser.parseWoleDeviceIdType(DeviceIdType.AndroidID.getId(),true,false,true,true) == 212);

    System.out.println(DeviceIdTypeParser.parseWoleDeviceIdType(DeviceIdType.IMEI.getId(),false,false,false,false) == 2);
    System.out.println(DeviceIdTypeParser.parseWoleDeviceIdType(DeviceIdType.IMEI.getId(),true,true,false,true) == 178);
    System.out.println(DeviceIdTypeParser.parseWoleDeviceIdType(DeviceIdType.IMEI.getId(),true,false,false,true) == 146);


    DeviceIdTypeParser deviceIdTypeParser=new DeviceIdTypeParser(178);
    System.out.println(new Gson().toJson(deviceIdTypeParser));//{"wholeDeviceIdType":67,"deviceIdType":3,"encrypted":false,"md5":false,"rawUpper":true,"encryptUpper":false}

    System.out.println("-------");
    System.out.println(DeviceIdTypeParser.parseWoleDeviceIdType(DeviceIdType.AndroidID.getId(),false,false,false,false));


    System.out.println("imeidedede="+DeviceIdTypeParser.parseWoleDeviceIdType(DeviceIdType.IMEI.getId(),true,true,false,true));

    String imeimd5Upper=MD5Static("864738038260428").toUpperCase();
    String idfaUpper=MD5Static("944eb571-3d91-4d25-b46b-f368dfd404af".toUpperCase()).toUpperCase();
    String macUpper=MD5Static("00:08:22:32:47:b6".toUpperCase()).toUpperCase();
    System.out.println("imeimd5Upper="+imeimd5Upper);
    System.out.println("idfaUpper="+idfaUpper);
    System.out.println("macUpper="+macUpper);

  }
}
