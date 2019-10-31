package com.stunner.moderstars.pro.ViewHolders;
import android.os.Environment;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder;

import java.io.File;

import stunner.moderstars.R;

public class TitleChildViewHolder extends ChildViewHolder {
    public TextView option1;
    public CheckBox checkBox1;
    //public int modnum;
    public TitleChildViewHolder(View itemView) {
        super(itemView);
        checkBox1 = itemView.findViewById(R.id.checkBox1);
        option1 = itemView.findViewById(R.id.option1);
    }
    
    public String getparent(){
        File fld = new File(Environment.getExternalStorageDirectory() + "/Mods");
        for (File x:fld.listFiles())
        {
           for (String e: x.list())
           {
               if (e.equals(option1.toString())) return x.toString();

           }
        }
        return null;
    }
}

