package com.stunner.moderstars.pro.Models;

import com.stunner.moderstars.pro.ViewHolders.TitleChildViewHolder;

import java.io.File;

public class TitleChild {
    private TitleChildViewHolder curholder;
    private String option1;
    public File path;
    public TitleChild(File path) {
        this.option1 = path.toString().split("/")[path.toString().split("/").length-1];
        this.path=path;
    }

    public TitleChildViewHolder getCurholder() {
        return curholder;
    }

    public void setCurholder(TitleChildViewHolder curholder) {
        this.curholder = curholder;
    }

    public String getOption1() {
        return option1;
    }

    public File getPath() {
        return path;
    }
    public String getforcopy(){
        return path.getAbsolutePath().split("com.stunner.moderstars/files/Mods")[path.toString().split("com.stunner.moderstars/files/Mods").length-1];
    }
}