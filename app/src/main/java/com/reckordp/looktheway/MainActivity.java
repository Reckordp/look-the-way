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
    private AllItem allItemFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        allItemFragment = new AllItem();
        itemDetailLauncher = usulkanDetail();
        findViewById(R.id.tambah).setOnClickListener(view -> layangkanDetail(null));
        allItemFragment.setOnItemClick(this::layangkanDetail);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.lembar, allItemFragment);
        ft.commit();
    }

    private void layangkanDetail(@Nullable ItemDetail item) {
        Intent intent = new Intent(this, ItemConfiguration.class);
        String confMode = ItemConfiguration.CONFIGURATION_MODE;

        if (item == null) {
            intent.putExtra(confMode, ItemConfiguration.CONFIGURATION_MODE_BARU);
        } else {
            intent.putExtra(confMode, ItemConfiguration.CONFIGURATION_MODE_ADA);
            intent.putExtra(ItemConfiguration.CONFIGURATION_MODE_ADA_ITEM, item.id);
        }

        itemDetailLauncher.launch(intent);
    }

    private ActivityResultLauncher<Intent> usulkanDetail() {
        return registerForActivityResult(new StartActivityForResult(), result -> {
            Intent intent = result.getData();
            if (intent != null && !intent.hasExtra(ItemConfiguration.ITEM_BATAL)) {
                allItemFragment.refresh();
            }
        });
    }
}