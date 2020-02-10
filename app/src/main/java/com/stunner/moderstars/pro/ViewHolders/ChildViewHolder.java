package com.stunner.moderstars.pro.ViewHolders;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import stunner.moderstars.R;


public class ChildViewHolder extends com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder {
    public TextView option1;
    public CheckBox checkBox1;
    public RelativeLayout cardView;
    private String path;
    public ChildViewHolder(View itemView) {
        super(itemView);
        checkBox1 = itemView.findViewById(R.id.checkBox1);
        option1 = itemView.findViewById(R.id.option1);
        cardView = itemView.findViewById(R.id.layout);
    }
    public String getPath() {
        return path;
    }

}

