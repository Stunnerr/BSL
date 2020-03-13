package com.stunner.moderstars;

import android.content.Context;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static com.stunner.moderstars.ActivityPro.ctx;

public class UsefulThings {
    static String TAG = "BSL";
    public static List<Object> checked = new ArrayList<>();
    static String bspath;
    static Comparator<File> comp = new Comparator<File>() {
        @Override
        public int compare(File o1, File o2) {
            Boolean a = o1.isDirectory(), b = o2.isDirectory();
            if (a == b) return o1.getName().compareTo(o2.getName());
            return b.compareTo(a);
        }
    };
    static DataOutputStream ost;
    static BufferedReader ist;
    private static Runtime process;

    static File[] filelist(Context context, File folder) {
        File[] files = folder.listFiles();
        if (files != null)
            Arrays.sort(files, comp);
        return files;

    }

    private static String trimsome(String s) {
        Log.d(TAG, s);
        String s1 = s.split("/csv_logic/")[0];
        s1 = s1.split("/badge/")[0];
        s1 = s1.split("/csv_client/")[0];
        s1 = s1.split("/fonts/")[0];
        s1 = s1.split("/font/")[0];
        s1 = s1.split("/image/")[0];
        s1 = s1.split("/localization/")[0];
        s1 = s1.split("/music/")[0];
        s1 = s1.split("/sc/")[0];
        s1 = s1.split("/sc3d/")[0];
        s1 = s1.split("/sfx/")[0];
        s1 = s1.split("/shader/")[0];
        s1 = s1.split("/titan/")[0];
        s1 = s1.split("/fingerprint.json")[0];
        return s1.endsWith("/") ? (s1) : (s1 + "/");
    }

    static void Unzip(String str) {
        try {
            File file = new File(str);
            int b = checkmods(ctx).length;
            new File(ctx.getExternalFilesDir(null).getAbsolutePath()).mkdirs();
            ZipFile zipFile = new ZipFile(file);
            Enumeration entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry zipEntry = (ZipEntry) entries.nextElement();
                File file2 = new File((ctx.getExternalFilesDir(null).getAbsolutePath() + "/Mods/" + b + "/" + zipEntry.getName().replace(trimsome(zipEntry.getName()), "")));
                file2.getParentFile().mkdirs();
                if (!zipEntry.isDirectory()) {
                    Log.d(TAG, "Extracting " + file2);
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
            Log.e(TAG, "Error :" + e);
        }
    }

    static String sudo(String cmd) throws IOException {
        if (process == null) {
            process = Runtime.getRuntime();
        }
        process.exec("su");
        try {
            return new DataInputStream(process.exec(cmd).getInputStream()).readLine();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    static void copy(File src, File dst) throws IOException {
        if (process == null) {
            process = Runtime.getRuntime();
        }


        try {
            DataInputStream proces = new DataInputStream(process.exec("cp -r " + src.getAbsolutePath() + " " + dst.getAbsolutePath() + "").getInputStream());
            Log.d(TAG, "copy out: " + proces.readLine());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    static void copy(String src, String dst) {
        if (process == null) {
            process = Runtime.getRuntime();
        }
        try {
            DataInputStream proces = new DataInputStream(process.exec("cp -r " + src + " " + dst + "").getInputStream());
            Log.d(TAG, "copy out: " + proces.readLine());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static int modcount(Context context) {
        String d = context.getExternalFilesDir(null) + "/Mods";
        File file = new File(d);
        int count = -10;
        try {
            for (File x : file.listFiles()) {
                if (x.listFiles().length - 1 != -1) count++;
            }
        } catch (Exception e) {
            count = -10;
        }
        return count;
    }

    public static File[] checkmod(Context context, int modn) {
        modn--;
        String d = context.getExternalFilesDir(null) + "/Mods/" + modn;
        Log.d(TAG, d);
        File[] mods = new File(d).listFiles();
        if (mods != null)
            Arrays.sort(mods, comp);
        return mods;
    }

    static File[] checkmods(Context context) {
        String d = context.getExternalFilesDir(null) + "/Mods";
        new File(d).mkdirs();
        File[] mods = new File(d).listFiles();
        for (File i : mods)
            i.renameTo(new File(i.getParentFile().toString() + "/" + i.getName().toLowerCase()));
        mods = new File(d).listFiles();
        if (mods != null)
            Arrays.sort(mods, comp);
        return mods;
    }
}
