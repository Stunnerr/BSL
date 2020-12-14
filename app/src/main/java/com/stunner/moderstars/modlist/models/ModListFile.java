package com.stunner.moderstars.modlist.models;

import com.stunner.moderstars.modlist.viewholders.FileViewHolder;

import java.io.File;

public class ModListFile {
    private FileViewHolder curholder;
    private String option1;
    public File path;
    private int modn;

    public ModListFile(File path, int modn) {
        this.option1 = path.getName();
        this.path = path;
        this.modn = modn - 1;
    }

    public FileViewHolder getCurholder() {
        return curholder;
    }

    public void setCurholder(FileViewHolder curholder) {
        this.curholder = curholder;
    }

    public String getOption1() {
        return option1;
    }

    public File getPath() {
        return path;
    }
    public String getforcopy(){
        return path.getAbsolutePath().split("com.stunner.moderstars/files/Mods/" + modn + "/")[path.toString().split("com.stunner.moderstars/files/Mods/" + modn + "/").length - 1];
    }
}