package com.stunner.moderstars.ui.more;


import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.stunner.moderstars.R;
import com.stunner.moderstars.UsefulThings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.preference.PreferenceFragmentCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class TabEditorFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_editor, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(new EditorAdapter());
        return view;
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
    }

    class EditorAdapter extends RecyclerView.Adapter<ModItem> {
        EditorAdapter() {
            super();
        }

        @NonNull
        @Override
        public ModItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.editlist_item, parent, false);
            return new ModItem(v);
        }

        @Override
        public void onBindViewHolder(@NonNull final ModItem holder, final int pos) {
            final int position = holder.getBindingAdapterPosition();
            String text = UsefulThings.getModName(getContext(), position);
            final OnClickListener b = new OnClickListener() {
                @Override
                public void onClick(View v) {
                    MaterialAlertDialogBuilder alertDialog = new MaterialAlertDialogBuilder(getActivity());
                    alertDialog.setMessage(getString(R.string.remove, UsefulThings.getModName(getContext(), position)));
                    alertDialog.setPositiveButton(getText(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final Runnable r = new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        UsefulThings.delMod(getContext(), position);
                                        notifyItemRemoved(position);
                                    } catch (Exception e) {
                                        UsefulThings.crashlytics.recordException(e);
                                    }
                                }
                            };
                            final Handler h = new Handler();
                            final Snackbar s = Snackbar.make(getView().findViewById(R.id.editbtn), R.string.removed, 2000);
                            s.setAction("Undo", new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    h.removeCallbacks(r);
                                }
                            });
                            s.setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE);
                            s.show();
                            h.postDelayed(r, 2100);
                        }
                    });
                    alertDialog.setNegativeButton(getText(R.string.no), null);
                    alertDialog.show();
                }
            }, a = new OnClickListener() {
                @Override
                public void onClick(View v) {
                    MaterialAlertDialogBuilder alertDialog = new MaterialAlertDialogBuilder(getActivity());
                    alertDialog.setMessage(R.string.entername);
                    final EditText input = new EditText(getActivity());
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT);
                    input.setLayoutParams(lp);
                    alertDialog.setView(input);
                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String newn = input.getText().toString().equals("") ? getString(R.string.mod, position + 1) : input.getText().toString();
                            UsefulThings.setModName(getContext(), position, newn);
                            notifyItemChanged(position);
                        }
                    });
                    alertDialog.show();
                }
            };
            holder.setData(text, a, b);
        }

        @Override
        public int getItemCount() {
            return UsefulThings.modСount(getContext());
        }


    }

    class ModItem extends RecyclerView.ViewHolder {
        TextView text;
        AppCompatImageButton edit, delete;

        ModItem(View v) {
            super(v);
            text = v.findViewById(R.id.title);
            edit = v.findViewById(R.id.editbtn);
            delete = v.findViewById(R.id.delbtn);
        }

        public void setData(String text, OnClickListener edit, OnClickListener delete) {
            this.text.setText(text);
            this.edit.setOnClickListener(edit);
            this.delete.setOnClickListener(delete);
        }
    }
}
