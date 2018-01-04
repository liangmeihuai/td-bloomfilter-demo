package com.talkingdata.mc.ds;

public enum DeviceIdType {
	
	TDID("TDID",1,"TDID"),
	IMEI("IMEI",2,"IMEI"),
	IDFA("IDFA",3,"IDFA"),
	AndroidID("AndroidID",4,"AndroidID"),
	PHONEMD5("PHONENOMD5",5,"PHONENOMD5"),
	MAC("MAC",6,"MAC"),
	OFFSET("OFFSETID",7,"OFFSETID"),
	/**下面是明文大写MD5编码*/
	IMEIUpMD5("IMEIUpMD5",8,"imei"),
	IDFAUpMD5("IDFAUpMD5",9,"idfa"),
	AndroidIDUpMD5("AndroidIDUpMD5",10,"androidid"),
	MACUpMD5("MACUpMD5",11,"mac"),
	;
	
	private Integer id;
	private String key;
	private String purekey;

	DeviceIdType( String key,Integer id, String purekey) {
		this.id = id;
		this.key = key;
		this.purekey = purekey;
	}

	private DeviceIdType(String key, Integer id){
		this.id = id;
		this.key = key;
	}
	
	public String getKey() {
		return key;
	}

	public Integer getId() {
		return id;
	}

	public String getPurekey() {
		return purekey;
	}

	public static boolean isValid(int idType) {
		boolean result = false;
		for(DeviceIdType dt : DeviceIdType.values()){
			if(dt.getId() == dt.getId()){
				result = true;
				break;
			}
		}
		return result;
	}

    public static DeviceIdType valueOfObjectName(String objectName){
        for (DeviceIdType deviceIdType : DeviceIdType.values()){
            if(deviceIdType.getPurekey().equalsIgnoreCase(objectName)){
                return deviceIdType;
            }
        }
        return null;
    }
	
	public static DeviceIdType parse (int idType) {
		for(DeviceIdType dt : DeviceIdType.values()){
			if(dt.getId() == idType){
				return dt;
			}
		}
		return null;
	}
	
	public static void main(String... args){
		for(int i = 1 ;i<=11;i++){
			DeviceIdType dt = DeviceIdType.parse(i);
			if(null != dt)
			System.out.println(dt.getKey());
		}
	}
	
}
