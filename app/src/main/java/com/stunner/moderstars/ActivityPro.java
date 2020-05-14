package com.stunner.moderstars;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import stunner.moderstars.R;

import static com.google.android.material.snackbar.BaseTransientBottomBar.ANIMATION_MODE_SLIDE;
import static com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_SHORT;
import static com.stunner.moderstars.UsefulThings.TAG;
import static com.stunner.moderstars.UsefulThings.bspath;
import static com.stunner.moderstars.UsefulThings.calculateSHA;
import static com.stunner.moderstars.UsefulThings.checked;
import static com.stunner.moderstars.UsefulThings.copy;
import static com.stunner.moderstars.UsefulThings.crashlytics;
import static com.stunner.moderstars.UsefulThings.root;
import static com.stunner.moderstars.UsefulThings.sudo;

public class ActivityPro extends AppCompatActivity {
    @SuppressLint("StaticFieldLeak")
    public static Context ctx;
    static boolean access;
    static TabsAdapter mTabsAdapter;
    static TabLayout mTabLayout;
    static ViewPager mViewPager;
    static FloatingActionButton fab;
    static FragmentManager fragmentManager;
    static int a = -10;
    int perms = 0;
    String[] supportedMimeTypes = {"application/zip", "application/vnd.android.package-archive"};
    String bsapk = "", size = "100";
    View.OnClickListener listeneraccess = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
            builder.setTitle("BSL.Install").setMessage(R.string.variant);
            builder.setPositiveButton(R.string.sign, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (!new File(getExternalFilesDir(null) + "/bs_original.apk").exists()) {
                        try {
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(ctx);
                            builder1.setTitle("BSL.Sign").setMessage(getString(R.string.signwarn, size));
                            builder1.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    DownloadManager downloadmanager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                                    Uri uri = Uri.parse(bsapk);
                                    DownloadManager.Request request = new DownloadManager.Request(uri);
                                    request.setTitle("Brawl Stars.apk");
                                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                    request.setVisibleInDownloadsUi(false);
                                    request.setDestinationUri(Uri.parse("file://" + getExternalFilesDir(null) + "/bs_original.apk"));
                                    downloadmanager.enqueue(request);
                                }
                            });
                            builder1.setNegativeButton(R.string.no, null);
                            builder1.setCancelable(true).setOnCancelListener(null).show();
                        } catch (Exception e) {
                            crashlytics.recordException(e);
                            Log.e(TAG, "Exception", e);
                        }
                    } else {
                        new UsefulThings.Signer().execute("");
                    }
                }
            });
            builder.setNegativeButton(R.string.install, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    new Deploy().execute();
                }
            });
            builder.create().show();
        }
    };

    public static void showSnackBar(String text) {
        Snackbar snackbar = Snackbar.make(fab, text, LENGTH_SHORT).setAnimationMode(ANIMATION_MODE_SLIDE);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) snackbar.getView().getLayoutParams();
        params.setMargins(0, 0, 0, 0);
        snackbar.getView().setLayoutParams(params);
        snackbar.show();
    }

    private void requestAppPermissions() {
        if (hasReadPermissions() && hasWritePermissions()) {
            return;
        }
        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, 1); // your request code
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
    }

    public boolean granted(int[] results) {
        for (int a : results) {
            if (a != PackageManager.PERMISSION_GRANTED) return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
            case 2:
                if (!granted(grantResults)) {
                    if (perms < 5) {
                        requestAppPermissions();
                        perms++;
                    } else System.exit(1);
                }
                break;
            default:
                break;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private boolean hasReadPermissions() {
        return (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    }

    private boolean hasWritePermissions() {
        return (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    }

    public void restore() {
        try {
            copy(bspath + "update/", bspath + "update2/");
            sudo("rm -rf " + bspath + "update/");
            copy(bspath + "update1/", bspath + "update/");
            sudo("rm -rf " + bspath + "update1/");
            copy(bspath + "update2/", bspath + "update1/");
            sudo("rm -rf " + bspath + "update2/");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_release, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, ActivitySettings.class));
                break;

            case R.id.action_about:
                startActivity(new Intent(this, ActivityAbout.class));
                break;

            case R.id.action_add:
                choosezip();
                break;
            case R.id.action_restore:
                restore();
                showSnackBar((String) getText(R.string.restored));
                break;
            case R.id.action_alpha:
                startActivity(new Intent(getApplicationContext(), ActivityPro.class));
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
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosezip();
            }
        });
        mViewPager = findViewById(R.id.viewpager);
        mTabLayout = findViewById(R.id.tabs);
        mTabsAdapter = new TabsAdapter(getSupportFragmentManager());
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("https://pastebin.com/raw/xStDu04p");
                    URLConnection urlConnection = url.openConnection();
                    urlConnection.setConnectTimeout(5000);
                    urlConnection.setReadTimeout(5000);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    bsapk = stringBuilder.toString();
                    size = new DecimalFormat("#.##").format((double) new URL(bsapk).openConnection().getContentLength() / 1048576);
                } catch (Exception e) {
                    crashlytics.recordException(e);
                }
            }
        };
        new Thread(runnable).start();
        mViewPager.setAdapter(mTabsAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        setSupportActionBar(toolbar);
        toolbar.inflateMenu(R.menu.menu_release);
        bspath = getFilesDir().getAbsolutePath().split(getPackageName())[0] + "com.supercell.brawlstars/";
        root = getIntent().getBooleanExtra("root", false);
        if (!root) UsefulThings.su = "";
        access = getIntent().getBooleanExtra("access", false);
        if (mTabsAdapter.getCount() > 0) {
            fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_check));
            if (access || root)
                fab.setOnClickListener(listeneraccess);
            else fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!new File(getExternalFilesDir(null) + "/bs_original.apk").exists() || !calculateSHA(new File(getExternalFilesDir(null) + "/bs_original.apk")).equals(bsapk.split("\n")[1])) {
                        try {
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(ctx);
                            builder1.setTitle("BSL.Sign").setMessage(getString(R.string.signwarn, size));
                            builder1.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    DownloadManager downloadmanager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                                    Uri uri = Uri.parse(bsapk.split("\n")[0]);
                                    DownloadManager.Request request = new DownloadManager.Request(uri);
                                    request.setTitle("Brawl Stars.apk");
                                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                    request.setVisibleInDownloadsUi(false);
                                    request.setDestinationUri(Uri.parse("file://" + getExternalFilesDir(null) + "/bs_original.apk"));
                                    downloadmanager.enqueue(request);
                                }
                            });
                            builder1.setNegativeButton(R.string.no, null);
                            builder1.setCancelable(true).setOnCancelListener(null).show();
                        } catch (Exception e) {
                            crashlytics.recordException(e);
                            Log.e(TAG, "Exception", e);
                        }
                    } else {
                        new UsefulThings.Signer().execute("");
                    }
                }
            });
        }
    }

    @Override
    protected void onResume() {
        ctx = this;
        if (a != mTabsAdapter.getCount()) mTabsAdapter.notifyDataSetChanged();
        SharedPreferences shPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        switch (shPrefs.getString("theme", "0")) {
            case "1":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case "-1":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }
        fragmentManager = getSupportFragmentManager();
        requestAppPermissions();
        super.onResume();
    }

    String getPath(Uri uri) {
        File a = new File(getExternalFilesDir(null).getAbsolutePath() + "/temp/");
        a.mkdirs();
        File file = new File(getExternalFilesDir(null).getAbsolutePath() + "/temp/temp" + a.list().length + ".zip");
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            FileOutputStream outputStream = new FileOutputStream(file);
            final byte[] buffers = new byte[8192];
            while (inputStream.read(buffers) != -1) {
                outputStream.write(buffers);
            }
            inputStream.close();
            outputStream.close();
        } catch (Exception e) {
            crashlytics.recordException(e);
        }
        return file.getAbsolutePath();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 13:
                if (resultCode == RESULT_OK) {
                    String[] paths;
                    if (null != data.getClipData()) {                                                 // checking multiple selection or not
                        paths = new String[data.getClipData().getItemCount()];
                        for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                            Uri uri = data.getClipData().getItemAt(i).getUri();
                            Log.d(TAG, "File Uri: " + uri.toString());
                            String path = getPath(uri);
                            Log.d(TAG, "onActivityResult: " + path);
                            paths[i] = path;
                        }
                    } else {
                        Uri uri = data.getData();
                        Log.d(TAG, "File Uri: " + uri.toString());
                        String path = getPath(uri);
                        Log.d(TAG, "onActivityResult: " + path);
                        paths = new String[]{path};
                    }
                    new UsefulThings.Unzipper().execute(paths);
                }
                break;
            default:
                Log.e(TAG, "Unexpected value: " + requestCode);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void choosezip() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, supportedMimeTypes);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Выберите архив мода"),
                    13);
        } catch (android.content.ActivityNotFoundException e) {
            crashlytics.recordException(e);
            Toast.makeText(this, "Установите файловый менеджер!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public static class TabsFragment extends Fragment {
        private static final String ARG_PAGE = "section_number";
        RecyclerViewAdapter adapter;
        private int mPage;

        public TabsFragment() {
        }

        static TabsFragment newInstance(int sectionNumber) {
            TabsFragment fragment = new TabsFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_PAGE, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
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

        List<ListParent> initParents(int modc) {
            List<ListParent> parents = new ArrayList<>();
            for (File e : UsefulThings.checkmod(getContext(), modc))
                parents.add(new ListParent(e, modc));
            return parents;
        }

        private List<ParentObject> initData(int modc) {
            int folc;
            List<ParentObject> parentObject = new ArrayList<>();
            folc = UsefulThings.checkmod(getContext(), modc).length - 1;
            if (folc != -1) {

                List<ListParent> parents = initParents(modc);
                for (int i = 0; i < folc + 1; i++) {
                    List<Object> childList = new ArrayList<>();
                    if (UsefulThings.filelist(UsefulThings.checkmod(getContext(), modc)[i]) != null) {
                        for (File file : UsefulThings.filelist(UsefulThings.checkmod(getContext(), modc)[i])) {
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

    class Deploy extends AsyncTask<Void, String, String> {


        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(ActivityPro.this);
            pd.setTitle("BSL.Install");
            pd.setMessage(getString(R.string.installing).replace(":", "..."));
            pd.setCancelable(false);
            pd.setCanceledOnTouchOutside(false);
            pd.show();
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                publishProgress(getString(R.string.backingup));
                sudo("rm -rf " + bspath + "update1/");
                copy(bspath + "update/", bspath + sudo("ls " + bspath + " "));
                sudo("rm -rf " + bspath + "update/");
                for (Object o : checked) {
                    if (o.getClass().equals(ListParent.class)) {
                        ListParent x = (ListParent) o;
                        publishProgress(getString(R.string.installing) + x.getforcopy());
                        Log.d(TAG, sudo("mkdir -p " + (bspath + "update/" + x.getforcopy())));
                        Log.d(TAG, x.getforcopy());
                    } else if (o.getClass().equals(ListChild.class)) {
                        ListChild x = (ListChild) o;
                        sudo("mkdir -p " + (bspath + "update/" + x.getforcopy().replace("/" + x.getOption1(), "/")));
                        publishProgress(getString(R.string.installing) + x.getforcopy());
                        copy(x.getPath(), new File(bspath + "update/" + x.getforcopy()));
                        x.getPath().mkdirs();
                        Log.d(TAG, "mkdir -p " + (bspath + "update/" + x.getforcopy().replace("/" + x.getOption1(), "/")));
                        Log.d(TAG, x.getPath().getAbsolutePath());
                        Log.d(TAG, bspath + x.getforcopy());
                    } else {
                        cancel(true);
                        Log.d(TAG, "doInBackground: wtf");
                        return "error";
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
            showSnackBar("Cancelled");
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
            showSnackBar(aVoid == null ? (String) getText(R.string.success) : aVoid);
        }
    }

    public class TabsAdapter extends FragmentStatePagerAdapter {

        TabsAdapter(FragmentManager fm) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @Override
        public void notifyDataSetChanged() {
            if (getCount() > 0) {
                mTabLayout.setVisibility(View.VISIBLE);
                fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_check));
                if (access || root)
                    fab.setOnClickListener(listeneraccess);
                else fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!new File(getExternalFilesDir(null) + "/bs_original.apk").exists() || !calculateSHA(new File(getExternalFilesDir(null) + "/bs_original.apk")).equals(bsapk.split("\n")[1])) {
                            try {
                                AlertDialog.Builder builder1 = new AlertDialog.Builder(ctx);
                                builder1.setTitle("BSL.Sign").setMessage(getString(R.string.signwarn, size));
                                builder1.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        DownloadManager downloadmanager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                                        Uri uri = Uri.parse(bsapk.split("\n")[0]);
                                        DownloadManager.Request request = new DownloadManager.Request(uri);
                                        request.setTitle("Brawl Stars.apk");
                                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                        request.setVisibleInDownloadsUi(false);
                                        request.setDestinationUri(Uri.parse("file://" + getExternalFilesDir(null) + "/bs_original.apk"));
                                        downloadmanager.enqueue(request);
                                    }
                                });
                                builder1.setNegativeButton(R.string.no, null);
                                builder1.setCancelable(true).setOnCancelListener(null).show();
                            } catch (Exception e) {
                                crashlytics.recordException(e);
                                Log.e(TAG, "Exception", e);
                            }
                        } else {
                            new UsefulThings.Signer().execute("");
                        }
                    }
                });
            } else {
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        choosezip();
                    }
                });
                mTabLayout.setVisibility(View.INVISIBLE);
            }
            super.notifyDataSetChanged();
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
            return PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("mod" + position, getString(R.string.mod) + (position + 1));
        }
    }
}