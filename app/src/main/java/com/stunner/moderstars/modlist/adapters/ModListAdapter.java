package com.stunner.moderstars.modlist.adapters;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.Model.ParentObject;
import com.stunner.moderstars.R;
import com.stunner.moderstars.UsefulThings;
import com.stunner.moderstars.modlist.models.ModListFile;
import com.stunner.moderstars.modlist.models.ModListFolder;
import com.stunner.moderstars.modlist.viewholders.FileViewHolder;
import com.stunner.moderstars.modlist.viewholders.FolderViewHolder;

import java.util.ArrayList;
import java.util.List;

public class ModListAdapter extends ExpandableRecyclerAdapter<FolderViewHolder, FileViewHolder> {

    private static List<ModListFolder> parents = new ArrayList<>();
    private LayoutInflater inflater;

    public ModListAdapter(Context context, List<ParentObject> parentItemList) {
        super(context, parentItemList);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public FolderViewHolder onCreateParentViewHolder(ViewGroup viewGroup) {
        View view = inflater.inflate(R.layout.list_parent, viewGroup, false);
        return new FolderViewHolder(view);
    }

    @Override
    public FileViewHolder onCreateChildViewHolder(ViewGroup viewGroup) {
        View view = inflater.inflate(R.layout.list_child, viewGroup, false);
        return new FileViewHolder(view);

    }

    @Override
    public void onBindParentViewHolder(final FolderViewHolder holder, final int i, final Object o) {
        final ModListFolder title = (ModListFolder) o;
        title.setCurholder(holder);
        parents.add(title);
        holder._textView.setText(title.getTitle());
        holder._checkBox.setChecked(UsefulThings.checked.indexOf(title) != -1);
        if (((ModListFolder) o).getPath().isDirectory()) {
            holder.layout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (UsefulThings.checked.indexOf(title) == -1) {
                        holder._checkBox.setChecked(true);
                        UsefulThings.checked.add(title);
                        for (Object x : title.getChildObjectList()) {
                            ModListFile child = (ModListFile) x;
                            if (child.getCurholder() != null)
                                child.getCurholder().checkBox1.setChecked(true);
                                                                 UsefulThings.checked.add(child);
                                                             }
                                                         } else {
                                                             holder._checkBox.setChecked(false);
                                                             UsefulThings.checked.remove(title);
                                                         }
                                                         return true;
                                                     }
                                                 }
            );
            holder._checkBox.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        if (UsefulThings.checked.indexOf(title) == -1) {
                                                            UsefulThings.checked.add(title);
                                                            for (Object x : title.getChildObjectList()) {
                                                                ModListFile child = (ModListFile) x;
                                                                if (child.getCurholder() != null)
                                                                    child.getCurholder().checkBox1.setChecked(true);
                                                                UsefulThings.checked.add(child);

                                                            }
                                                        } else {
                                                            UsefulThings.checked.remove(title);
                                                        }

                                                    }
                                                }
            );
        } else {
            holder.layout.setOnClickListener(new View.OnClickListener() {
                                                 @Override
                                                 public void onClick(View v) {
                                                     if (UsefulThings.checked.indexOf(title) == -1) {
                                                         holder._checkBox.setChecked(true);
                                                         UsefulThings.checked.add(title);
                                                         for (Object x : title.getChildObjectList()) {
                                                             ModListFile child = (ModListFile) x;
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
    public void onBindChildViewHolder(final FileViewHolder holder, final int i, final Object o) {
        final ModListFile title = (ModListFile) o;
        final boolean[] d = {false};
        final ModListFolder[] parent = {null};
        for (ModListFolder x : parents) {
            if (x.getChildObjectList().indexOf(title) != -1) {
                parent[0] = x;
                break;
            }
        }
        title.setCurholder(holder);
        holder.option1.setText(title.getOption1());
        holder.checkBox1.setChecked(UsefulThings.checked.indexOf(title) != -1);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UsefulThings.checked.indexOf(title) == -1) {
                    holder.checkBox1.setChecked(true);
                    UsefulThings.checked.add(title);
                    for (Object x : parent[0].getChildObjectList()) {
                        ModListFile child = (ModListFile) x;
                        if (UsefulThings.checked.indexOf(child) != -1) d[0] = true;
                        else {
                            d[0] = false;
                            break;
                        }
                    }
                    if (d[0]) {
                        parent[0].getCurholder()._checkBox.setChecked(true);
                        UsefulThings.checked.add(parent[0]);

                    }

                } else {
                    holder.checkBox1.setChecked(false);
                    UsefulThings.checked.remove(title);
                    if (UsefulThings.checked.indexOf(parent[0]) != -1) {
                        UsefulThings.checked.remove(parent[0]);
                        parent[0].getCurholder()._checkBox.setChecked(false);
                    }
                }
            }
        });
        holder.checkBox1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UsefulThings.checked.indexOf(title) == -1) {
                    UsefulThings.checked.add(title);
                    for (Object x : parent[0].getChildObjectList()) {
                        ModListFile child = (ModListFile) x;
                        if (UsefulThings.checked.indexOf(child) != -1) d[0] = true;
                        else {
                            d[0] = false;
                            break;
                        }
                    }
                    if (d[0]) {
                        parent[0].getCurholder()._checkBox.setChecked(true);
                        UsefulThings.checked.add(parent[0]);
                    }

                } else {
                    UsefulThings.checked.remove(title);
                    if (UsefulThings.checked.indexOf(parent[0]) != -1) {
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