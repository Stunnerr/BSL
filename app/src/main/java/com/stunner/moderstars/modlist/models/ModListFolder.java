package com.stunner.moderstars.modlist.models;

import com.bignerdranch.expandablerecyclerview.Model.ParentObject;
import com.stunner.moderstars.modlist.viewholders.FolderViewHolder;

import java.io.File;
import java.util.List;

/**
 * Created by reale on 23/11/2016.
 */

public class ModListFolder implements ParentObject {

    private List<Object> mChildrenList;
    private FolderViewHolder curholder;
    private String title;
    private File path;
    private int modn;

    public ModListFolder(File path, int modn) {
        this.title = path.getName();
        this.path = path;
        this.modn = modn - 1;
    }

    public File getPath() {
        return path;
    }

    public void setCurholder(FolderViewHolder curholder) {
        this.curholder = curholder;
    }

    public FolderViewHolder getCurholder() {
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
        return path.getAbsolutePath().split("com.stunner.moderstars/files/Mods/" + modn + "/")[path.toString().split("com.stunner.moderstars/files/Mods/" + modn + "/").length - 1];
    }
}