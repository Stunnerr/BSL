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

    private LayoutInflater inflater;
    public static List<Object> checked = new ArrayList<>();
    private static List<TitleParent> parents = new ArrayList<>();

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
    public void onBindParentViewHolder(final TitleParentViewHolder holder, final int i,final Object o) {
        final TitleParent title = (TitleParent) o;
        title.setCurholder(holder);
        parents.add(title);
        //Log.i(ActivityPro.TAG,"parent id:" +String.valueOf(i)+ " name:" + title.getTitle() + " obj:" + o.toString());
        holder._textView.setText(title.getTitle());
        holder._checkBox.setChecked(checked.indexOf(title)!=-1);
        holder.layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (checked.indexOf(title)==-1) {
                    holder._checkBox.setChecked(true);
                    checked.add(title);
                    for (Object x:title.getChildObjectList()) {
                        TitleChild child = (TitleChild) x;
                        if(child.getCurholder()!=null) child.getCurholder().checkBox1.setChecked(true);
                        checked.add(child);
                    }
                }
                else {holder._checkBox.setChecked(false);checked.remove(title);}
                return true;
            }}
        );
        holder._checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checked.indexOf(title)==-1) {
                    checked.add(title);
                    for (Object x:title.getChildObjectList()) {
                        TitleChild child = (TitleChild) x;
                        if(child.getCurholder()!=null) child.getCurholder().checkBox1.setChecked(true);
                        checked.add(child);
                    }
                }
                else checked.remove(title);

            }}
        );
    }


    @Override
    public void onBindChildViewHolder(final TitleChildViewHolder holder, final int i, final Object o) {
        final TitleChild title = (TitleChild) o;
        final boolean[] d = {false};
        final TitleParent[] parent = {null};
        for (TitleParent x: parents) {if(x.getChildObjectList().indexOf(title)!=-1) {parent[0]=x;break;}}
        title.setCurholder(holder);
        holder.option1.setText(title.getOption1());
        //Log.i(ActivityPro.TAG, "child id:" + String.valueOf(i) + " name:" + title.getOption1() + " obj:" + o.toString() + " Holder:" + holder.toString());
        holder.checkBox1.setChecked(checked.indexOf(title)!=-1);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checked.indexOf(title) == -1)
                {
                    holder.checkBox1.setChecked(true);
                    checked.add(title);
                    for (Object x:parent[0].getChildObjectList()){
                        TitleChild child = (TitleChild) x;
                        if (checked.indexOf(child)!=-1)d[0]=true;
                        else  {d[0]=false;break;}
                    }
                    if (d[0]) {parent[0].getCurholder()._checkBox.setChecked(true);checked.add(parent[0]);}

                }
                else {
                    holder.checkBox1.setChecked(false);
                    checked.remove(title);
                    if (checked.indexOf(parent[0])!=-1){
                        checked.remove(parent[0]);
                        parent[0].getCurholder()._checkBox.setChecked(false);
                    }
                }
            }
        });
        holder.checkBox1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checked.indexOf(title) == -1)
                {
                    checked.add(title);
                    for (Object x:parent[0].getChildObjectList()){
                        TitleChild child = (TitleChild) x;
                        if (checked.indexOf(child)!=-1)d[0]=true;
                        else  {d[0]=false;break;}
                    }
                    if (d[0]) {parent[0].getCurholder()._checkBox.setChecked(true);checked.add(parent[0]);}

                }
                else {
                    checked.remove(title);
                    if (checked.indexOf(parent[0])!=-1){
                        checked.remove(parent[0]);
                        parent[0].getCurholder()._checkBox.setChecked(false);
                    }
                }
            }
        });
    }

    @Override
    public Bundle onSaveInstanceState(Bundle savedInstanceStateBundle) {
        return super.onSaveInstanceState(savedInstanceStateBundle);
    }




}