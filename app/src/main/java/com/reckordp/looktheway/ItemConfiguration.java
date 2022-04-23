package com.reckordp.looktheway;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

public class ItemConfiguration extends AppCompatActivity {
    static String ITEM_BARU_HASIL = "ITEM BARU HASIL";

    ItemDetail hadapan;
    private ActivityResultLauncher<Intent> berkaitanSelect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        View berkaitanCentang;
        ActionBar bar;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_configuration);

        bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayShowHomeEnabled(true);
            bar.setDisplayHomeAsUpEnabled(true);
        }

        hadapan = new ItemDetail();
        berkaitanCentang = findViewById(R.id.centang_berkaitan);

        berkaitanSelect = registerForActivityResult(new StartActivityForResult(), result -> {
            Intent intent;
            intent = result.getData();
            if (result.getResultCode() == RESULT_OK && intent != null) {
                masukkanKaitan(intent.getIntExtra(BerkaitanActivity.BERKAITAN_TERPILIH, -1));
            }
        });

        switch (AllItem.adapterAbadi.allItem.size()) {
            case 0:
                berkaitanCentang.setEnabled(false);
                break;

            case 1:
                berkaitanCentang.setEnabled(false);
                masukkanKaitan(AllItem.adapterAbadi.allItem.get(0).id);
                break;

            default:
                berkaitanCentang.setOnClickListener(view -> {
                    berkaitanSelect.launch(new Intent(this, BerkaitanActivity.class));
                });
                break;
        }

        Intent intent = new Intent();
        intent.putExtra(ITEM_BARU_HASIL, hadapan);
        setResult(RESULT_OK, intent);

        findViewById(R.id.item_berkaitan).setOnClickListener(view -> finish());
        findViewById(R.id.item_selesai).setOnClickListener(view -> {
            hadapan.aktif = false;
            finish();
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void masukkanKaitan(int idItem) {
        if (idItem != -1) {
            hadapan.berkaitan = idItem;
        }
    }
}