package com.reckordp.looktheway;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;


public class AllItem extends Fragment {

    public static final int MODE_CONFIGURATION = 0;
    public static final int MODE_SELECTION = 1;
    public static ItemAdapter adapterAbadi = null;

    int clickMode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        clickMode = MODE_CONFIGURATION;
        if (adapterAbadi == null) adapterAbadi = new ItemAdapter(requireContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FrameLayout layout = (FrameLayout) inflater.inflate(R.layout.fragment_all_item,
                container, false);
        ListView allItemListView = layout.findViewById(R.id.all_item);
        allItemListView.setAdapter(adapterAbadi);
        return layout;
    }

    public void addItem(ItemDetail item) {
        adapterAbadi.add(item);
    }

    @Nullable
    @Override
    public View getView() {
        switch (clickMode) {
            case MODE_CONFIGURATION:
                adapterAbadi.urutNama();
                break;

            case MODE_SELECTION:
                adapterAbadi.urutPalingPenting();
                break;

            default:
                return null;
        }
        return super.getView();
    }

    public void setMode(int mode) {
        clickMode = mode;
    }
}