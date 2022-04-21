package com.reckordp.looktheway;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.tambah).setOnClickListener(view -> {
            Intent intent = new Intent(this, ItemConfiguration.class);
            startActivity(intent);
        });

        AllItem allItemFragment = new AllItem();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.lembar, allItemFragment);
        ft.commit();
    }
}