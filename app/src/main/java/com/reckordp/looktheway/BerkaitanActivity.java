package com.reckordp.looktheway;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;

public class BerkaitanActivity extends AppCompatActivity {
    public static final String BERKAITAN_DIRI = "BERKAITAN DIRI";
    public static final String BERKAITAN_TERPILIH = "BERKAITAN TERPILIH";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AllItem allItemConfiguration = new AllItem();
        int diriId = getIntent().getIntExtra(BERKAITAN_DIRI, ItemAdapter.KECUALI_KOSONG);
        AllItem.adapterAbadi.pengecualian(diriId);
        allItemConfiguration.setMode(AllItem.MODE_SELECTION);
        allItemConfiguration.setOnItemClick(item -> {
            Intent intent = new Intent();
            intent.putExtra(BERKAITAN_TERPILIH, item.id);
            setResult(RESULT_OK, intent);
            finish();
        });

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(android.R.id.content, allItemConfiguration);
        transaction.commit();
    }
}