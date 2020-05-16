package com.stunner.moderstars;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.ArrayMap;
import android.util.Log;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.stunner.moderstars.pro.Models.ListChild;
import com.stunner.moderstars.pro.Models.ListParent;
import com.stunner.moderstars.signer.apksigner.Main;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import stunner.moderstars.R;

import static com.stunner.moderstars.ActivityPro.ctx;
import static com.stunner.moderstars.ActivityPro.showSnackBar;

public class UsefulThings {
    static final String TAG = "BSL";
    private static final Comparator<File> comp = new Comparator<File>() {
        @Override
        public int compare(File o1, File o2) {
            Boolean a = o1.isDirectory(), b = o2.isDirectory();
            if (a == b) return o1.getName().compareTo(o2.getName());
            return b.compareTo(a);
        }
    };
    public static FirebaseCrashlytics crashlytics;
    public static List<Object> checked = new ArrayList<>();
    static boolean root = false;
    static String su = "su -c ";
    static String bspath;
    static byte[] output;
    private static ProgressDialog pd;
    private static Runtime process;

    static File[] filelist(File folder) {
        File[] files = folder.listFiles();
        if (files != null)
            Arrays.sort(files, comp);
        return files;

    }

    private static String trimsome(String s) {
        //Log.d(TAG, s);
        String s1 = s.split("/csv_logic/")[0];
        s1 = s1.split("/badge/")[0];
        s1 = s1.split("/csv_client/")[0];
        s1 = s1.split("/font/")[0];
        s1 = s1.split("/image/")[0];
        s1 = s1.split("/localization/")[0];
        s1 = s1.split("/music/")[0];
        s1 = s1.split("/sc/")[0];
        s1 = s1.split("/sc3d/")[0];
        s1 = s1.split("/sfx/")[0];
        s1 = s1.split("/shader/")[0];
        s1 = s1.split("/fingerprint.json")[0];
        return s1.endsWith("/") ? (s1) : (s1 + "/");
    }

    public static String calculateSHA(File f) {
        try {
            return calculateSHA(new FileInputStream(f));
        } catch (Exception ignore) {
        }
        return "no";
    }
    public static String calculateSHA(InputStream is) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {

            Log.e(TAG, "Exception while getting digest", e);
            return null;
        }

        byte[] buffer = new byte[8192];
        int read;
        try {
            StringBuilder sb = new StringBuilder();
            while ((read = is.read(buffer)) > 0) {
                digest.update(buffer, 0, read);
            }
            byte[] md5sum = digest.digest();
            for (byte b : md5sum) {
                sb.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Unable to process file for MD5", e);
        } finally {
            try {
                is.close();
            } catch (Exception e) {
                Log.e(TAG, "Exception on closing MD5 input stream", e);
            }
        }
    }

