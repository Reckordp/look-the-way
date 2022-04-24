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
import android.widget.CheckBox;
import android.widget.EditText;

public class ItemConfiguration extends AppCompatActivity {
    public static String ITEM_BARU_HASIL = "ITEM BARU HASIL";
    public static String ITEM_BATAL = "ITEM BATAL";
    public static String CONFIGURATION_MODE = "CONFIGURATION MODE";
    public static String CONFIGURATION_MODE_BARU = "ITEM BARU";
    public static String CONFIGURATION_MODE_ADA = "ITEM ADA";
    public static String CONFIGURATION_MODE_ADA_ITEM = "CONFIGURATION ITEM";

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

        hadapan = adakanHadapan();
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

        findViewById(R.id.item_simpan).setOnClickListener(view -> sudahiKonfigurasi());
        findViewById(R.id.item_selesai).setOnClickListener(view -> {
            hadapan.aktif = false;
            sudahiKonfigurasi();
        });
    }

    private void sudahiKonfigurasi() {
        hadapan.nama = ((EditText)findViewById(R.id.item_nama)).getText().toString();
        hadapan.penting = ((CheckBox)findViewById(R.id.centang_penting)).isChecked();
        hadapan.darurat = ((CheckBox)findViewById(R.id.centang_darurat)).isChecked();
        hadapan.terkini = ((CheckBox)findViewById(R.id.centang_terkini)).isChecked();

        Intent intent = new Intent();
        intent.putExtra(ITEM_BARU_HASIL, hadapan);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            batalkan();
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        batalkan();
        super.onBackPressed();
    }

    private ItemDetail adakanHadapan() {
        Intent intent = getIntent();
        if (intent.hasExtra(CONFIGURATION_MODE)) {
            if (intent.getStringExtra(CONFIGURATION_MODE).equals(CONFIGURATION_MODE_ADA)) {
                return (ItemDetail) intent.getParcelableExtra(CONFIGURATION_MODE_ADA_ITEM);
            }
        }
        return new ItemDetail();
    }

    private void masukkanKaitan(int idItem) {
        if (idItem != -1) {
            hadapan.berkaitan = idItem;
        }
    }

    private void batalkan() {
        Intent intent = new Intent();
        intent.putExtra(ITEM_BATAL, true);
        setResult(RESULT_OK, intent);
    }
}