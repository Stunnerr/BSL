package com.stunner.moderstars;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.LoginFilter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.bignerdranch.expandablerecyclerview.Model.ParentObject;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.stunner.moderstars.pro.Adapters.MyAdapter;
import com.stunner.moderstars.pro.Models.TitleChild;
import com.stunner.moderstars.pro.Models.TitleParent;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import stunner.moderstars.R;

import static com.stunner.moderstars.UsefulThings.Unzip;
import static com.stunner.moderstars.UsefulThings.bspath;
import static com.stunner.moderstars.UsefulThings.copy;
import static com.stunner.moderstars.UsefulThings.sudo;
import static com.stunner.moderstars.pro.Adapters.MyAdapter.checked;


public class ActivityPro extends AppCompatActivity {
int perms = 0;
 class Deploy extends AsyncTask<Void,Void,Void> {


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
            for (Object o:checked){
                if (o.getClass().equals(TitleParent.class)){
                    TitleParent x = (TitleParent) o;
                    Log.d(tag,x.getforcopy());
                    try{sudo("mkdir -p "+(bspath+"dkoskd"+ x.getforcopy()));}
                    catch(IOException e ) {Snackbar.make(findViewById(R.id.recyclerView), e.getLocalizedMessage(), Snackbar.LENGTH_LONG);}
                }

                else
                if (o.getClass().equals(TitleChild.class)){
                    TitleChild x = (TitleChild) o;
                    x.getPath().mkdirs();
                    Log.d(tag,bspath + "dkoskd" + x.getforcopy());
                    try{copy(x.getPath(), new File(bspath + "dkoskd" + x.getforcopy()));}
                    catch (IOException e) {
                        Snackbar.make(findViewById(R.id.recyclerView), e.getLocalizedMessage(), Snackbar.LENGTH_LONG);
                    }
                }


            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pd.dismiss();
        }
    }

    FloatingActionButton fab;
    RecyclerView recyclerView;
    public static String tag = "Brawl Mods";
    //public String modspath = getExternalFilesDir("")+"/Mods/test";
    public static Context ctx;

    private void requestAppPermissions() {
        if (hasReadPermissions() && hasWritePermissions()) {
            return;
        }
        ActivityCompat.requestPermissions(this,
                new String[] {
                        Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
                },1 ); // your request code
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
    }
    public boolean granted(int[] results)
    {
        for (int a: results) {
            if(a!= PackageManager.PERMISSION_GRANTED)return false;

        }
        return true;
    }

    private boolean hasReadPermissions() {
        return (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    }

    private boolean hasWritePermissions() {
        return (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case 1:
                if(granted(grantResults))
                {}
                else {if(perms<5){requestAppPermissions(); perms++;}
                else System.exit(1);}
                break;
            case 2:

                if(granted(grantResults))
                {}
                else {if(perms<5){requestAppPermissions(); perms++;}
                else System.exit(1);}
                break;
            default:
                break;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
                Snackbar.make(findViewById(R.id.pro_root), "Some text", Snackbar.LENGTH_SHORT).show();


                break;

            case R.id.action_settings_pro:
                startActivity(new Intent(this,ActivityAbout.class));
                break;

            case R.id.action_add_pro:

                Choosezip();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestAppPermissions();
        setContentView(R.layout.activity_pro);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.app_name);
        }
        toolbar.inflateMenu(R.menu.menu_activity_pro);
        bspath = getFilesDir().getAbsolutePath().split(getPackageName())[0] +"com.supercell.brawlstars/";
        fab = findViewById(R.id.fab);
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
                    //
                    deploy();
                    Snackbar.make(findViewById(R.id.recyclerView
                    ), "Some text", Snackbar.LENGTH_SHORT).setDuration(900).show();

                }
            });
            MyAdapter adapter = new MyAdapter(this, initData());
            adapter.setParentClickableViewAnimationDefaultDuration();
            adapter.setParentAndIconExpandOnClick(true);
            recyclerView.setAdapter(adapter);
        }

    }


    @Override
    protected void onResume() {
        ctx=this;
        requestAppPermissions();
        super.onResume();

    }
//TODO: установка мода
    private void deploy() {
        for (Object o:checked){
            if (o.getClass().equals(TitleParent.class)){
                TitleParent x = (TitleParent) o;
                //Log.d(tag,x.getforcopy());
                try{sudo("mkdir -p "+(bspath+"update"+ x.getforcopy()));}
                catch(IOException e ) {Snackbar.make(findViewById(R.id.recyclerView), e.getLocalizedMessage(), Snackbar.LENGTH_LONG);}
            }

            else
            if (o.getClass().equals(TitleChild.class)){
                TitleChild x = (TitleChild) o;
                x.getPath().mkdirs();
                //Log.d(tag,bspath + "dkoskd" + x.getforcopy());
                try{copy(x.getPath(), new File(bspath + "update" + x.getforcopy()));}
                catch (IOException e) {
                    Snackbar.make(findViewById(R.id.recyclerView), e.getLocalizedMessage(), Snackbar.LENGTH_LONG);
                }
            }


        }

    }

    public List<TitleParent> initParents(){
        List<TitleParent> parents = new ArrayList<>();
        for (File e:UsefulThings.checkmods(this)) parents.add(new TitleParent(e));
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
                    if (UsefulThings.filelist(this, UsefulThings.checkmods(this)[i]) != null){
                        for (File file : UsefulThings.filelist(this, UsefulThings.checkmods(this)[i])) {//список файлов
                            if ((file.toString().endsWith(".csv")) || (file.toString().endsWith(".sc")) || (file.toString().endsWith(".scw")) || (file.toString().endsWith(".ogg") || (file.toString().endsWith(".ktx")))) {//Проверка файла
                                childList.add(new TitleChild(file));
                            }
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
            case 13:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    Log.d(tag, "File Uri: " + uri.toString());
                    String path = Environment.getExternalStorageDirectory().getPath() +'/'+ uri.toString().replaceAll("%2F", "/").split("%3A")[uri.toString().split("%3A").length - 1];
                    Log.d(tag, "File Path: " + path);
                    AlertDialog dialog = new AlertDialog.Builder(this).create();
                    dialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            try {

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    dialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.no), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    dialog.setTitle(R.string.app_name); dialog.setMessage(getString(R.string.clear));
                    dialog.show();
                    Unzip(path);
                    if (initData() !=null){
                    MyAdapter adapter = new MyAdapter(this, initData());
                    adapter.setParentClickableViewAnimationDefaultDuration();
                    adapter.setParentAndIconExpandOnClick(true);
                    recyclerView.setAdapter(adapter);
                        fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_check));
                        fab.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //
                                deploy();
                                Snackbar.make(findViewById(R.id.recyclerView
                                ), "Some text", Snackbar.LENGTH_SHORT).setDuration(900).show();
                               // Log.d(tag,getFilesDir().getPath() + "/com.supercell.brawlstars");

                            }
                        });
                }

                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    private void Choosezip() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/zip");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Выберите архив мода"),
                    13);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Установите файловый менеджер!",
                    Toast.LENGTH_SHORT).show();
        }
    }
}