package com.stunner.moderstars;

import android.content.Context;
import android.util.Log;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import androidx.annotation.Nullable;

import static com.stunner.moderstars.ActivityPro.ctx;

public class UsefulThings {
    public static String TAG = "Brawl Mods";
    public static List<Object> checked = new ArrayList<>();
    static String bspath;
    static File[] filelist(Context context, File folder){
        File[] files = folder.listFiles();
        Arrays.sort(files);
        return files;

}
    static String trimsome(String s){
        Log.d(TAG, s);
        String s1 = s.split("/csv_logic/")[0];
        s1 =s1.split("/badge/")[0];
        s1 =s1.split("/csv_client/")[0];
        s1 =s1.split("/fonts/")[0];
        s1 =s1.split("/font/")[0];
        s1 =s1.split("/image/")[0];
        s1 =s1.split("/localization/")[0];
        s1 =s1.split("/music/")[0];
        s1 =s1.split("/sc/")[0];
        s1 =s1.split("/sc3d/")[0];
        s1 =s1.split("/sfx/")[0];
        s1 =s1.split("/shader/")[0];
        s1 =s1.split("/titan/")[0];
        return s1.endsWith("/")?(s1):(s1+"/");
    }
    static void Unzip(String str) {
        try {
            File file = new File(str);
            new File(ctx.getExternalFilesDir(null).getAbsolutePath()).mkdir();
            ZipFile zipFile = new ZipFile(file);
            Enumeration entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry zipEntry = (ZipEntry) entries.nextElement();
                //Log.d(TAG,"Trimmed: /" + trimsome(zipEntry.getName()));
                File file2 = new File((ctx.getExternalFilesDir(null).getAbsolutePath() +"/Mods/"+checkmods(ctx).length + zipEntry.getName().replace("/" + trimsome(zipEntry.getName()), "")));
               // Log.d(TAG, file2.toString());
                file2.getParentFile().mkdirs();
                if (!zipEntry.isDirectory()) {
                    Log.d(TAG,"Extracting " + file2);
                    BufferedInputStream bufferedInputStream = new BufferedInputStream(zipFile.getInputStream(zipEntry));
                    byte[] bArr = new byte[1024];
                    BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file2), 1024);
                    while (true) {
                        int read = bufferedInputStream.read(bArr, 0, 1024);
                        if (read == -1) {
                            break;
                        }
                        bufferedOutputStream.write(bArr, 0, read);
                    }
                    bufferedOutputStream.flush();
                    bufferedOutputStream.close();
                    bufferedInputStream.close();
                }
            }
        } catch (Exception e) {
            Log.e(TAG,"Error :" + e);
        }
    }

    static String sudo(String cmd) throws IOException{
        return new DataInputStream(Runtime.getRuntime().exec("su -c " +cmd).getInputStream()).readLine();
    }
    static void copy(File src, File dst) throws IOException {
        //Log.d(TAG, "src: " + src.getAbsolutePath() + " dst: " + dst.getAbsolutePath());
        DataInputStream process = new DataInputStream(Runtime.getRuntime().exec("su -c cp -r " +src.getAbsolutePath()+" "+dst.getAbsolutePath()+ "").getInputStream());
        //Log.d(TAG, "copy out:"+process.readLine());
    }

    public static int modcount(Context context){
        String d = context.getExternalFilesDir(null) + "/Mods";
        File file = new File(d);
        int count = 0;
        for (File x:file.listFiles()) {
            if (x.listFiles().length-1!=-1) count++;
        }
        return count;
    }

    public static File[] checkmod(Context context, int modn){
        modn--;
        String d = context.getExternalFilesDir(null) + "/Mods/" + modn;
        Log.d(TAG, d);
        @Nullable File[] mods = new File(d).listFiles();
        Arrays.sort(mods);
        return mods;
    }
    static File[] checkmods(Context context){
        String d = context.getExternalFilesDir(null) + "/Mods";
        new File(d).mkdirs();
        File[] mods = new File(d).listFiles();
        for (File i: mods)i.renameTo(new File(i.getParentFile().toString() +"/"+i.getName().toLowerCase() ));
        mods = new File(d).listFiles();
        Arrays.sort(mods);
        return mods;
    }

}
