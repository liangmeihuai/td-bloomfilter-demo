package com.talkingdata.mc.ds.algorithm;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;

import java.io.*;
import java.nio.charset.Charset;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class BloomData {
  private static Logger logger = LogManager.getLogManager().getLogger(BloomData.class.getName());

  private long count;// bloom文件存的count的数量
  private final static double DEFAULT_EMPLIFY_RATE = 0.01;
  private final static String DEFAULT_EMPLIFY_RATE_KEY = "DEFAULT_EMPLIFY_RATE";
  private BloomFilter<CharSequence> filter;

  public BloomFilter<CharSequence> getFilter() {
    return filter;
  }

  private BloomData(BloomFilter<CharSequence> bmfilter) {
    filter = bmfilter;
  }

  private BloomData() {}


  public long getCount() {
    return count;
  }

  public void setCount(long count) {
    this.count = count;
  }

  public boolean add(String str) {
    if (str != null && !str.trim().equals("")) {
      return filter.put(str);
    }
    return false;
  }

  public boolean has(String str) {
    if (str == null || str.trim().equals("")) {
      return false;
    } else {
      return filter.mightContain(str);
    }
  }

  public double getEmplifyRate() {
    return filter.expectedFpp();
  }

  public File save(String dir, String filename) throws IOException {
    if (filename != null && !filename.trim().equals("")) {
      File savedDir = new File(dir);
      if (savedDir.exists()) {
        if (savedDir.isDirectory()) {
          File datafile = new File(dir, filename);
          BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(datafile));
          filter.writeTo(bos);
          bos.flush();
          bos.close();
          return datafile;
        } else {
          throw new IOException("The specified directory is a file.");
        }
      } else {
        if (savedDir.mkdirs()) {
          File datafile = new File(dir, filename);
          filter.writeTo(new FileOutputStream(datafile));
          return datafile;
        } else {
          throw new IOException("Create directory failed.");
        }
      }
    } else {
      throw new IOException("Filename is null or empty");
    }
  }

  public static BloomData load(String dir, String filename) throws IOException {
    File file = new File(dir, filename);
    BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
    BloomFilter<CharSequence> bmfilter =
        BloomFilter.readFrom(bis, Funnels.stringFunnel(Charset.forName("UTF-8")));
    bis.close();
    return new BloomData(bmfilter);
  }

  public static BloomData load(InputStream is) throws IOException {
    BloomFilter<CharSequence> bmfilter =
        BloomFilter.readFrom(is, Funnels.stringFunnel(Charset.forName("UTF-8")));
    return new BloomData(bmfilter);
  }

  public static BloomData create(long expectedInsertions) {
    BloomFilter<CharSequence> bmfilter =
        BloomFilter.create(Funnels.stringFunnel(Charset.forName("UTF-8")), expectedInsertions, DEFAULT_EMPLIFY_RATE);
    return new BloomData(bmfilter);
  }

  public static BloomData create(long expectedInsertions, double emplifyRate) {
    BloomFilter<CharSequence> bmfilter = BloomFilter
        .create(Funnels.stringFunnel(Charset.forName("UTF-8")), expectedInsertions, emplifyRate);
    return new BloomData(bmfilter);
  }

}
