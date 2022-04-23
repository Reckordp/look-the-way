package com.reckordp.looktheway;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Comparator;

public class ItemAdapter extends ArrayAdapter<ItemDetail> {

    private static final int RESOURCE_ITEM_ADAPTER = R.layout.item_resource_layout;
    ItemDatabaseHelper openHelper;
    SQLiteDatabase db;
    ArrayList<ItemDetail> allItem;
    Resources cResources;
    Resources.Theme cTheme;
    int colorDefault;

    ItemAdapter(Context ctx) {
        super(ctx, RESOURCE_ITEM_ADAPTER);
        openHelper = new ItemDatabaseHelper(ctx);
        db = openHelper.getReadableDatabase();
        String[] selectionArgs = {};
        Cursor allItemCursor = db.query(ItemDetail.TABLE_NAME, null, null,
                selectionArgs, null, null, null);
        allItem = new ArrayList<>();
        while (allItemCursor.moveToNext()) {
            ItemDetail item = new ItemDetail();
            item.id = allItemCursor.getInt(0);
            item.nama = allItemCursor.getString(1);
            item.fromTanda((byte) allItemCursor.getInt(2));
            item.berkaitan = allItemCursor.getInt(3);
            allItem.add(item);
        }
        allItemCursor.close();
        addAll(allItem);
        cResources = ctx.getResources();
        cTheme = ctx.getTheme();
        colorDefault = -1;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final View view;
        final TextView resourceItemNama;
        final TextView resourceItemPenting;
        final TextView resourceItemDarurat;
        final TextView resourceItemTerkini;
        final TextView resourceItemBerkaitan;
        final TextView resourceItemItemTerkait;
        final LayoutInflater inflater;

        inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            view = inflater.inflate(RESOURCE_ITEM_ADAPTER, parent, false);
        } else {
            view = convertView;
        }

        resourceItemNama = view.findViewById(R.id.resource_item_nama);
        resourceItemPenting = view.findViewById(R.id.resource_item_penting);
        resourceItemDarurat = view.findViewById(R.id.resource_item_darurat);
        resourceItemTerkini = view.findViewById(R.id.resource_item_terkini);
        resourceItemBerkaitan = view.findViewById(R.id.resource_item_berkaitan);
        resourceItemItemTerkait = view.findViewById(R.id.resource_item_item_terkait);

        ItemDetail item = allItem.get(position);
        resourceItemNama.setText(item.nama);
        textViewTanda(resourceItemPenting, item.penting);
        textViewTanda(resourceItemDarurat, item.darurat);
        textViewTanda(resourceItemTerkini, item.terkini);
        textViewTanda(resourceItemBerkaitan, item.berkaitan != ItemDetail.LEPAS_KAITAN);
        textViewTanda(resourceItemItemTerkait, item.berkaitan != ItemDetail.LEPAS_KAITAN);
        resourceItemItemTerkait.setText(item.nama);

        return view;
    }

    private void textViewTanda(TextView base, boolean acuan) {
        int colorId = acuan ? android.R.color.black : R.color.item_color;
        base.setTextColor(cResources.getColor(colorId, null));
    }

    @Override
    public void add(@Nullable ItemDetail object) {
        super.add(object);
        allItem.add(object);
    }

    public void urutPalingPenting() {
        sort(Comparator.comparingInt(this::hitungSkor));
    }

    public void urutNama() {
        sort(Comparator.comparing(ItemDetail::getNama));
    }

    private int hitungSkor(ItemDetail item) {
        return item.getSkor() + item.getKetergantunganSkor();
    }
}
