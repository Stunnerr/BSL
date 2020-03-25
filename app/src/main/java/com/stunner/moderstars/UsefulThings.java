package com.stunner.moderstars;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.ArrayMap;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
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

import stunner.moderstars.R;

import static com.stunner.moderstars.ActivityPro.ctx;

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
    static Unzipper unzipper = new Unzipper();
    private static ProgressDialog pd;
    public static List<Object> checked = new ArrayList<>();
    static String bspath;

    static File[] filelist(File folder) {
        File[] files = folder.listFiles();
        if (files != null)
            Arrays.sort(files, comp);
        return files;

    }

    private static Runtime process;

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

    private static String calculateSHA(InputStream is) {
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

    static byte[] output;

    static String sudo(String cmd) {
        output = new byte[256];
        if (process == null) {
            process = Runtime.getRuntime();
        }
        try {
            new DataInputStream(process.exec("su -c " + cmd).getInputStream()).readFully(output);
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
            Log.d(TAG, "su -c cp -r " + src.getAbsolutePath() + " " + dst.getAbsolutePath() + "");
            new DataInputStream(process.exec("su -c cp -r " + src.getAbsolutePath() + " " + dst.getAbsolutePath() + "").getInputStream()).readFully(output);
            Log.d(TAG, "copy out: " + new String(output));
        } catch (Exception e) {
            e.printStackTrace();
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
            new DataInputStream(process.exec("su -c cp -r " + src + " " + dst + "").getInputStream()).readFully(output);
            Log.d(TAG, "copy out: " + new String(output));
        } catch (Exception e) {
            e.printStackTrace();
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
            String str = zapk[0];
            try (ZipFile zip = new ZipFile(zapk[0])) {
                if (zip.getEntry("classes.dex") != null) {
                    if (zip.getEntry("assets/fingerprint.json") != null) {//unapk
                        try {
                            File file = new File(str);
                            int b = checkmods(ctx).length;
                            ctx.getExternalFilesDir(null).mkdirs();
//             unzip-start
                            ZipFile zipFile = new ZipFile(file);
                            Enumeration entries = zipFile.entries();
                            ZipEntry zipEntry = zipFile.getEntry("assets/fingerprint.json");
                            File file2 = new File((ctx.getExternalFilesDir(null).getAbsolutePath() + "/Temp/" + b + "/" + zipEntry.getName().replace("assets/", "")));
                            file2.getParentFile().mkdirs();
                            StringBuilder json = new StringBuilder();
                            BufferedInputStream bufferedInputStream = new BufferedInputStream(zipFile.getInputStream(zipEntry));
                            byte[] bArr = new byte[8192];
                            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file2), 1024);
                            while (true) {
                                int read = bufferedInputStream.read(bArr, 0, 8192);
                                if (read == -1) {
                                    break;
                                }
                                json.append(new String(bArr));
                            }
                            bufferedOutputStream.flush();
                            bufferedOutputStream.close();
                            bufferedInputStream.close();
                            File folder = file2.getParentFile();
                            JSONObject jsonObject = new JSONObject(json.toString());
                            JSONArray files = jsonObject.getJSONArray("files");
                            // List<Pair<String,String>> list = new ArrayList<>();
                            Map<String, String> list = new ArrayMap<>();
                            for (int i = 0; i < files.length(); ++i) {
                                list.put(files.getJSONObject(i).getString("file").replace("\\/", "/"), files.getJSONObject(i).getString("sha"));
                            }
                            while (entries.hasMoreElements()) {
                                zipEntry = (ZipEntry) entries.nextElement();
                                if (!zipEntry.getName().contains("assets/")) continue;
                                file2 = new File((ctx.getExternalFilesDir(null).getAbsolutePath() + "/Mods/" + b + "/" + zipEntry.getName().replace("assets/", "")));
                                if (zipEntry.isDirectory()) continue;
                                if (zipEntry.getName().replace(trimsome(zipEntry.getName()), "").equals(zipEntry.getName()))
                                    continue;
                                //Log.d(TAG, "Extracting " + file2);
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
                                    int read = bufferedInputStream.read(bArr, 0, 8192);
                                    if (read == -1) {
                                        break;
                                    }
                                    bufferedOutputStream.write(bArr, 0, read);
                                }
                                bufferedOutputStream.flush();
                                bufferedOutputStream.close();
                                bufferedInputStream.close();
                            }
                            //unzip-end temp copy-start
                            Log.d(TAG, "doInBackground: cleared");

                        } catch (Exception e) {
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
                    }//unzip-end
                }
            } catch (Exception e) {
                Log.e(TAG, "doInBackground: ", e);
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
