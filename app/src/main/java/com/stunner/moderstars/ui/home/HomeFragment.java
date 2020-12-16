package com.stunner.moderstars.ui.home;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bignerdranch.expandablerecyclerview.Model.ParentObject;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.stunner.moderstars.R;
import com.stunner.moderstars.UsefulThings;
import com.stunner.moderstars.modlist.adapters.ModListAdapter;
import com.stunner.moderstars.modlist.models.ModListFile;
import com.stunner.moderstars.modlist.models.ModListFolder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import static android.app.Activity.RESULT_OK;
import static com.stunner.moderstars.UsefulThings.TAG;
import static com.stunner.moderstars.UsefulThings.crashlytics;
import static com.stunner.moderstars.UsefulThings.getModName;
import static com.stunner.moderstars.UsefulThings.modСount;

public class HomeFragment extends Fragment {
    public static TabsAdapter mTabsAdapter;
    static TabLayout mTabLayout;
    static ViewPager mViewPager;
    static FloatingActionButton fab;
    String[] supportedMimeTypes = {"application/zip", "application/vnd.android.package-archive"};

    private void choosezip() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, supportedMimeTypes);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Выберите архив мода"),
                    13);
        } catch (android.content.ActivityNotFoundException e) {
            crashlytics.recordException(e);
            Toast.makeText(getActivity(), "Установите файловый менеджер!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    String getPath(Uri uri) {
        File a = new File(getContext().getExternalCacheDir().getAbsolutePath() + "/");
        a.mkdirs();
        File file = new File(getContext().getExternalCacheDir().getAbsolutePath() + "/temp" + a.list().length + ".zip");
        try {
            InputStream inputStream = getContext().getContentResolver().openInputStream(uri);
            FileOutputStream outputStream = new FileOutputStream(file);
            final byte[] buffers = new byte[8192];
            while (inputStream.read(buffers) != -1) {
                outputStream.write(buffers);
            }
            inputStream.close();
            outputStream.close();
        } catch (Exception e) {
            crashlytics.recordException(e);
        }
        return file.getAbsolutePath();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 13:
                if (resultCode == RESULT_OK) {
                    String[] paths;
                    if (null != data.getClipData()) {                                                 // checking multiple selection or not
                        paths = new String[data.getClipData().getItemCount()];
                        for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                            Uri uri = data.getClipData().getItemAt(i).getUri();
                            Log.d(TAG, "File Uri: " + uri.toString());
                            String path = getPath(uri);
                            Log.d(TAG, "onActivityResult: " + path);
                            paths[i] = path;
                        }
                    } else {
                        Uri uri = data.getData();
                        Log.d(TAG, "File Uri: " + uri.toString());
                        String path = getPath(uri);
                        Log.d(TAG, "onActivityResult: " + path);
                        paths = new String[]{path};
                    }
                    new UsefulThings.Unzipper().execute(paths);
                }
                break;
            default:
                Log.e(TAG, "Unexpected value: " + requestCode);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_home, container, false);
        fab = rootview.findViewById(R.id.fab);
        mViewPager = rootview.findViewById(R.id.viewpager);
        mTabLayout = rootview.findViewById(R.id.tabs);
        mTabsAdapter = new TabsAdapter(getActivity().getSupportFragmentManager());
        mViewPager.setAdapter(mTabsAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabsAdapter.notifyDataSetChanged();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosezip();
            }
        });
        return rootview;
    }

    public static class TabsFragment extends Fragment {
        private static final String ARG_PAGE = "section_number";
        ModListAdapter adapter;

        public TabsFragment() {
        }

        private List<ParentObject> initData(int modc) {
            int folc;
            List<ParentObject> parentObject = new ArrayList<>();
            folc = UsefulThings.getMod(getContext(), modc).length - 1;
            if (folc != -1) {
                List<ModListFolder> parents = initParents(modc);
                for (int i = 0; i < folc + 1; i++) {
                    List<Object> childList = new ArrayList<>();
                    if (UsefulThings.listFiles(UsefulThings.getMod(getContext(), modc)[i]) != null) {
                        for (File file : UsefulThings.listFiles(UsefulThings.getMod(getContext(), modc)[i])) {
                            childList.add(new ModListFile(file, modc));
                        }
                    }
                    parents.get(i).setChildObjectList(childList);
                    parentObject.add(parents.get(i));

                }
                return parentObject;
            } else return null;
        }

        List<ModListFolder> initParents(int modc) {
            List<ModListFolder> parents = new ArrayList<>();
            for (File e : UsefulThings.getMod(getContext(), modc))
                parents.add(new ModListFolder(e, modc));
            return parents;
        }

        static TabsFragment newInstance(int sectionNumber) {
            TabsFragment fragment = new TabsFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_PAGE, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            int mPage = modСount(getContext());
            if (getArguments() != null) {
                mPage = getArguments().getInt(ARG_PAGE);
            }
            adapter = new ModListAdapter(getContext(), initData(mPage));
            adapter.setParentClickableViewAnimationDefaultDuration();
            adapter.setParentAndIconExpandOnClick(true);
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.mod_tab, container, false);
            RecyclerView recyclerView = rootView.findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(adapter);
            return rootView;

        }
    }

    public class TabsAdapter extends FragmentStatePagerAdapter {

        TabsAdapter(FragmentManager fm) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @Override
        public int getCount() {
            return UsefulThings.modСount(getContext());
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return TabsFragment.newInstance(position + 1);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return getModName(getContext(), position);
        }

        @Override
        public void notifyDataSetChanged() {
            if (getCount() > 0) {
                mTabLayout.setVisibility(View.VISIBLE);
            } else {
                mTabLayout.setVisibility(View.INVISIBLE);
            }
            super.notifyDataSetChanged();
        }
    }
}