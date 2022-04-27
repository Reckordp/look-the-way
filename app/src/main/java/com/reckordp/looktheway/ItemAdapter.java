package com.reckordp.looktheway;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.ArrayMap;
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
    private static final int RESOURCE_KOSONG = R.layout.item_kosong;

    ItemDatabaseHelper openHelper;
    SQLiteDatabase db;
    ArrayList<ItemDetail> allItem;
    Resources cResources;
    int posKecuali;
    private final ArrayMap<Integer, ItemDetail> idMap = new ArrayMap<>();
    private final ArrayMap<Integer, Integer> skorMap = new ArrayMap<>();

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
            idMap.put(item.id, item);
            skorMap.put(item.id, item.getSkor());
        }

        allItemCursor.close();
        addAll(allItem);
        cResources = ctx.getResources();
        posKecuali = KECUALI_KOSONG;
    }

    @Override
    public void add(@Nullable ItemDetail object) {
        allItem.add(object);
        if (object != null) {
            idMap.put(object.id, object);
            skorMap.put(object.id, object.getSkor());
        }
        super.add(object);
    }

    @Nullable ItemDetail itemFromId(int id) {
        return idMap.get(id);
    }

    public void pengecualian(int id) {
        if (id == KECUALI_KOSONG) {
            posKecuali = KECUALI_KOSONG;
            return;
        }
        for (int i = 0; i < allItem.size(); i++) {
            if (allItem.get(i).id == id) posKecuali = i;
        }
    }

    @Override
    public boolean isEnabled(int position) {
        if (posKecuali == position) return false;
        return super.isEnabled(position);
    }

    @NonNull
    @Override
    public View getView(int pos, @Nullable View convertView, @NonNull ViewGroup parent) {
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
        if (!isEnabled(pos)) return inflater.inflate(RESOURCE_KOSONG, parent, false);
        item = getItem(pos);
        kaitan = item.isBerkaitan() ? itemFromId(item.berkaitan) : null;

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
        ArrayMap<Integer, Integer> skorGantung = new ArrayMap<>(skorMap);
        skorGantung.forEach((id, skor) -> {
            ItemDetail item = idMap.get(id);
            if (item != null && item.isBerkaitan()) {
                ItemDetail berkaitan = idMap.get(item.berkaitan);
                if (berkaitan != null) {
                    skorGantung.replace(id, berkaitan.getSkor() * (berkaitan.aktif ? 1 : -1));
                }
            }
        });
        sort(Comparator.comparingInt(item -> skorGantung.get(item.id)));
    }

    public void urutNama() {
        sort(Comparator.comparing(ItemDetail::getNama));
    }

    public int jalurUntukMenyimpan(ItemDetail itemDetail) {
        return allItem.contains(itemDetail) ? DATABASE_UPDATE : DATABASE_INSERT;
    }
}
