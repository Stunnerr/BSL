package com.stunner.moderstars;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;
import stunner.moderstars.R;

import static com.stunner.moderstars.UsefulThings.TAG;
import static com.stunner.moderstars.UsefulThings.crashlytics;

public class Loading extends AppCompatActivity {
    TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        crashlytics = FirebaseCrashlytics.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        Resources res = this.getResources();
        int mode = res.getConfiguration().uiMode;
        SharedPreferences shPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        switch (shPrefs.getString("theme", "0")) {
            case "1":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                mode = Configuration.UI_MODE_NIGHT_NO;
                break;
            case "-1":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                mode = Configuration.UI_MODE_NIGHT_YES;

            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }
        Configuration conf = new Configuration(res.getConfiguration());
        conf.uiMode = mode;
        createConfigurationContext(conf);
        text = findViewById(R.id.LoadText);
        MyTask task = new MyTask();
        task.execute(true);
    }

    boolean Getroot(final boolean att) {
        try {
            Process process = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            DataInputStream osRes = new DataInputStream(process.getInputStream());
            os.writeBytes("id\n");
            os.flush();
            Thread.sleep(400);
            boolean root = false;
            try {
                byte[] b = new byte[1024];
                osRes.read(b);
                root = new String(b).contains("uid=0(root)");
            } catch (Exception ignore) {
            }
            if (root) {
                Log.i(TAG, "Rooted!");
                return true;

            } else {
                Log.w(TAG, "Root access rejected");
                if (att) Getroot(true);
                else {
                    Log.e(TAG, "Can't get root access");
                }
            }
        } catch (Exception e) {
            //crashlytics.recordException(e);
            Log.e(TAG, e.toString());
            return false;
        }
        return false;
    }

    private class MyTask extends AsyncTask<Boolean, Boolean, Boolean> {
        boolean access = true;

        @Override
        protected Boolean doInBackground(Boolean... root) {
            root[0] = false;
            try {
                root[0] = Getroot(false);
                try {
                    String path = getFilesDir().getAbsolutePath().replace(getPackageName() + "/files", "com.supercell.brawlstars/");
                    access = new File(path).list() != null;
                } catch (Exception ignore) {
                    access = false;
                }
            } catch (Exception e) {
                // crashlytics.recordException(e);
            }

            return root[0];
        }

        @Override
        protected void onPostExecute(Boolean root) {
            text.setText(R.string.starting);
            Intent intent = new Intent(getApplicationContext(), ActivityPro.class);
            intent.putExtra("access", access);
            intent.putExtra("root", root);
            startActivity(intent);
            finish();
        }
    }
}
