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

    private ListView allItemListView = null;
    private int clickMode = MODE_CONFIGURATION;
    private ItemClickListener eItemClick = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (adapterAbadi == null) adapterAbadi = new ItemAdapter(requireContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FrameLayout layout = (FrameLayout) inflater.inflate(R.layout.fragment_all_item,
                container, false);
        allItemListView = layout.findViewById(R.id.all_item);
        allItemListView.setAdapter(adapterAbadi);
        refreshListView();
        return layout;
    }

    public void refresh() {
        if (allItemListView == null) return;
        switch (clickMode) {
            case MODE_CONFIGURATION:
                adapterAbadi.urutPalingPenting();
                adapterAbadi.pengecualian(ItemAdapter.KECUALI_KOSONG);
                break;

            case MODE_SELECTION:
                adapterAbadi.urutNama();
                break;
        }
        allItemListView.requestLayout();
        allItemListView.invalidate();
    }

    @Nullable
    @Override
    public View getView() {
        refresh();
        return super.getView();
    }

    public void setMode(int mode) {
        clickMode = mode;
        if (allItemListView != null) refreshListView();
    }

    public interface ItemClickListener {
        void onItemClick(ItemDetail item);
    }

    public void setOnItemClick(ItemClickListener onClick) {
        eItemClick = onClick;
    }

    private void refreshListView() {
        allItemListView.setOnItemClickListener((parent, view, position, id) -> {
            ItemDetail item = (ItemDetail) allItemListView.getItemAtPosition(position);
            if (eItemClick != null) eItemClick.onItemClick(item);
        });
    }
}