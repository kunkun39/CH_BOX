package com.changhong.touying.file;

import java.io.Serializable;
import java.security.PublicKey;

/**
 * Created by Jack Wang
 */
public class FileItem implements Serializable {

    private String title; // PPT名称

    private String path;// PPT路径

    public FileItem(String title, String path) {
        this.title = title;
        this.path = path;
    }

    public FileItem(){
    	
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPath() {
        return path;
    }
    
    public void setPath(String path){
    	this.path = path;
    }

}
