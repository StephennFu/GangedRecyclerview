package com.fatchao.gangedrecyclerview.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by fuyuanyuan on 2018/3/10.
 */

public class SingleRowBean implements Parcelable {

  private String name;
  private String tag;
  private String imgsrc;

  public SingleRowBean(String name) {
    this.name = name;
  }

  protected SingleRowBean(Parcel in) {
    name = in.readString();
    tag = in.readString();
    imgsrc = in.readString();
  }

  public static final Creator<SingleRowBean> CREATOR = new Creator<SingleRowBean>() {
    @Override
    public SingleRowBean createFromParcel(Parcel in) {
      return new SingleRowBean(in);
    }

    @Override
    public SingleRowBean[] newArray(int size) {
      return new SingleRowBean[size];
    }
  };

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }


  public String getTag() {
    return tag;
  }

  public void setTag(String tag) {
    this.tag = tag;
  }

  public String getImgsrc() {
    return imgsrc;
  }

  public void setImgsrc(String imgsrc) {
    this.imgsrc = imgsrc;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(name);
    dest.writeString(tag);
    dest.writeString(imgsrc);
  }
}
