package com.reckordp.looktheway;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

public class BerkaitanActivity extends AppCompatActivity {
    public static final String BERKAITAN_TERPILIH = "BERKAITAN TERPILIH";

    FrameLayout berkaitanView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_berkaitan);

        berkaitanView = (FrameLayout) getWindow().getDecorView().getRootView();
        berkaitanView.setId(View.generateViewId());
        setContentView(berkaitanView);

        AllItem allItemConfiguration = new AllItem();
        allItemConfiguration.setMode(AllItem.MODE_SELECTION);
        allItemConfiguration.setOnItemClick(item -> {
            Intent intent = new Intent();
            intent.putExtra(BERKAITAN_TERPILIH, item.id);
            setResult(RESULT_OK, intent);
            finish();
        });

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(berkaitanView.getId(), allItemConfiguration);
        transaction.commit();
    }
}