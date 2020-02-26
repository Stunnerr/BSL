package com.stunner.moderstars;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bignerdranch.expandablerecyclerview.Model.ParentObject;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.stunner.moderstars.pro.Adapters.RecyclerViewAdapter;
import com.stunner.moderstars.pro.Models.ListChild;
import com.stunner.moderstars.pro.Models.ListParent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import stunner.moderstars.R;

import static com.stunner.moderstars.UsefulThings.TAG;
import static com.stunner.moderstars.UsefulThings.Unzip;
import static com.stunner.moderstars.UsefulThings.bspath;
import static com.stunner.moderstars.UsefulThings.checked;
import static com.stunner.moderstars.UsefulThings.copy;
import static com.stunner.moderstars.UsefulThings.sudo;

public class ActivityPro extends AppCompatActivity {
int perms = 0;
 class Deploy extends AsyncTask<Void,String,String> {


        ProgressDialog pd;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(getApplicationContext());
            pd.setTitle("Brawl Mods");
            pd.setMessage(getString(R.string.installing).replace(":","..."));
            pd.setCancelable(false);
            pd.setCanceledOnTouchOutside(false);
            pd.show();
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                publishProgress(getString(R.string.backingup));
                sudo("rm -rf "+ bspath+"update1/");
                copy(bspath + "update/", bspath+"update1/");
                sudo("rm -rf "+ bspath+"update/");
                for (Object o : checked) {
                    if (o.getClass().equals(ListParent.class)) {
                        ListParent x = (ListParent) o;
                        publishProgress(getString(R.string.installing) + x.getforcopy());
                        sudo("mkdir -p "+(bspath+"update"+ x.getforcopy()));
                        Log.d(UsefulThings.TAG, x.getforcopy());
                    } else if (o.getClass().equals(ListChild.class)) {
                        ListChild x = (ListChild) o;
                        sudo("mkdir -p "+(bspath+"update"+ x.getforcopy().replace("/"+x.getOption1(), "/") ));
                        copy(x.getPath(), new File(bspath + "update" + x.getforcopy()));
                        publishProgress(getString(R.string.installing) + x.getforcopy());
                        x.getPath().mkdirs();
                        Log.d(UsefulThings.TAG, bspath + x.getforcopy());
                    }
                    else{
                        Log.d(TAG, "doInBackground: wtf");
                    }
                }
            } catch (Exception e) {
                cancel(true);
                return e.getLocalizedMessage();
            }
            return null;
        }

     @Override
     protected void onCancelled(String aVoid) {
            pd.dismiss();
         Snackbar.make(findViewById(R.id.pro_root),aVoid, Snackbar.LENGTH_LONG);
         super.onCancelled(aVoid);
     }

     @Override
     protected void onProgressUpdate(String... values) {
         super.onProgressUpdate(values);
         pd.setMessage(values[0]);
     }

     @Override
        protected void onPostExecute(String aVoid) {
            super.onPostExecute(aVoid);
            pd.dismiss();
        }
    }

    private TabsAdapter mTabsAdapter;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    FloatingActionButton fab;
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
                if(!granted(grantResults))
                {if(perms<5){requestAppPermissions(); perms++;}
                else System.exit(1);}
                break;
            case 2:

                if(!granted(grantResults))
                {if(perms<5){requestAppPermissions(); perms++;}
                else System.exit(1);}
                break;
            default:
                break;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_release, menu);
        return true;
    }

    public void restore(){
try {
        copy(bspath+"update/", bspath+"update2/");
        sudo("rm -rf "+bspath+"update/");
        copy(bspath+"update1/", bspath+"update/");
        sudo("rm -rf "+bspath+"update1/");
        copy(bspath+"update2/", bspath+"update1/");
        sudo("rm -rf "+bspath+"update2/");
    }
    catch (Exception e){e.printStackTrace();}
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_about:
                Snackbar.make(findViewById(R.id.pro_root), "Some text", Snackbar.LENGTH_SHORT).show();
                break;

            case R.id.action_settings:
                startActivity(new Intent(this,ActivityAbout.class));
                break;

            case R.id.action_add:

                Choosezip();
                break;
            case R.id.action_restore:
                restore();
                Snackbar.make(findViewById(R.id.pro_root), R.string.restored, Snackbar.LENGTH_SHORT).show();
                break;
            case R.id.action_alpha:
                startActivity(new Intent(getApplicationContext(),ActivityEasy.class));
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
        toolbar.inflateMenu(R.menu.menu_release);
        bspath = getFilesDir().getAbsolutePath().split(getPackageName())[0] +"com.supercell.brawlstars/";
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Choosezip();
            }
        });
        mTabsAdapter = new TabsAdapter(getSupportFragmentManager());
         mViewPager = findViewById(R.id.viewpager);
        mTabLayout = findViewById(R.id.tabs);
        mViewPager.setAdapter(mTabsAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        int a;
        try{
            a=mTabsAdapter.getCount();
        }
        catch (NullPointerException e){
            a=-10;
        }
        if (a!=-10) {
            fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_check));
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Deploy task = new Deploy();
                    task.execute();
                    Snackbar.make(findViewById(R.id.pro_root
                    ), R.string.success, Snackbar.LENGTH_SHORT).setDuration(900).show();
                }
            });
        }
    }


    @Override
    protected void onResume() {
        ctx=this;
        requestAppPermissions();
        super.onResume();

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 13:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    Log.d(TAG, "File Uri: " + uri.toString());
                    String path = Environment.getExternalStorageDirectory().getPath() +'/'+ uri.toString().replaceAll("%2F", "/").split(Environment.getExternalStorageDirectory().getPath())[uri.toString().split(Environment.getExternalStorageDirectory().getPath()).length - 1];
                    Log.d(TAG, "File Path: " + path);
                    Unzip(path);
                    checked.clear();
                    mTabsAdapter = new TabsAdapter(getSupportFragmentManager());
                    mViewPager.setAdapter(mTabsAdapter);
                    mTabLayout.setupWithViewPager(mViewPager);
                    if (mTabsAdapter.getCount()>=1){
                        fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_check));
                        fab.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Deploy task = new Deploy();
                                task.execute();
                                Snackbar.make(findViewById(R.id.pro_root
                                ), R.string.success, Snackbar.LENGTH_SHORT).setDuration(900).show();

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
    public class TabsAdapter extends FragmentPagerAdapter {

        TabsAdapter(FragmentManager fm) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return TabsFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return UsefulThings.modcount(getApplicationContext());
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "Mod #"+ (position+1);
        }
    }
    public static class TabsFragment extends Fragment {
        private int mPage;
        private static final String ARG_PAGE = "section_number";

        public TabsFragment() {
        }
        static TabsFragment newInstance(int sectionNumber) {
            TabsFragment fragment = new TabsFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_PAGE, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }
        RecyclerViewAdapter adapter;
        @Override public void onCreate(Bundle savedInstanceState) {
            if (getArguments() != null) {
                mPage = getArguments().getInt(ARG_PAGE);
            }
            adapter = new RecyclerViewAdapter(getContext(), initData(mPage));
            adapter.setParentClickableViewAnimationDefaultDuration();
            adapter.setParentAndIconExpandOnClick(true);
            super.onCreate(savedInstanceState);
        }
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_pro, container, false);
            RecyclerView recyclerView = rootView.findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(adapter);
            return rootView;

        }
        List<ListParent> initParents(int modc){
            List<ListParent> parents = new ArrayList<>();
            for (File e:UsefulThings.checkmod(getContext(), modc)) parents.add(new ListParent(e, modc));
            return parents;
        }
        private List<ParentObject> initData(int modc) {
            int folc;
            List<ParentObject> parentObject = new ArrayList<>();
            folc = UsefulThings.checkmod(getContext(), modc).length-1;
            if (folc != -1) {

                List<ListParent> parents = initParents(modc);
                for (int i = 0; i < folc; i++) {
                    List<Object> childList = new ArrayList<>();
                    if (UsefulThings.filelist(getContext(), UsefulThings.checkmod(getContext(), modc)[i]) != null){
                        for (File file : UsefulThings.filelist(getContext(), UsefulThings.checkmod(getContext(), modc)[i])) {
                            childList.add(new ListChild(file, modc));
                        }
                    }
                    parents.get(i).setChildObjectList(childList);
                    parentObject.add(parents.get(i));

                }
                return parentObject;
            } else return null;
        }
    }
}