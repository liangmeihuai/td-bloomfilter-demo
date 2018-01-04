package com.talkingdata.mc.ds;

public enum ExtDeviceIdType {

	IMEI("IMEI",2),
	IDFA("IDFA",67),
	AndroidID("AndroidID",68),
	MAC("MAC",70),
	IMEI_MD5("IMEI_MD5",242),
	IDFA_MD5("IDFA_MD5",243),
	AndroidID_MD5("AndroidID_MD5",180),
	MAC_MD5("MAC_MD5",246),
	PHONE_MD5("PHONE_MD5",53);
	
	private Integer id;
	private String key;
	
	private ExtDeviceIdType(String key,Integer id){
		this.id = id;
		this.key = key;
	}
	
	public String getKey() {
		return key;
	}

	public Integer getId() {
		return id;
	}


	public static boolean isValid(int idType) {
		boolean result = false;
		for(ExtDeviceIdType dt : ExtDeviceIdType.values()){
			if(dt.getId() == dt.getId()){
				result = true;
				break;
			}
		}
		return result;
	}
	
	
	public static ExtDeviceIdType parse (int idType) {
		for(ExtDeviceIdType dt : ExtDeviceIdType.values()){
			if(dt.getId() == idType){
				return dt;
			}
		}
		return null;
	}
	
	public static void main(String... args){
		for(int i = 0 ;i< 7;i++){
			ExtDeviceIdType dt = ExtDeviceIdType.parse(i);
			if(null != dt)
			System.out.println(dt.getKey());
		}
	}
	
}
