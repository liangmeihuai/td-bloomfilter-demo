package com.talkingdata.mc.ds;

import java.io.Serializable;
import java.util.Set;

public class Audience implements Serializable {

	private static final long serialVersionUID = 1L;

	private int audienceId;
	private int audienceType = AudienceType.PRIVATE;
//	private String audienceName;
	private long count;
	private int deviceIdType;
	private long start;
	private long end;
	private String deviceIdFile;
    private Set<Integer> segmentIds;

  // private List<Map<String, Object>> list ;

  // public List<Map<String, Object>> getList() {
  // return list;
  // }
  //
  // public void setList(List<Map<String, Object>> list) {
  // this.list = list;
  // }

	public int getAudienceId() {
		return audienceId;
	}

	public void setAudienceId(int audienceId) {
		this.audienceId = audienceId;
	}

	public int getAudienceType() {
		return audienceType;
	}

	public void setAudienceType(int audienceType) {
		this.audienceType = audienceType;
	}

	public int getDeviceIdType() {
		return deviceIdType;
	}

	public void setDeviceIdType(int deviceIdType) {
		this.deviceIdType = deviceIdType;
	}

	public long getStart() {
		return start;
	}

	public void setStart(long start) {
		this.start = start;
	}

	public long getEnd() {
		return end;
	}

	public void setEnd(long end) {
		this.end = end;
	}

	public String getDeviceIdFile() {
		return deviceIdFile;
	}

	public void setDeviceIdFile(String deviceIdFile) {
		this.deviceIdFile = deviceIdFile;
	}

//	public String getAudienceName() {
//		return audienceName;
//	}
//
//	public void setAudienceName(String audienceName) {
//		this.audienceName = audienceName;
//	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

  public Set<Integer> getSegmentIds() {
    return segmentIds;
  }

  public void setSegmentIds(Set<Integer> segmentIds) {
    this.segmentIds = segmentIds;
  }

	@Override
	public String toString() {
		return "Audience{" +
				"audienceId=" + audienceId +
				", audienceType=" + audienceType +
				", count=" + count +
				", deviceIdType=" + deviceIdType +
				", start=" + start +
				", end=" + end +
				", deviceIdFile='" + deviceIdFile + '\'' +
				", segmentIds=" + segmentIds +
				'}';
	}
}
