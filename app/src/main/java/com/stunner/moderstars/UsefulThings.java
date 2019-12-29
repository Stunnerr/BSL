package com.stunner.moderstars;

import android.content.Context;
import android.util.Log;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import androidx.annotation.Nullable;

import static com.stunner.moderstars.ActivityPro.ctx;
import static com.stunner.moderstars.ActivityPro.tag;

public class UsefulThings {
    static String bspath;
    static File[] filelist(Context context, File folder){
        File[] files = folder.listFiles();
        Arrays.sort(files);
        return files;

    }
    static void Unzip(String str) {
        try {
            File file = new File(str);
            new File(ctx.getExternalFilesDir(null).getAbsolutePath()).mkdir();
            ZipFile zipFile = new ZipFile(file);
            Enumeration entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry zipEntry = (ZipEntry) entries.nextElement();
                File file2 = new File((ctx.getExternalFilesDir(null).getAbsolutePath() +"/Mods/" + zipEntry.getName()).replace("/update", ""));
                file2.getParentFile().mkdirs();
                if (!zipEntry.isDirectory()) {
                    Log.d("Brawl Mods","Extracting " + file2);
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
            Log.e(tag,"Error :" + e);
        }
    }

    public static int modcount(Context context){ return new File (context.getExternalFilesDir(null)+ "/Mods").listFiles().length;}
    static String sudo(String cmd) throws IOException{
        return new DataInputStream(Runtime.getRuntime().exec("su -c " +cmd).getInputStream()).readLine();
    }
    static void copy(File src, File dst) throws IOException {
        //Log.d(tag, "src: " + src.getAbsolutePath() + " dst: " + dst.getAbsolutePath());
        DataInputStream process = new DataInputStream(Runtime.getRuntime().exec("su -c cp -r " +src.getAbsolutePath()+" "+dst.getAbsolutePath()+ "").getInputStream());
        //Log.d(tag, "copy out:"+process.readLine());
    }

    public static File[] checkmods(Context context, int modn){
        String d = context.getExternalFilesDir(null) + "/Mods/" + modn;
         new File(d).mkdir();
        @Nullable File[] mods = new File(d).listFiles();
        Arrays.sort(mods);
        return mods;
    }
    static File[] checkmods(Context context){
        String d = context.getExternalFilesDir(null) + "/Mods";
        new File(d).mkdir();
        File[] mods = new File(d).listFiles();
        for (File i: mods)i.renameTo(new File(i.getParentFile().toString() +"/"+i.getName().toLowerCase() ));
        mods = new File(d).listFiles();
        Arrays.sort(mods);
        return mods;
    }

}
