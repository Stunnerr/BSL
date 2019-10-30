package com.stunner.moderstars;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bignerdranch.expandablerecyclerview.Model.ParentObject;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.stunner.moderstars.pro.Models.TitleChild;
import com.stunner.moderstars.pro.Models.TitleCreator;
import com.stunner.moderstars.pro.Models.TitleParent;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import stunner.moderstars.R;

import com.stunner.moderstars.pro.Adapters.MyAdapter;

public class ActivityPro extends AppCompatActivity {
    public List<Integer> children = new ArrayList<>();
    RecyclerView recyclerView;
    public static String tag = "Brawl Mods";

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MobileAds.initialize(this);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Choosezip();
            }
        });
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

    }

    @Override
    protected void onResume() {
        super.onResume();
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Choosezip();
            }
        });
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        if (initData() != null) {
            fab.setImageDrawable(getDrawable(R.drawable.ic_check));
            MyAdapter adapter = new MyAdapter(this, initData());
            adapter.setParentClickableViewAnimationDefaultDuration();
            adapter.setParentAndIconExpandOnClick(true);
            recyclerView.setAdapter(adapter);
        }
    }

    private void deploy() {


    }

    private List<ParentObject> initData() {
        int modc;
        List<TitleCreator> titleCreator = TitleCreator.get(this);
        List<ParentObject> parentObject = new ArrayList<>();
        List<Object> childList = new ArrayList<>();
        modc = TitleCreator.checkmods(this).length-1;
        if (modc != -1) {
            for (int z = 0; z < titleCreator.size(); z++) {
                List<TitleParent> titles = titleCreator.get(z).getAll();
                for (int i = 0; i < modc; i++) {
                        childList.clear();
                        children.add(0);
                        for (File file : TitleCreator.filelist(this, TitleCreator.checkmods(this)[i])) {
                            if ((file.toString().endsWith(".csv")) || (file.toString().endsWith(".sc")) || (file.toString().endsWith(".scw")) || (file.toString().endsWith(".ogg"))) {
                                children.set(i, children.get(i) + 1);
                                childList.add(new TitleChild(file.toString().split("/")[file.toString().split("/").length - 1].toLowerCase()));
                            }
                        }
                        titles.get(i).setChildObjectList(childList);
                        parentObject.add(titles.get(i));


                }
            }
            return parentObject;
        } else return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {
                    // Get the Uri of the selected file
                    Uri uri = data.getData();
                    Log.d(tag, "File Uri: " + uri.toString());
                    // Get the path
                    String path = Environment.getExternalStorageDirectory().getPath() + uri.toString().replaceAll("%2F", "/").split("%3A")[uri.toString().split("%3A").length - 1];
                    Log.d(tag, "File Path: " + path);
                    // unzip(path);
                    // Get the file instance
                    // File file = new File(path);
                    // Initiate the upload
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private boolean unpackZip(String path, String zipname)
    {
        InputStream is;
        ZipInputStream zis;
        try
        {
            String filename;
            is = new FileInputStream(path + zipname);
            zis = new ZipInputStream(new BufferedInputStream(is));
            ZipEntry ze;
            byte[] buffer = new byte[1024];
            int count;

            while ((ze = zis.getNextEntry()) != null)
            {
                filename = ze.getName();

                // Need to create directories if not exists, or
                // it will generate an Exception...
                if (ze.isDirectory()) {
                    File fmd = new File(getExternalFilesDir(null).toString() + "/Mods/" + new File(getExternalFilesDir(null) + "/Mods").listFiles().length);
                    fmd.mkdirs();
                    continue;
                }

                FileOutputStream fout = new FileOutputStream(getExternalFilesDir(null).toString() + "/Mods/" + new File(getExternalFilesDir(null) + "/Mods").listFiles().length + filename);

                while ((count = zis.read(buffer)) != -1)
                {
                    fout.write(buffer, 0, count);
                }

                fout.close();
                zis.closeEntry();
            }

            zis.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return false;
        }

        return true;
    }
    private void Choosezip() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/zip");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Выберите архив мода"),
                    0);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Установите файловый менеджер!",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
        //choosezip();

    /*private static final int FILE_SELECT_CODE = 0;

    private void choosezip() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*//*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"),
                    FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(this, "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }*/