package com.stunner.moderstars.pro.ViewHolders;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder;

import stunner.moderstars.R;

public class TitleChildViewHolder extends ChildViewHolder {
    public TextView option1;
    public CheckBox checkBox1;

    public TitleChildViewHolder(View itemView) {
        super(itemView);
        checkBox1 = itemView.findViewById(R.id.checkBox1);
        option1 = itemView.findViewById(R.id.option1);
    }
}

