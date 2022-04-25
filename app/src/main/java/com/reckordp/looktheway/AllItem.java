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
    private int clickMode;
    private ItemClickListener eItemClick = null;

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
        allItemListView = layout.findViewById(R.id.all_item);
        allItemListView.setAdapter(adapterAbadi);
        refreshListView();
        return layout;
    }

    @Nullable
    @Override
    public View getView() {
        switch (clickMode) {
            case MODE_CONFIGURATION:
                adapterAbadi.urutNama();
                adapterAbadi.pengecualian(ItemAdapter.KECUALI_KOSONG);
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
            if (eItemClick != null) eItemClick.onItemClick(adapterAbadi.allItem.get((int) id));
        });
    }
}