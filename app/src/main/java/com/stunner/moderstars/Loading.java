package com.stunner.moderstars;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import stunner.moderstars.R;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
public class Loading extends AppCompatActivity {
    TextView text;
    private class MyTask extends AsyncTask <Boolean, Boolean, Boolean> {
        @Override
        protected Boolean doInBackground(Boolean... root) {
            root[0] = false;
            try{
                root[0] = Getroot(1);
            }
            catch (Exception e){
                e.printStackTrace();
            }

            return root[0];
        }

        @Override
        protected void onPostExecute(Boolean root) {
            if (root) {
                text.setText("Запуск...");
                Intent intent = new Intent(getApplicationContext(), ActivityPro.class);
                startActivity(intent);
            }
            else System.exit(1);
        }
    }
    String tag = "Brawl Mods";
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

    boolean Getroot(final int att) {
        try {
            Log.i(tag, "Trying to get root attemp " + att);
            Process process = Runtime.getRuntime().exec(new String[]{"su","id"});
            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            DataInputStream osRes = new DataInputStream(process.getInputStream());
            os.writeBytes("id\n");
            os.flush();
            Thread.sleep(400);
            Log.d(tag, "Write \"id\"");
            boolean root = osRes.readLine().contains("uid=0");
            if (root) {
                Log.i(tag, "Rooted!");
                return root;

            } else {
                Log.w(tag, "Root access rejected");
                if (att <= 5) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Brawl Mods")
                            .setMessage("Не удалось получить права root! Повторить попытку?")
                            .setCancelable(false)
                            .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    try {
                                        Getroot(1 + att);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            })
                            .setNegativeButton("Нет",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            System.exit(0);
                                        }
                                    });
                    AlertDialog alert = builder.create();
                    alert.show();

                } else {
                    Log.e(tag, "Can't get root access");
                    System.exit(0);
                }

            }
        } catch (Exception e) {
            Log.e(tag,e.toString());
            return false;
        }
        return false;
    }
}
