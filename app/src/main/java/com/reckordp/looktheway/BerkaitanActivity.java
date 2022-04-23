package com.reckordp.looktheway;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.reckordp.looktheway.databinding.ActivityBerkaitanBinding;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

public class BerkaitanActivity extends AppCompatActivity {
    static String BERKAITAN_TERPILIH = "BERKAITAN TERPILIH";

    ActivityBerkaitanBinding berkaitanBinding;
    FrameLayout berkaitanView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_berkaitan);

        berkaitanBinding = ActivityBerkaitanBinding.inflate(getLayoutInflater());
        berkaitanView = berkaitanBinding.getRoot();
        berkaitanView.setId(View.generateViewId());
        setContentView(berkaitanView);

        AllItem allItemConfiguration = new AllItem();
        allItemConfiguration.setMode(AllItem.MODE_SELECTION);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(berkaitanView.getId(), allItemConfiguration);
        transaction.commit();
    }
}