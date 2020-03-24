package com.stunner.moderstars.pro.Models;
import com.bignerdranch.expandablerecyclerview.Model.ParentObject;
import com.stunner.moderstars.pro.ViewHolders.ParentViewHolder;

import java.io.File;
import java.util.List;

/**
 * Created by reale on 23/11/2016.
 */

public class ListParent implements ParentObject {

    private List<Object> mChildrenList;
    private ParentViewHolder curholder;
    private String title;
    private File path;
   private int modn;
    public ListParent(File path, int modn) {
        this.title = path.getName();
        this.path = path;
        this.modn= modn-1;
    }
    public File getPath() {
        return path;
    }

    public void setCurholder(ParentViewHolder curholder) {
        this.curholder = curholder;
    }

    public ParentViewHolder getCurholder() {
        return curholder;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public List<Object> getChildObjectList() {
        return mChildrenList;
    }

    @Override
    public void setChildObjectList(List<Object> list) {
        mChildrenList = list;
    }
    public String getforcopy(){
        return "/" + path.getAbsolutePath().split("com.stunner.moderstars/files/Mods/" + modn + "/")[path.toString().split("com.stunner.moderstars/files/Mods/" + modn + "/").length - 1];
    }
}