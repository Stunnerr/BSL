package com.stunner.moderstars.pro.Models;

import com.stunner.moderstars.pro.ViewHolders.ChildViewHolder;

import java.io.File;

public class ListChild {
    private ChildViewHolder curholder;
    private String option1;
    public File path;
    private int modn;
    public ListChild(File path, int modn) {
        this.option1 = path.getName();
        this.path = path;
        this.modn = modn-1;
    }

    public ChildViewHolder getCurholder() {
        return curholder;
    }

    public void setCurholder(ChildViewHolder curholder) {
        this.curholder = curholder;
    }

    public String getOption1() {
        return option1;
    }

    public File getPath() {
        return path;
    }
    public String getforcopy(){
        String p ="/"+path.getAbsolutePath().split("com.stunner.moderstars/files/Mods/"+modn+"/")[path.toString().split("com.stunner.moderstars/files/Mods/"+modn+"/").length-1];
        return p;
    }
}