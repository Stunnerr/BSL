package com.stunner.moderstars;

import android.content.Context;

import com.stunner.moderstars.pro.Models.TitleParent;

import java.io.File;
import java.util.Arrays;

public class UsefulThings {

    public static File[] filelist(Context context, File folder){
        File[] dirs = folder.listFiles();
        Arrays.sort(dirs);
        return dirs;

    }
    public static int modcount(Context context){ return new File (context.getExternalFilesDir(null)+ "/Mods").listFiles().length;}

    public static File[] checkmods(Context context, int modn){
        String d = context.getExternalFilesDir(null) + "/Mods" + modn;
        new File(d).mkdir();
        File[] mods = new File(d).listFiles();
        Arrays.sort(mods);
        return mods;
    }
    public static File[] checkmods(Context context){
        String d = context.getExternalFilesDir(null) + "/Mods";
        new File(d).mkdir();
        File[] mods = new File(d).listFiles();
        for (File i: mods)i.renameTo(new File(i.toString().toLowerCase()));
        mods = new File(d).listFiles();
        Arrays.sort(mods);
        return mods;
    }

}
