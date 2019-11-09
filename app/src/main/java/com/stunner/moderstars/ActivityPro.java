package com.stunner.moderstars;

import androidx.annotation.NonNull;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
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
import com.stunner.moderstars.pro.Models.TitleParent;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import stunner.moderstars.R;

import com.stunner.moderstars.pro.Adapters.MyAdapter;
import static com.stunner.moderstars.pro.Adapters.MyAdapter.checked;

public class ActivityPro extends Activity {
    RecyclerView recyclerView;
    public static String tag = "Brawl Mods";
    ProgressDialog pd;
    public static Context ctx;
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
        setContentView(R.layout.activity_pro);

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

    private void copyFile(String inputPath, String inputFile, String outputPath) {

        InputStream in = null;
        OutputStream out = null;
        try {

            //create output directory if it doesn't exist
            File dir = new File (outputPath);
            if (!dir.exists())
            {
                dir.mkdirs();
            }


            in = new FileInputStream(inputPath + inputFile);
            out = new FileOutputStream(outputPath + inputFile);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            // write the output file (You have now copied the file)
            out.flush();
            out.close();

        }  catch (FileNotFoundException fnfe1) {
            Log.e("tag", fnfe1.getMessage());
        }
        catch (Exception e) {
            Log.e("tag", e.getMessage());
        }

    }
    @Override
    protected void onResume() {
        ctx=this;
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
            fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_check));
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deploy();
                }
            });
            MyAdapter adapter = new MyAdapter(this, initData());
            adapter.setParentClickableViewAnimationDefaultDuration();
            adapter.setParentAndIconExpandOnClick(true);
            recyclerView.setAdapter(adapter);
        }
    }
//TODO: установка мода
    private void deploy() {
        pd = new ProgressDialog(this);
        pd.setTitle("Brawl Mods");
        pd.setMessage("Установка...");
        pd.setCancelable(false);
        pd.show();
        /*for (Object x: checked)
        {
            File temp = new File(x);

                if (!temp.isDirectory())copyFile(x.replace(x.split("/")[x.split("/").length-1],""),x,getFilesDir().getPath().split(getPackageName())[0]+"com.supercell.brawlstars/update"+ x.split("/files/Mods")[1]);
        }*/
        pd.cancel();
    }
    public List<TitleParent> initParents(){
        List<TitleParent> parents = new ArrayList<>();
        for (File e:UsefulThings.checkmods(this)) parents.add(new TitleParent(e.toString()));
        return parents;
    }
//TODO: переписать инициализацию
    private List<ParentObject> initData() {
        int modc;
        List<ParentObject> parentObject = new ArrayList<>();
        modc = UsefulThings.checkmods(this).length-1;
        if (modc != -1) {

                List<TitleParent> parents = initParents();
                for (int i = 0; i < modc; i++) {
                    List<Object> childList = new ArrayList<>();
                        for (File file : UsefulThings.filelist(this, UsefulThings.checkmods(this)[i])) {//список файлов
                            if ((file.toString().endsWith(".csv")) || (file.toString().endsWith(".sc")) || (file.toString().endsWith(".scw")) || (file.toString().endsWith(".ogg"))) {//Проверка файла
                                childList.add(new TitleChild(file.toString()));
                            }
                        }
                    parents.get(i).setChildObjectList(childList);
                    parentObject.add(parents.get(i));

                }
            return parentObject;
        } else return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    Log.d(tag, "File Uri: " + uri.toString());
                    String path = Environment.getExternalStorageDirectory().getPath() + uri.toString().replaceAll("%2F", "/").split("%3A")[uri.toString().split("%3A").length - 1];
                    Log.d(tag, "File Path: " + path);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
//TODO: доделать распаковку зип
    private boolean unzip(String path, String zipname)
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