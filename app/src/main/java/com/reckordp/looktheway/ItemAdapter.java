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
import java.util.stream.Collectors;

public class ItemAdapter extends ArrayAdapter<ItemDetail> {
    static final int DATABASE_INSERT = 0;
    static final int DATABASE_UPDATE = 1;
    static final int KECUALI_KOSONG = -1;
    private static final int RESOURCE_ITEM_ADAPTER = R.layout.item_resource_layout;

    ItemDatabaseHelper openHelper;
    SQLiteDatabase db;
    ArrayList<ItemDetail> allItem;
    Resources cResources;
    int idKecuali;

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
        idKecuali = KECUALI_KOSONG;
    }

    ItemDetail itemFromId(int id) {
        try {
            return allItem.stream()
                    .filter(e -> e.id == id)
                    .collect(Collectors.toList())
                    .get(0);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    public void pengecualian(int id) {
        idKecuali = id;
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
        final ItemDetail item, kaitan;

        inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        item = allItem.get(position);
        kaitan = item.isBerkaitan() ? itemFromId(item.berkaitan) : null;

        if (item.id == idKecuali) {
            return new View(getContext());
        }

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

        resourceItemNama.setText(item.nama);
        textViewTanda(resourceItemPenting, item.penting);
        textViewTanda(resourceItemDarurat, item.darurat);
        textViewTanda(resourceItemTerkini, item.terkini);
        textViewTanda(resourceItemBerkaitan, item.isBerkaitan());
        textViewTanda(resourceItemItemTerkait, item.isBerkaitan());
        if (kaitan != null) resourceItemItemTerkait.setText(kaitan.nama);

        return view;
    }

    private void textViewTanda(TextView base, boolean acuan) {
        int colorId = acuan ? android.R.color.black : R.color.item_color;
        base.setTextColor(cResources.getColor(colorId, null));
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

    public int jalurUntukMenyimpan(ItemDetail itemDetail) {
        return allItem.contains(itemDetail) ? DATABASE_UPDATE : DATABASE_INSERT;
    }
}
