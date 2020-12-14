package com.stunner.moderstars.modlist.viewholders;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.stunner.moderstars.R;

public class FolderViewHolder extends com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder {
    public TextView _textView;
    public CheckBox _checkBox;
    public RelativeLayout layout;

    public FolderViewHolder(View itemView) {
        super(itemView);
        _textView = itemView.findViewById(R.id.parentTitle);
        _checkBox = itemView.findViewById(R.id.checkBox);
        layout = itemView.findViewById(R.id.layout1);
    }
}