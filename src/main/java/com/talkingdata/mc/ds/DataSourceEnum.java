package com.talkingdata.mc.ds;

/**
 * Created by harlan on 2016/6/27.
 */
public enum DataSourceEnum {

  ASSIGN_DATA(1,"投放数据"),FEEDBACK_DATA(2,"回补数据");

  private DataSourceEnum(int id, String name){
    this.id = id;
    this.name=name;
  }

  private int id;
  private String name;

  public int toInt(){
    return id;
  }

  public String toName(){return name;}
  public static DataSourceEnum valueOf(int id){
    for (DataSourceEnum dse : DataSourceEnum.values()){
      if(dse.toInt() == id){
        return dse;
      }
    }
    return null;
  }



}
