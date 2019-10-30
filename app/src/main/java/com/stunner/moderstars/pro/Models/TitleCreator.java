package com.stunner.moderstars.pro.Models;

import android.content.Context;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TitleCreator {

    private static List<TitleCreator> _titleCreator = new ArrayList<>();
    private List<TitleParent> _titleParents;

    private TitleCreator(Context context, String folder) {
        _titleParents = new ArrayList<>();
        TitleParent title = new TitleParent(folder);
        _titleParents.add(title);

    }
    public static File[] filelist(Context context, File folder){
        File[] dirs = folder.listFiles();
        Arrays.sort(dirs);
        return dirs;

    }
    public static File[] checkmods(Context context){
        String d = context.getExternalFilesDir(null) + "/Mods";
        new File(d).mkdir();
        File[] mods = new File(d).listFiles();
        Arrays.sort(mods);
        return mods;
    }

    public static List<TitleCreator> get(Context context) {
            _titleCreator.clear();
            try {
                List <String> files = new ArrayList<>();
                for(File i: checkmods(context)) files.add(i.toString());
                if (files.size() != 0)
                    for (String j : files) {
                        _titleCreator.add(new TitleCreator(context, j.split("/")[j.split("/").length-1]));
                    }
                else {
                    Toast.makeText(context, "Не загруэено ни одной модификации! Нажмите на кнопку внизу слева для загрузки", Toast.LENGTH_LONG).show();
                }
            }
            catch (Exception e) {

            }

        return _titleCreator;
    }

    public List<TitleParent> getAll() {
        return _titleParents;
    }
}