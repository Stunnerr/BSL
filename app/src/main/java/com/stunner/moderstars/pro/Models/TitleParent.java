package com.stunner.moderstars.pro.Models;
import java.util.List;
import java.util.UUID;

import com.bignerdranch.expandablerecyclerview.Model.ParentObject;
import com.stunner.moderstars.pro.ViewHolders.TitleParentViewHolder;

/**
 * Created by reale on 23/11/2016.
 */

public class TitleParent implements ParentObject {

    private List<Object> mChildrenList;
    TitleParentViewHolder curholder;
    private UUID _id;
    private String title;
    private String path;
    public TitleParent(String path) {
        this.title = path.split("/")[path.split("/").length-1];
        this.path = path;
        _id = UUID.randomUUID();
    }

    public String getPath() {
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
}