package com.stunner.moderstars.pro.Adapters;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.stunner.moderstars.ActivityPro;
import com.stunner.moderstars.pro.Models.TitleChild;
import com.stunner.moderstars.pro.Models.TitleParent;
import stunner.moderstars.R;
import com.stunner.moderstars.pro.ViewHolders.TitleChildViewHolder;
import com.stunner.moderstars.pro.ViewHolders.TitleParentViewHolder;

import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.Model.ParentObject;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends ExpandableRecyclerAdapter<TitleParentViewHolder, TitleChildViewHolder> {
    TitleParent title;
    LayoutInflater inflater;
    List<Boolean> checked = new ArrayList<>();
    List<Integer> parents = new ArrayList<>();
    List<Integer> lastchildren = new ArrayList<>();
    List<TitleParentViewHolder> parentvh = new ArrayList<>();
    List<TitleChildViewHolder> childvh = new ArrayList<>();

    public List<List<String>> folders = new ArrayList<>();
    public List<String> files = new ArrayList<>();

    public MyAdapter(Context context, List<ParentObject> parentItemList) {
        super(context, parentItemList);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public TitleParentViewHolder onCreateParentViewHolder(ViewGroup viewGroup) {
        View view = inflater.inflate(R.layout.list_parent, viewGroup, false);
        return new TitleParentViewHolder(view);
    }

    @Override
    public TitleChildViewHolder onCreateChildViewHolder(ViewGroup viewGroup) {
        View view = inflater.inflate(R.layout.list_child, viewGroup, false);
        return new TitleChildViewHolder(view);

    }

    @Override
    public void onBindParentViewHolder(TitleParentViewHolder holder, final int i, Object o) {
        title = (TitleParent) o;

        parents.add(i);
        parentvh.add(holder);
        Log.i(ActivityPro.tag,"parent id:" +String.valueOf(i)+ " name:" + title.getTitle() + " obj:" + o.toString());
        if (checked.size()<i+1) checked.add(i,false);
        holder._textView.setText(title.getTitle());

        holder._checkBox.setChecked(checked.get(i));
        holder._checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checked.set(i,!checked.get(i));
                for(int x=i;x<childvh.size();x++) {
                    checked.set(x, checked.get(i));
                    childvh.get(x).checkBox1.setChecked(checked.get(i));
                }
            }}
        );
    }


    @Override
    public void onBindChildViewHolder(TitleChildViewHolder holder, final int i, Object o) {
        TitleChild title = (TitleChild) o;
        final boolean[] d={false};
        childvh.add(holder);
        Log.i(ActivityPro.tag,"child id:" +String.valueOf(i)+ " name:" + title.getOption1() + " obj:" + o.toString() + " Holder:" + holder.toString());
        final int[] parent={0};
        for (int x = 0;x<lastchildren.size();x++) {
            if (i>=x) {parent[0]=x;break;}
        }
        if (checked.size() < i+1) checked.add(false);
        if (checked.get(parent[0]))holder.checkBox1.setChecked(true);
        else holder.checkBox1.setChecked(checked.get(i));
        holder.option1.setText(title.getOption1());
        holder.checkBox1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checked.get(parent[0]))
                {
                    parentvh.get(parent[0])._checkBox.setChecked(false);
                    checked.set(parent[0],false);
                    checked.set(i,!checked.get(i));
                }
                else {
                    checked.set(i,!checked.get(i));
                    for (int x = parent[0]+1; x < checked.size(); x++){
                    if (checked.get(x)) d[0]=true;
                    else {d[0]=false;break;}
                }
                if(d[0]) {parentvh.get(parent[0])._checkBox.setChecked(true); checked.set(parent[0],true);}
                }
            }
        });
    }

    @Override
    public Bundle onSaveInstanceState(Bundle savedInstanceStateBundle) {
        return super.onSaveInstanceState(savedInstanceStateBundle);
    }




    public List<Boolean> getChecked() {
        return checked;
    }
}