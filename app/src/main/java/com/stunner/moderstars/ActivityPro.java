package com.stunner.moderstars;

import androidx.annotation.NonNull;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.bignerdranch.expandablerecyclerview.Model.ParentObject;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.stunner.moderstars.pro.Models.TitleChild;
import com.stunner.moderstars.pro.Models.TitleParent;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
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

public class ActivityPro extends AppCompatActivity {

    class Deploy extends AsyncTask<Void,Void,Void>{

        ProgressDialog pd;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(ActivityPro.ctx);
            pd.setTitle("Brawl Mods");
            pd.setMessage("Установка...");
            pd.setCancelable(false);
            pd.setCanceledOnTouchOutside(false);
            pd.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Process process = Runtime.getRuntime().exec("su");
                DataOutputStream os = new DataOutputStream(process.getOutputStream());
                DataInputStream is = new DataInputStream(process.getInputStream());
                for (Object x : checked) {
                    if (x.getClass().equals(TitleChild.class)) {
                        String e = ActivityPro.ctx.getFilesDir().toString()+"com.supercell.brawlstars/test"+((TitleChild)x).path.split("/files/Mods")[1];
                        new File(e).mkdirs();
                        os.writeBytes("touch " + e + "\n\r");
                        os.writeBytes("cat "+((TitleChild) x).path + " >> " + e + "\n\r");
                        //((TitleChild) x).path;
                        os.flush();
                    }

                }
            }
            catch (Exception e) {e.printStackTrace();}
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pd.cancel();
        }
    }


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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_pro, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_about_pro:

                break;

            case R.id.action_settings_pro:
                startActivity(new Intent(this,ActivityAbout.class));
                break;


        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pro);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.app_name);
        }
        toolbar.inflateMenu(R.menu.menu_activity_pro);
        MobileAds.initialize(this);
        /*AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);*/
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

    }
//TODO: установка мода
    private void deploy() {
    }
    public List<TitleParent> initParents(){
        List<TitleParent> parents = new ArrayList<>();
        for (File e:UsefulThings.checkmods(this)) parents.add(new TitleParent(e.toString()));
        return parents;
    }
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
                    unzip(path);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
//TODO: доделать распаковку зип
    private boolean unzip(String path)
    {
        InputStream is;
        ZipInputStream zis;
        try
        {
            String filename;
            is = new FileInputStream(path);
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