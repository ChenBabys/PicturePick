package com.home.picturepick.selectImage;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

/**
 * author : CYS
 * e-mail : 1584935420@qq.com
 * date : 2020/9/17 15:29
 * desc :
 * version : 1.0
 */
public class Image implements Parcelable {

    private int id;
    private String path;
    private String thumbPath;
    private boolean isSelect;
    private String folderName;
    private String name;
    private long date;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getThumbPath() {
        return thumbPath;
    }

    public void setThumbPath(String thumbPath) {
        this.thumbPath = thumbPath;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Image) {
            return this.path.equals(((Image) o).getPath());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPath());
    }

    public Image() {
    }

    protected Image(Parcel in) {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.path);
        dest.writeString(this.thumbPath);
        dest.writeByte((this.isSelect ? (byte) 1 : (byte) 0));
        dest.writeString(this.folderName);
        dest.writeString(this.name);
        dest.writeLong(this.date);
    }


//    public static final Creator<Image> CREATOR = new Creator<Image>() {
//        @Override
//        public Image createFromParcel(Parcel in) {
//            return new Image(in);
//        }
//
//        @Override
//        public Image[] newArray(int size) {
//            return new Image[size];
//        }
//    };

    /**
     * 醉了，上面的这种方式居然在intent传递过程中报错，只能用下面这种方式，但是为什么教程的代码可以呢，难道是因为api改了吗
     * 记住这个读和上面填的顺序要一致，否则报错
     */
    public static final Creator<Image> CREATOR = new Creator<Image>() {
        @Override
        public Image createFromParcel(Parcel in) {
            Image image = new Image();
            image.id = in.readInt();
            image.path = in.readString();
            image.thumbPath = in.readString();
            image.isSelect = in.readBoolean();
            image.folderName = in.readString();
            image.name = in.readString();
            image.date = in.readLong();
            return image;
        }

        @Override
        public Image[] newArray(int size) {
            return new Image[size];
        }
    };


}
