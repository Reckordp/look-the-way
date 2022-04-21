package com.reckordp.looktheway;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class AllItem extends Fragment {

    public static int MODE_CONFIGURATION = 0;
    public static int MODE_SELECTION = 1;

    int clickMode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        clickMode = MODE_CONFIGURATION;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_all_item, container, false);
    }

    public void setMode(int mode) {
        clickMode = mode;
    }
}