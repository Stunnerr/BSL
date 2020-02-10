package com.stunner.moderstars;



import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import stunner.moderstars.R;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.bignerdranch.expandablerecyclerview.Model.ParentObject;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.stunner.moderstars.pro.Adapters.RecyclerViewAdapter;
import com.stunner.moderstars.pro.Models.ListChild;
import com.stunner.moderstars.pro.Models.ListParent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.stunner.moderstars.UsefulThings.TAG;

public class ActivityEasy extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;
    private TabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_easy);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar2));
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = findViewById(R.id.viewpager2);
        mTabLayout = findViewById(R.id.tabs2);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        FloatingActionButton fab = findViewById(R.id.fab2);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(findViewById(R.id.fab2), "test", BaseTransientBottomBar.LENGTH_LONG).show();
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_release, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_alpha) {
            startActivity(new Intent(getApplicationContext(),ActivityPro.class));
        }

        return super.onOptionsItemSelected(item);
    }

    public static class TabsFragment extends Fragment {
        private int mPage;
        private static final String ARG_PAGE = "section_number";
        RecyclerViewAdapter adapter;
        public TabsFragment() {
        }
        public static TabsFragment newInstance(int sectionNumber) {
            Log.d(TAG, "newInstance: run");
            TabsFragment fragment = new TabsFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_PAGE, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override public void onCreate(Bundle savedInstanceState) {
            if (getArguments() != null) {
                mPage = getArguments().getInt(ARG_PAGE);
            }
            super.onCreate(savedInstanceState);
            adapter = new RecyclerViewAdapter(getContext(), initData(mPage));
            adapter.setParentClickableViewAnimationDefaultDuration();
            adapter.setParentAndIconExpandOnClick(true);
        }
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_easy, container, false);
            RecyclerView recyclerView = rootView.findViewById(R.id.recyclerView2);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(adapter);
            return rootView;

        }

        List<ListParent> initParents(int modc){
            List<ListParent> parents = new ArrayList<>();
            for (File e:UsefulThings.checkmod(getContext(), modc)) parents.add(new ListParent(e, modc));
            return parents;
        }
        private List<ParentObject> initData(int modc) {
            int folc;
            List<ParentObject> parentObject = new ArrayList<>();
            folc = UsefulThings.checkmod(getContext(), modc).length-1;
            if (folc != -1) {

                List<ListParent> parents = initParents(modc);
                for (int i = 0; i < folc; i++) {
                    List<Object> childList = new ArrayList<>();
                    if (UsefulThings.filelist(getContext(), UsefulThings.checkmod(getContext(), modc)[i]) != null){
                        for (File file : UsefulThings.filelist(getContext(), UsefulThings.checkmod(getContext(), modc)[i])) {//список файлов
                            childList.add(new ListChild(file, modc));

                        }
                    }
                    if (childList.isEmpty()) childList.add("Это файл");
                    parents.get(i).setChildObjectList(childList);
                    parentObject.add(parents.get(i));

                }
                return parentObject;
            } else return null;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a TabsFragment (defined as a static inner class below).
            return TabsFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return UsefulThings.modcount(getApplicationContext());
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "Mod #"+ (position+1);
        }
    }
}
