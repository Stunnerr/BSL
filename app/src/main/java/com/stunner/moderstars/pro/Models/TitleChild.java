package com.stunner.moderstars.pro.Models;

import com.stunner.moderstars.pro.ViewHolders.TitleChildViewHolder;

public class TitleChild {
    TitleChildViewHolder curholder;
    public String option1;
    public String path;
    public TitleChild(String path) {
        this.option1 = path.split("/")[path.split("/").length-1];
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

    public String getPath() {
        return path;
    }
}