package com.home.picturepick.selectImage;

import java.util.ArrayList;

/**
 * author : CYS
 * e-mail : 1584935420@qq.com
 * date : 2020/9/17 15:38
 * desc :
 * version : 1.0
 */
public class ImageFolder {
    private String name;
    private String path;
    private String albumPath;
    private ArrayList<Image> images = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getAlbumPath() {
        return albumPath;
    }

    public void setAlbumPath(String albumPath) {
        this.albumPath = albumPath;
    }

    public ArrayList<Image> getImages() {
        return images;
    }

    public void setImages(ArrayList<Image> images) {
        this.images = images;
    }

    @Override
    public boolean equals(Object o) {
        if (o != null && o instanceof ImageFolder) {
            if (((ImageFolder) o).getPath() == null && path != null) return false;
            String oPath = ((ImageFolder) o).getPath().toLowerCase();
            return oPath.equals(this.path.toLowerCase());
        }
        return false;
    }
}
