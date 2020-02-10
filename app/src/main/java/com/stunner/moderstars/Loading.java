package com.stunner.moderstars;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import androidx.appcompat.app.AppCompatActivity;
import stunner.moderstars.R;
public class Loading extends AppCompatActivity {
    TextView text;
    private class MyTask extends AsyncTask <Boolean, Boolean, Boolean> {
        @Override
        protected Boolean doInBackground(Boolean... root) {
            root[0] = false;
            try{
                root[0] = Getroot(false);
            }
            catch (Exception e){
                e.printStackTrace();
            }

            return root[0];
        }

        @Override
        protected void onPostExecute(Boolean root) {
            if (root) {
                text.setText(R.string.starting);
                Intent intent = new Intent(getApplicationContext(), ActivityPro.class);
                startActivity(intent);
            }
            else finishActivity(1);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
    }

    @Override
    protected void onResume() {
        super.onResume();
        text = findViewById(R.id.LoadText);
        MyTask task = new MyTask();
        task.execute(true);

    }

    boolean Getroot(final boolean att) {
        try {
            Process process = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            DataInputStream osRes = new DataInputStream(process.getInputStream());
            os.writeBytes("id -u\n");
            os.flush();
            Thread.sleep(400);
            boolean root = false;
            try{ root= osRes.readLine().equals("0");}
            catch (Exception e){System.exit(1);}
            if (root) {
                Log.i(UsefulThings.TAG, "Rooted!");
                return true;

            } else {
                Log.w(UsefulThings.TAG, "Root access rejected");
                if(att)Getroot(true);
                else {
                    Log.e(UsefulThings.TAG, "Can't get root access");
                    System.exit(1);
                }
            }
        } catch (Exception e) {
            Log.e(UsefulThings.TAG,e.toString());
            return false;
        }
        return false;
    }
}
