package com.reckordp.looktheway;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    private ActivityResultLauncher<Intent> itemDetailLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        itemDetailLauncher = registerForActivityResult(new StartActivityForResult(), result -> {
            Intent intent;
            AllItem allItem;
            intent = result.getData();
            if (intent != null) {
                ItemDetail item = intent.getParcelableExtra(ItemConfiguration.ITEM_BARU_HASIL);
                allItem = (AllItem) getSupportFragmentManager().getFragments().get(0);
                allItem.addItem(item);
            }
        });

        AllItem allItemFragment = new AllItem();
        findViewById(R.id.tambah).setOnClickListener(view -> layangkanDetail(null));
        allItemFragment.setOnItemClick(this::layangkanDetail);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.lembar, allItemFragment);
        ft.commit();
    }

    private void layangkanDetail(@Nullable ItemDetail item) {
        Intent intent = new Intent(this, ItemConfiguration.class);

        if (item == null) {
            intent.putExtra(ItemConfiguration.CONFIGURATION_MODE, ItemConfiguration.CONFIGURATION_MODE_BARU);
        } else {
            intent.putExtra(ItemConfiguration.CONFIGURATION_MODE, ItemConfiguration.CONFIGURATION_MODE_ADA);
            intent.putExtra(ItemConfiguration.CONFIGURATION_MODE_ADA_ITEM, item);
        }

        itemDetailLauncher.launch(intent);
    }
}