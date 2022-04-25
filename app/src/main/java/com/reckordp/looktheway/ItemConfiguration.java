package com.reckordp.looktheway;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

public class ItemConfiguration extends AppCompatActivity {
    public static final String ITEM_BARU_HASIL = "ITEM BARU HASIL";
    public static final String ITEM_BATAL = "ITEM BATAL";
    public static final String CONFIGURATION_MODE = "CONFIGURATION MODE";
    public static final String CONFIGURATION_MODE_BARU = "ITEM BARU";
    public static final String CONFIGURATION_MODE_ADA = "ITEM ADA";
    public static final String CONFIGURATION_MODE_ADA_ITEM = "CONFIGURATION ITEM";

    ItemDetail hadapan;
    private boolean satuKaitan = false;
    private ActivityResultLauncher<Intent> berkaitanSelect;

    private EditText confNama;
    private CheckBox confPenting;
    private CheckBox confDarurat;
    private CheckBox confTerkini;
    private CheckBox confBerkaitan;
    private TextView nameOfBerkaitan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_configuration);

        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayShowHomeEnabled(true);
            bar.setDisplayHomeAsUpEnabled(true);
        }

        berkaitanSelect = registerForActivityResult(new StartActivityForResult(), result -> {
            Intent intent;
            intent = result.getData();
            if (result.getResultCode() == RESULT_OK && intent != null) {
                masukkanKaitan(intent.getIntExtra(BerkaitanActivity.BERKAITAN_TERPILIH,
                        ItemDetail.LEPAS_KAITAN));
            }
        });

        confNama = findViewById(R.id.item_nama);
        confPenting = findViewById(R.id.centang_penting);
        confDarurat = findViewById(R.id.centang_darurat);
        confTerkini = findViewById(R.id.centang_terkini);
        confBerkaitan = findViewById(R.id.centang_berkaitan);
        nameOfBerkaitan = findViewById(R.id.item_berkaitan);
    }

    @Override
    protected void onResume() {
        super.onResume();
        hadapan = adakanHadapan();

        switch (AllItem.adapterAbadi.allItem.size()) {
            case 0:
                confBerkaitan.setEnabled(false);
                break;

            case 1:
                satuKaitan = true;
                confBerkaitan.setEnabled(true);
                break;

            default:
                confBerkaitan.setEnabled(true);
                confBerkaitan.setOnClickListener(view -> {
                    if (!confBerkaitan.isChecked()) return;
                    Intent data = new Intent(this, BerkaitanActivity.class);
                    data.putExtra(BerkaitanActivity.BERKAITAN_DIRI, hadapan.id);
                    berkaitanSelect.launch(data);
                });
                break;
        }

        findViewById(R.id.item_simpan).setOnClickListener(view -> sudahiKonfigurasi());
        findViewById(R.id.item_selesai).setOnClickListener(view -> {
            hadapan.aktif = false;
            sudahiKonfigurasi();
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View focus = getCurrentFocus();
        float ySentuh = ev.getRawY();
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        boolean ret = super.dispatchTouchEvent(ev);

        if (focus == confNama && ev.getAction() == MotionEvent.ACTION_UP) {
            if (ySentuh < focus.getTop() || ySentuh > focus.getBottom()) {
                imm.hideSoftInputFromWindow(confNama.getWindowToken(), 0);
                confNama.clearFocus();
            }
        }

        return ret;
    }

    private void sudahiKonfigurasi() {
        hadapan.nama = confNama.getText().toString();
        hadapan.penting = confPenting.isChecked();
        hadapan.darurat = confDarurat.isChecked();
        hadapan.terkini = confTerkini.isChecked();

        if (!confBerkaitan.isChecked()) {
            lepasKaitan();
        } else if (satuKaitan) {
            masukkanKaitan(AllItem.adapterAbadi.allItem.get(0).id);
        }

        if (hadapan.nama.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this,
                    R.style.Theme_MaterialComponents_Dialog_Alert);
            builder.setMessage(R.string.alert_name);
            builder.setPositiveButton(android.R.string.ok, (dialog, which) -> { });
            builder.show();
            return;
        }

        hadapan.saveDatabase();

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
                return sesuaikanConfig(intent.getParcelableExtra(CONFIGURATION_MODE_ADA_ITEM));
            }
        }
        return new ItemDetail();
    }

    private void masukkanKaitan(int idItem) {
        if (idItem == ItemDetail.LEPAS_KAITAN) {
            confBerkaitan.setChecked(false);
            lepasKaitan();
        } else {
            confBerkaitan.setChecked(true);
            hadapan.berkaitan = idItem;
        }
    }

    private void lepasKaitan() {
        hadapan.berkaitan = ItemDetail.LEPAS_KAITAN;
    }

    private void batalkan() {
        Intent intent = new Intent();
        intent.putExtra(ITEM_BATAL, true);
        setResult(RESULT_OK, intent);
    }

    private ItemDetail sesuaikanConfig(ItemDetail item) {
        confNama.setText(item.nama);
        confPenting.setChecked(item.penting);
        confTerkini.setChecked(item.terkini);
        confDarurat.setChecked(item.darurat);
        masukkanKaitan(item.berkaitan);
        return item;
    }
}