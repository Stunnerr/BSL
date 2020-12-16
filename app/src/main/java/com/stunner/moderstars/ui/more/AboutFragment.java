package com.stunner.moderstars.ui.more;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.stunner.moderstars.R;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;

@Keep
public class AboutFragment extends PreferenceFragmentCompat {
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.about_app);
        return inflater.inflate(R.layout.fragnent_about, container, false);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
    }
}
