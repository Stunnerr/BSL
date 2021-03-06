package com.stunner.moderstars.ui.repo;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BulletSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;
import com.stunner.moderstars.DividerSpan;
import com.stunner.moderstars.R;
import com.stunner.moderstars.UsefulThings;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import static android.content.Context.DOWNLOAD_SERVICE;


public class RepoFragment extends Fragment {
    ModList modList;
    SwipeRefreshLayout refreshLayout;
    SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            new Update().execute((Void) null);
        }
    };

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_repo, container, false);
        refreshLayout = root.findViewById(R.id.root);
        RecyclerView rv = root.findViewById(R.id.recyclerView);
        rv.setAdapter(modList);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        refreshLayout.setOnRefreshListener(refreshListener);
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(true);
                refreshListener.onRefresh();
            }
        });
        return root;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        modList = new ModList();
        super.onCreate(savedInstanceState);
    }

    class Update extends AsyncTask<Void, Void, Boolean> {
        List<Map<String, String>> data = new ArrayList<>();
        List<List<Map<String, String>>> changelog = new ArrayList<>();

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            String json = "[]";
            try {
                URL url = new URL("https://github.com/Like6po/bssmodsconfigure/raw/master/conf.json");
                URLConnection urlConnection = url.openConnection();
                urlConnection.setConnectTimeout(5000);
                urlConnection.setReadTimeout(5000);
                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line + "\n");
                }
                json = stringBuilder.toString();
                JSONArray array = new JSONArray(json);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = array.getJSONObject(i);
                    Map<String, String> map = new HashMap<>();
                    map.put("id", object.getString("id"));
                    map.put("title", object.getString("title"));
                    map.put("desc", object.getString("desc"));
                    map.put("download", object.getString("download"));
                    data.add(map);
                    JSONArray changeloga = object.getJSONArray("changelog");
                    List<Map<String, String>> clog = new ArrayList<>();
                    for (int j = 0; j < changeloga.length(); j++) {
                        JSONObject objecta = changeloga.getJSONObject(j);
                        Map<String, String> map1 = new HashMap<>();
                        map1.put("version", objecta.getString("version"));
                        map1.put("changes", objecta.getString("changes"));
                        clog.add(map1);
                    }
                    changelog.add(clog);
                }
            } catch (IOException e) {
                return false;
            } catch (JSONException e) {
                UsefulThings.crashlytics.recordException(e);
                return null;
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean aVoid) {
            if (aVoid == null) {
                Toast.makeText(getActivity(), "An unexpected error occured", Toast.LENGTH_LONG).show();
            } else if (!aVoid) {
                Toast.makeText(getActivity(), "Check your internet connection", Toast.LENGTH_LONG).show();
            } else {
                modList.setData(data, changelog);
                modList.notifyDataSetChanged();
            }
            refreshLayout.setRefreshing(false);
        }
    }

    class ModList extends RecyclerView.Adapter<ModList.Mod> {
        int lastPosition = -1;
        Handler h;
        List<Map<String, String>> maps = new ArrayList<>();
        List<List<Map<String, String>>> changelog = new ArrayList<>();

        ModList() {
            h = new Handler();
        }

        void setData(List<Map<String, String>> data, List<List<Map<String, String>>> clog) {
            maps = data;
            changelog = clog;
        }

        @NonNull
        @Override
        public Mod onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.mod_element, parent, false);
            return new Mod(v);
        }

        @Override
        public void onBindViewHolder(@NonNull Mod holder, int position) {
            position = holder.getBindingAdapterPosition();
            holder.setData(
                    maps.get(position),
                    changelog.get(position));
            setAnimation(holder.itemView, position);
        }

        @Override
        public int getItemCount() {
            return maps.size();
        }

        @Override
        public void onViewDetachedFromWindow(@NonNull Mod holder) {
            super.onViewDetachedFromWindow(holder);
            holder.itemView.clearAnimation();
        }

        private void setAnimation(final View viewToAnimate, final int position) {
            // If the bound view wasn't previously displayed on screen, it's animated

            if (position > lastPosition) {
                viewToAnimate.setAlpha(0);
                h.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Animator y = ObjectAnimator.ofFloat(viewToAnimate, "translationY", -20, 0);
                        Animator a = ObjectAnimator.ofFloat(viewToAnimate, "alpha", 0, 1);
                        AnimatorSet set = new AnimatorSet();
                        set.setTarget(viewToAnimate);
                        y.setDuration(500);
                        a.setDuration(500);
                        y.setInterpolator(new AnticipateOvershootInterpolator());
                        a.setInterpolator(new AnticipateOvershootInterpolator());
                        set.playTogether(y, a);
                        set.start();
                    }
                }, 100);
            }
        }

        class Mod extends RecyclerView.ViewHolder {
            TextView text, status;
            ImageButton dl, info;
            String link = "https://example.com";
            String desc = "";
            List<Map<String, String>> changelog;
            View.OnClickListener i;
            int id = -1;

            Mod(@NonNull View itemView) {
                super(itemView);
                text = itemView.findViewById(R.id.title);
                dl = itemView.findViewById(R.id.dlbtn);
                dl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dl.setEnabled(false);
                        final int modnum = UsefulThings.getRepoModId(getContext(), id) == -1 ? UsefulThings.modСount(getContext()) : UsefulThings.getRepoModId(getContext(), id);
                        DownloadManager downloadmanager = (DownloadManager) getActivity().getSystemService(DOWNLOAD_SERVICE);
                        Uri uri = Uri.parse(link);
                        final DownloadManager.Request request = new DownloadManager.Request(uri);
                        request.setTitle(text.getText());
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
                        request.setVisibleInDownloadsUi(false);
                        request.setDestinationUri(Uri.parse("file://" + getActivity().getExternalCacheDir() + "/TempMod.zip"));
                        downloadmanager.enqueue(request);
                        dl.setOnClickListener(null);
                        getActivity().registerReceiver(new BroadcastReceiver() {
                            @Override
                            public void onReceive(Context context, Intent intent) {
                                try {
                                    dl.setVisibility(View.GONE);
                                    FilenameFilter ff = new FilenameFilter() {
                                        @Override
                                        public boolean accept(File dir, String name) {
                                            return name.contains("TempMod");
                                        }
                                    };
                                    int count = getActivity().getExternalCacheDir().list(ff).length;
                                    String name = getActivity().getExternalCacheDir() + "/TempMod" + (count > 1 ? "-" + (count - 1) : "") + ".zip";
                                    ZipFile zf = new ZipFile(name);
                                    Enumeration entries = zf.entries();
                                    while (entries.hasMoreElements()) {
                                        ZipEntry zipEntry = (ZipEntry) entries.nextElement();
                                        File file2 = new File(getActivity().getExternalFilesDir(null).getAbsolutePath() + "/Mods/" + modnum + "/" + zipEntry.getName().replace(UsefulThings.trimFolderName(zipEntry.getName()), ""));
                                        file2.getParentFile().mkdirs();
                                        if (!zipEntry.isDirectory()) {
                                            BufferedInputStream bufferedInputStream = new BufferedInputStream(zf.getInputStream(zipEntry));
                                            byte[] bArr = new byte[1024];
                                            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file2), 1024);
                                            while (true) {
                                                int read = bufferedInputStream.read(bArr);
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
                                    new File(name).delete();
                                    PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putString("repo" + id, changelog.get(0).get("version")).apply();
                                    UsefulThings.setModName(getContext(), modnum, text.getText().toString());
                                    UsefulThings.setRepoModId(getContext(), modnum, id);
                                } catch (Exception e) {
                                    UsefulThings.crashlytics.recordException(e);
                                }

                            }
                        }, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                    }
                });
                info = itemView.findViewById(R.id.ibtn);
                info.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MaterialAlertDialogBuilder b = new MaterialAlertDialogBuilder(getView().getContext());
                        LayoutInflater inflater = getLayoutInflater();
                        View layout = inflater.inflate(R.layout.repo_mod_info, null);
                        String d = getString(R.string.changelog);
                        int start = 0;
                        SpannableStringBuilder string = new SpannableStringBuilder(text.getText() + "\n\n" + desc + "\n\n" + d + "\n\n");
                        string.setSpan(new RelativeSizeSpan(2f), start, text.getText().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        start = text.getText().length() + desc.length() + 4;
                        //string.setSpan(new RelativeSizeSpan(1), text.getText().length(), end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        string.setSpan(new RelativeSizeSpan(1.5f), start, start + d.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        string.setSpan(new DividerSpan(), start, start + d.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        start += d.length() + 2;
                        for (Map<String, String> changes : changelog) {
                            string.append(getString(R.string.version) + changes.get("version"));
                            string.append("\n");
                            string.setSpan(new RelativeSizeSpan(1.3f), start, start + (getString(R.string.version) + changes.get("version")).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            start += (getString(R.string.version) + changes.get("version")).length() + 1;
                            String change = changes.get("changes");
                            for (String a : change.split("\n")) {
                                string.append(a + "\n");
                                string.setSpan(new BulletSpan(20), start, start + a.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                string.setSpan(new RelativeSizeSpan(1.1f), start, start + a.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                start += a.length() + 2;
                            }
                            string.append("\n");
                        }
                        MaterialTextView ndesc = layout.findViewById(R.id.ndesc);
                        ndesc.setText(string, TextView.BufferType.SPANNABLE);
                        b.setView(layout);
                        b.setNegativeButton("OK", null);
                        b.show();
                    }
                });
            }

            void setData(Map<String, String> map, List<Map<String, String>> clog) {
                text.setText(map.get("title"));
                link = map.get("download");
                desc = map.get("desc");
                id = Integer.parseInt(map.get("id"));
                changelog = clog;

                if (PreferenceManager.getDefaultSharedPreferences(getContext()).getString("repo" + id, "").equals(clog.get(0).get("version"))) {
                    dl.setVisibility(View.GONE);
                }
            }

        }

    }
}