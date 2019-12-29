package com.stunner.moderstars.pro.Models;
import com.bignerdranch.expandablerecyclerview.Model.ParentObject;
import com.stunner.moderstars.pro.ViewHolders.TitleParentViewHolder;

import java.io.File;
import java.util.List;

/**
 * Created by reale on 23/11/2016.
 */

public class TitleParent implements ParentObject {

    private List<Object> mChildrenList;
    private TitleParentViewHolder curholder;
    private String title;
    private File path;
    public TitleParent(File path) {
        this.title = path.toString().split("/")[path.toString().split("/").length-1];
        this.path = path;
    }

    public File getPath() {
        return path;
    }

    public void setCurholder(TitleParentViewHolder curholder) {
        this.curholder = curholder;
    }

    public TitleParentViewHolder getCurholder() {
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
        return path.getAbsolutePath().split("com.stunner.moderstars/files/Mods" )[path.toString().split("com.stunner.moderstars/files").length-1];
    }
}