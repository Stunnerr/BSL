package com.stunner.moderstars;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.stunner.moderstars.ui.home.HomeFragment;
import com.stunner.moderstars.ui.more.SettingsFragment;
import com.stunner.moderstars.ui.repo.RepoFragment;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import static com.google.android.material.snackbar.BaseTransientBottomBar.ANIMATION_MODE_SLIDE;
import static com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_SHORT;
import static com.stunner.moderstars.UsefulThings.TAG;
import static com.stunner.moderstars.UsefulThings.bspath;
import static com.stunner.moderstars.UsefulThings.calculateSHA;
import static com.stunner.moderstars.UsefulThings.crashlytics;
import static com.stunner.moderstars.UsefulThings.root;

public class RedesignActivity extends AppCompatActivity {

    static boolean access;
    static CoordinatorLayout layout;
    int perms = 0;
    boolean showcheck = true;
    Fragment home = new HomeFragment(), repo = new RepoFragment(), more = new SettingsFragment();
    Fragment active = home;
    AlertDialog dialog;
    String bsapk = "", size = "...";
    Runnable download = new Runnable() {
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
                    stringBuilder.append(line).append('\n');
                }
                bsapk = stringBuilder.toString();
                size = new DecimalFormat("#.##").format((double) new URL(bsapk.split("\n")[0]).openConnection().getContentLength() / 1048576);
                if (dialog != null) dialog.setMessage(getString(R.string.signwarn, size));
            } catch (Exception e) {
                crashlytics.recordException(e);
            }
        }
    };

    public boolean granted(int[] results) {
        for (int a : results) {
            if (a != PackageManager.PERMISSION_GRANTED) return false;
        }
        return true;
    }

    private boolean hasReadPermissions() {
        return (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    }

    private boolean hasWritePermissions() {
        return (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    }

    private void installMethod() {
        final MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle("BSL.Install").setMessage(R.string.variant);
        builder.setPositiveButton(R.string.sign, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                signer();
            }
        });
        builder.setNegativeButton(R.string.install, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new UsefulThings.Deploy().execute();
            }
        });
        builder.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redesign);
        layout = findViewById(R.id.nav_host_fragment);
        requestAppPermissions();
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        new Thread(download).start();
        root = getIntent().getBooleanExtra("root", false);
        final float elevation = getSupportActionBar().getElevation();
        getSupportActionBar().setElevation(0);
        if (!root) UsefulThings.su = "";
        access = getIntent().getBooleanExtra("access", false);
        bspath = getFilesDir().getAbsolutePath().split(getPackageName())[0] + "com.supercell.brawlstars/";
        navView.setSelectedItemId(R.id.navigation_home);
        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                showcheck = item.getItemId() == R.id.navigation_home;
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        getSupportActionBar().setTitle(R.string.app_name);
                        getSupportActionBar().setElevation(0);
                        home = new HomeFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, home).commit();
                        active = home;
                        break;
                    case R.id.navigation_repo:
                        getSupportActionBar().setTitle(R.string.title_repo);
                        getSupportActionBar().setElevation(elevation);
                        getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, repo).commit();
                        active = repo;
                        break;
                    case R.id.navigation_more:
                        getSupportActionBar().setTitle(R.string.title_more);
                        getSupportActionBar().setElevation(elevation);
                        getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, more).commit();
                        active = more;
                        break;
                }
                invalidateOptionsMenu();
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.nav_host_fragment, active).commit();
        UsefulThings.ctx = this;
    }

    public static void showSnackBar(String text) {
        Snackbar snackbar = Snackbar.make(layout, text, LENGTH_SHORT).setAnimationMode(ANIMATION_MODE_SLIDE);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) snackbar.getView().getLayoutParams();
        params.setMargins(0, 0, 0, 0);
        snackbar.getView().setLayoutParams(params);
        snackbar.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_tab_toolbar, menu);
        MenuItem check = menu.findItem(R.id.done);
        if (!showcheck) check.setVisible(false);
        else check.setVisible(true);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.done) {
            if (access || root)
                installMethod();
            else signer();
        }
        return super.onOptionsItemSelected(item);
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

    public void signer() {
        try {
            wait(1000);
        } catch (Exception ignore) {
        }
        String sha = calculateSHA(new File(getExternalFilesDir(null) + "/bs_original.apk"));
        if (!new File(getExternalFilesDir(null) + "/bs_original.apk").exists() || !sha.equals(bsapk.split("\n")[1].toLowerCase())) {
            try {
                MaterialAlertDialogBuilder builder1 = new MaterialAlertDialogBuilder(this);
                builder1.setTitle("BSL.Sign").setMessage(getString(R.string.signwarn, size));
                builder1.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (new File(getExternalFilesDir(null) + "/bs_original.apk").exists())
                            new File(getExternalFilesDir(null) + "/bs_original.apk").delete();
                        if (new File(getExternalFilesDir(null) + "/bs_mod_unsigned.apk").exists())
                            new File(getExternalFilesDir(null) + "/bs_mod_unsigned.apk").delete();
                        if (new File(getExternalFilesDir(null) + "/bs_mod_signed.apk").exists())
                            new File(getExternalFilesDir(null) + "/bs_mod_signed.apk").delete();
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
                dialog = builder1.setCancelable(true).setOnCancelListener(null).create();
                dialog.show();
            } catch (Exception e) {
                crashlytics.recordException(e);
                Log.e(TAG, "Exception", e);
            }
        } else {
            new UsefulThings.Signer().execute("");
        }
    }

}