    static String sudo(String cmd) {
        output = new byte[256];
        if (process == null) {
            process = Runtime.getRuntime();
        }
        try {
            new DataInputStream(process.exec(su + cmd).getInputStream()).readFully(output);
            return new String(output);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    static void copy(File src, File dst) {
        output = new byte[256];
        if (process == null) {
            process = Runtime.getRuntime();
        }
        try {
            new DataInputStream(process.exec(su + "cp -r " + src.getAbsolutePath() + " " + dst.getAbsolutePath() + "").getInputStream()).readFully(output);
        } catch (Exception e) {
            crashlytics.recordException(e);
        }

    }

    private static File[] checkmods(Context context) {
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

    static void copy(String src, String dst) {
        output = new byte[256];
        if (process == null) {
            process = Runtime.getRuntime();
        }
        try {
            if (root)
                new DataInputStream(process.exec(su + "cp -r " + src + " " + dst + "").getInputStream()).readFully(output);
            Log.d(TAG, "copy out: " + new String(output));
        } catch (Exception e) {
            crashlytics.recordException(e);
        }

    }

    static int modcount(Context context) {
        String d = context.getExternalFilesDir(null) + "/Mods";
        File file = new File(d);
        int count = 0;
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

    public static class Signer extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(ctx);
            pd.setCancelable(false);
            pd.setCanceledOnTouchOutside(false);
            pd.setTitle("BSL.Install");
            pd.setMessage(ctx.getString(R.string.installing).replace(":", "..."));
            pd.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                ZipInputStream zin = new ZipInputStream(new FileInputStream(ctx.getExternalFilesDir(null) + "/bs_original.apk"));
                ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(ctx.getExternalFilesDir(null) + "/bs_mod_unsigned.apk"));
                ZipEntry ze;
                boolean flag;
                while ((ze = zin.getNextEntry()) != null) {
                    try {
                        flag = false;
                        zout.putNextEntry(new ZipEntry(ze.getName()));
                        if (ze.getName().contains("assets/")) {
                            for (Object z : checked) {
                                String x = z.getClass() == ListChild.class ? ((ListChild) z).getforcopy() : ((ListParent) z).getforcopy();
                                if (x.matches(ctx.getExternalFilesDir(null) + "/(\\d+)/" + ze.getName().replace("assets/", ""))) {
                                    FileInputStream fis = new FileInputStream(x);
                                    ZipEntry entry = new ZipEntry(x);
                                    zout.putNextEntry(entry);
                                    publishProgress(ctx.getString(R.string.installing) + x);
                                    byte[] buffer = new byte[16384];
                                    while (fis.read(buffer) != -1) {
                                        zout.write(buffer);
                                    }
                                    zout.closeEntry();
                                    break;
                                }
                                flag = true;
                            }
                            if (flag) continue;
                        }
                        if (ze.getName().contains("META-INF")) continue;
                        publishProgress(ctx.getString(R.string.installing) + ze.getName());
                        byte[] buffer = new byte[16384];
                        while (zin.read(buffer) != -1) zout.write(buffer);
                        zout.closeEntry();
                    } catch (Exception e) {
                        crashlytics.recordException(e);
                        publishProgress(e.getMessage());
                    }
                }
                publishProgress(ctx.getString(R.string.signing));
                String[] strings1 = new String[3];
                strings1[0] = ctx.getExternalFilesDir(null) + "/sign";
                strings1[1] = ctx.getExternalFilesDir(null) + "/bs_mod_unsigned.apk";
                strings1[2] = ctx.getExternalFilesDir(null) + "/bs_mod_signed.apk";
                Main.main(strings1);
            } catch (Exception e) {
                crashlytics.recordException(e);
                Log.e(TAG, "Signing: ", e);
                //e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            pd.setMessage(values[0]);
        }

        @Override
        protected void onPostExecute(String ret) {
            pd.dismiss();
            showSnackBar(ret == null ? ctx.getString(R.string.success) : ret);
        }
    }

    public static class Unzipper extends AsyncTask<String, String, Void> {

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(ctx);
            pd.setTitle("BSL");
            pd.setMessage(ctx.getString(R.string.extracting).replace(":", "..."));
            pd.setCancelable(false);
            pd.setCanceledOnTouchOutside(false);
            pd.show();
        }

        @Override
        protected Void doInBackground(String... zapk) {
            for (String str : zapk) {
                publishProgress(str);
                try (ZipFile zip = new ZipFile(new File(str))) {
                    if (zip.getEntry("classes.dex") != null) {
                        if (zip.getEntry("assets/fingerprint.json") != null) {//unapk
                            try {
                                File file = new File(str);
                                int b = checkmods(ctx).length;
                                ctx.getExternalFilesDir(null).mkdirs();
                                ZipFile zipFile = new ZipFile(file);
                                Enumeration entries = zipFile.entries();
                                ZipEntry zipEntry = zipFile.getEntry("assets/fingerprint.json");
                                File file2 = new File((ctx.getExternalFilesDir(null).getAbsolutePath() + "/Temp/" + b + "/" + zipEntry.getName().replace("assets/", "")));
                                file2.getParentFile().mkdirs();
                                StringBuilder json = new StringBuilder();
                                BufferedInputStream bufferedInputStream = new BufferedInputStream(zipFile.getInputStream(zipEntry));
                                byte[] bArr = new byte[8192];
                                BufferedOutputStream bufferedOutputStream;
                                while (true) {
                                    int read = bufferedInputStream.read(bArr);
                                    if (read == -1) {
                                        break;
                                    }
                                    json.append(new String(bArr));
                                }
                                bufferedInputStream.close();
                                File folder = file2.getParentFile();
                                JSONObject jsonObject = new JSONObject(json.toString());
                                JSONArray files = jsonObject.getJSONArray("files");
                                Map<String, String> list = new ArrayMap<>();
                                for (int i = 0; i < files.length(); ++i) {
                                    list.put(files.getJSONObject(i).getString("file").replace("\\/", "/"), files.getJSONObject(i).getString("sha"));
                                }
                                while (entries.hasMoreElements()) {
                                    publishProgress("Seaching for changed files");
                                    zipEntry = (ZipEntry) entries.nextElement();
                                    if (!zipEntry.getName().contains("assets/")) continue;
                                    file2 = new File((ctx.getExternalFilesDir(null).getAbsolutePath() + "/Mods/" + b + "/" + zipEntry.getName().replace("assets/", "")));
                                    if (zipEntry.isDirectory()) continue;
                                    if (zipEntry.getName().replace(trimsome(zipEntry.getName()), "").equals(zipEntry.getName()))
                                        continue;
                                    if (zipEntry.getName().contains("fingerprint.json")) continue;
                                    bufferedInputStream = new BufferedInputStream(zipFile.getInputStream(zipEntry));
                                    try {
                                        if (list.get(zipEntry.getName().replace("assets/", "")).equals(calculateSHA(bufferedInputStream)))
                                            continue;
                                    } catch (NullPointerException ignored) {
                                    }

                                    publishProgress(zipEntry.getName());
                                    file2.getParentFile().mkdirs();
                                    bufferedInputStream = new BufferedInputStream(zipFile.getInputStream(zipEntry));
                                    bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file2), 8192);
                                    while (true) {
                                        int read = bufferedInputStream.read(bArr);
                                        if (read == -1) {
                                            break;
                                        }
                                        bufferedOutputStream.write(bArr);
                                    }
                                    bufferedOutputStream.flush();
                                    bufferedOutputStream.close();
                                    bufferedInputStream.close();
                                }

                            } catch (Exception e) {
                                crashlytics.recordException(e);
                                Log.e(TAG, "Error: " + e);
                            }
                        } else {
                            cancel(true);
                            return null;
                        }
                    } else {//unzip
                        try {
                            File file = new File(str);
                            int b = checkmods(ctx).length;
                            new File(ctx.getExternalFilesDir(null).getAbsolutePath()).mkdirs();
                            ZipFile zipFile = new ZipFile(file);
                            Enumeration entries = zipFile.entries();
                            while (entries.hasMoreElements()) {
                                ZipEntry zipEntry = (ZipEntry) entries.nextElement();
                                //Log.d(TAG, "ZE: " + zipEntry.getName());
                                publishProgress(zipEntry.getName());
                                File file2 = new File((ctx.getExternalFilesDir(null).getAbsolutePath() + "/Mods/" + b + "/" + zipEntry.getName().replace(trimsome(zipEntry.getName()), "")));
                                file2.getParentFile().mkdirs();
                                if (!zipEntry.isDirectory()) {
                                    Log.d(TAG, "Extracting " + file2);
                                    BufferedInputStream bufferedInputStream = new BufferedInputStream(zipFile.getInputStream(zipEntry));
                                    byte[] bArr = new byte[1024];
                                    BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file2), 1024);
                                    while (true) {
                                        int read = bufferedInputStream.read(bArr);
                                        if (read == -1) {
                                            break;
                                        }
                                        bufferedOutputStream.write(bArr);
                                    }
                                    bufferedOutputStream.flush();
                                    bufferedOutputStream.close();
                                    bufferedInputStream.close();
                                }
                            }
                        } catch (Exception e) {
                            crashlytics.recordException(e);
                            Log.e(TAG, "Error :" + e);
                        }//unzip-end
                    }
                } catch (Exception e) {
                    crashlytics.recordException(e);
                    Log.e(TAG, "doInBackground: ", e);
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            pd.setMessage(ctx.getString(R.string.extracting) + values[0]);
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            ActivityPro.mTabsAdapter.notifyDataSetChanged();
            pd.cancel();
            super.onPostExecute(aVoid);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            pd.cancel();
        }
    }
}
