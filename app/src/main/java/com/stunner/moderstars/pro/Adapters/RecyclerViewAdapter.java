package com.stunner.moderstars.pro.Adapters;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.Model.ParentObject;
import com.stunner.moderstars.UsefulThings;
import com.stunner.moderstars.pro.Models.ListChild;
import com.stunner.moderstars.pro.Models.ListParent;
import com.stunner.moderstars.pro.ViewHolders.ChildViewHolder;
import com.stunner.moderstars.pro.ViewHolders.ParentViewHolder;

import java.util.ArrayList;
import java.util.List;

import stunner.moderstars.R;

public class RecyclerViewAdapter extends ExpandableRecyclerAdapter<ParentViewHolder, ChildViewHolder> {

    private LayoutInflater inflater;
    private static List<ListParent> parents = new ArrayList<>();

    public RecyclerViewAdapter(Context context, List<ParentObject> parentItemList) {
        super(context, parentItemList);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public ParentViewHolder onCreateParentViewHolder(ViewGroup viewGroup) {
        View view = inflater.inflate(R.layout.list_parent, viewGroup, false);
        return new ParentViewHolder(view);
    }

    @Override
    public ChildViewHolder onCreateChildViewHolder(ViewGroup viewGroup) {
        View view = inflater.inflate(R.layout.list_child, viewGroup, false);
        return new ChildViewHolder(view);

    }

    @Override
    public void onBindParentViewHolder(final ParentViewHolder holder, final int i, final Object o) {
        final ListParent title = (ListParent) o;
        title.setCurholder(holder);
        parents.add(title);
        //Log.i(ActivityPro.TAG,"parent id:" +String.valueOf(i)+ " name:" + title.getTitle() + " obj:" + o.toString());
        holder._textView.setText(title.getTitle());
        holder._checkBox.setChecked(UsefulThings.checked.indexOf(title)!=-1);
        if (((ListParent) o).getPath().isDirectory()) {
            holder.layout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (UsefulThings.checked.indexOf(title)==-1) {
                        holder._checkBox.setChecked(true);
                        UsefulThings.checked.add(title);
                        for (Object x:title.getChildObjectList()) {
                            ListChild child = (ListChild) x;
                            if(child.getCurholder()!=null) child.getCurholder().checkBox1.setChecked(true);
                            UsefulThings.checked.add(child);
                        }
                    } else {holder._checkBox.setChecked(false);
                        UsefulThings.checked.remove(title);}
                    return true;
                }}
            );
            holder._checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (UsefulThings.checked.indexOf(title)==-1) {
                        UsefulThings.checked.add(title);
                        for (Object x:title.getChildObjectList()) {
                            ListChild child = (ListChild) x;
                            if(child.getCurholder()!=null) child.getCurholder().checkBox1.setChecked(true);
                            UsefulThings.checked.add(child);
                        }
                    } else UsefulThings.checked.remove(title);

                }}
            );
        } else {
            holder.layout.setOnClickListener(new View.OnClickListener() {
                                                 @Override
                                                 public void onClick(View v) {
                                                     if (UsefulThings.checked.indexOf(title) == -1) {
                                                         holder._checkBox.setChecked(true);
                                                         UsefulThings.checked.add(title);
                                                         for (Object x : title.getChildObjectList()) {
                                                             ListChild child = (ListChild) x;
                                                             if (child.getCurholder() != null)
                                                                 child.getCurholder().checkBox1.setChecked(true);
                                                             UsefulThings.checked.add(child);
                                                         }
                                                     } else {
                                                         holder._checkBox.setChecked(false);
                                                         UsefulThings.checked.remove(title);
                                                     }
                                                 }
                                             }
            );

        }
    }


    @Override
    public void onBindChildViewHolder(final ChildViewHolder holder, final int i, final Object o) {
        final ListChild title = (ListChild) o;
        final boolean[] d = {false};
        final ListParent[] parent = {null};
        for (ListParent x: parents) {if(x.getChildObjectList().indexOf(title)!=-1) {parent[0]=x;break;}}
        title.setCurholder(holder);
        holder.option1.setText(title.getOption1());
        //Log.i(ActivityPro.TAG, "child id:" + String.valueOf(i) + " name:" + title.getOption1() + " obj:" + o.toString() + " Holder:" + holder.toString());
        holder.checkBox1.setChecked(UsefulThings.checked.indexOf(title)!=-1);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UsefulThings.checked.indexOf(title) == -1)
                {
                    holder.checkBox1.setChecked(true);
                    UsefulThings.checked.add(title);
                    for (Object x:parent[0].getChildObjectList()){
                        ListChild child = (ListChild) x;
                        if (UsefulThings.checked.indexOf(child)!=-1)d[0]=true;
                        else  {d[0]=false;break;}
                    }
                    if (d[0]) {parent[0].getCurholder()._checkBox.setChecked(true);
                        UsefulThings.checked.add(parent[0]);}

                }
                else {
                    holder.checkBox1.setChecked(false);
                    UsefulThings.checked.remove(title);
                    if (UsefulThings.checked.indexOf(parent[0])!=-1){
                        UsefulThings.checked.remove(parent[0]);
                        parent[0].getCurholder()._checkBox.setChecked(false);
                    }
                }
            }
        });
        holder.checkBox1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UsefulThings.checked.indexOf(title) == -1)
                {
                    UsefulThings.checked.add(title);
                    for (Object x:parent[0].getChildObjectList()){
                        ListChild child = (ListChild) x;
                        if (UsefulThings.checked.indexOf(child)!=-1)d[0]=true;
                        else  {d[0]=false;break;}
                    }
                    if (d[0]) {parent[0].getCurholder()._checkBox.setChecked(true);
                        UsefulThings.checked.add(parent[0]);}

                }
                else {
                    UsefulThings.checked.remove(title);
                    if (UsefulThings.checked.indexOf(parent[0])!=-1){
                        UsefulThings.checked.remove(parent[0]);
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