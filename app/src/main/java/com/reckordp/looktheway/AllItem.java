package com.reckordp.looktheway;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;


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
        FrameLayout layout = (FrameLayout) inflater.inflate(R.layout.fragment_all_item, container, false);
        ListView allItemListView = layout.findViewById(R.id.all_item);
        allItemListView.setAdapter(new ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1));
        return layout;
    }

    public void setMode(int mode) {
        clickMode = mode;
    }
}