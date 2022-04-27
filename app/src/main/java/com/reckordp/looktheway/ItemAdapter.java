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
import java.util.Objects;

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
        }

        allItemCursor.close();
        addAll(allItem);
        cResources = ctx.getResources();
        posKecuali = KECUALI_KOSONG;
    }

    @Override
    public void add(@Nullable ItemDetail object) {
        allItem.add(object);
        if (object != null) idMap.put(object.id, object);
        super.add(object);
    }

    @Override
    public void remove(@Nullable ItemDetail object) {
        if (object != null) {
            for (ItemDetail item : allItem) {
                if (item.isBerkaitan() && item.berkaitan == object.id) {
                    item.berkaitan = ItemDetail.LEPAS_KAITAN;
                    item.saveDatabase();
                }
            }
            idMap.remove(object.id);
        }
        allItem.remove(object);
        super.remove(object);
    }

    @Nullable ItemDetail itemFromId(int id) {
        return idMap.get(id);
    }

    public void pengecualian(int id) {
        if (id == KECUALI_KOSONG) {
            posKecuali = KECUALI_KOSONG;
            return;
        }
        posKecuali = getPosition(idMap.get(id));
    }

    @Override
    public boolean isEnabled(int position) {
        return posKecuali != position;
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
        if (posKecuali == pos) return inflater.inflate(RESOURCE_KOSONG, parent, false);
        item = getItem(pos);
        kaitan = item.isBerkaitan() ? itemFromId(item.berkaitan) : null;

        if (convertView == null) {
            view = inflater.inflate(RESOURCE_ITEM_ADAPTER, parent, false);
        } else {
            view = convertView;
        }

        view.setBackgroundColor(cResources.getColor(
                item.aktif ? android.R.color.transparent : R.color.item_disabled, null));

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
        resourceItemItemTerkait.setText(kaitan != null ? kaitan.nama : "");

        return view;
    }

    private void textViewTanda(TextView base, boolean acuan) {
        int colorId = acuan ? android.R.color.black : R.color.item_color;
        base.setTextColor(cResources.getColor(colorId, null));
    }

    private interface PenghitungSkor {
        ArrayMap<Integer, Integer> skorGantung = new ArrayMap<>();

        int hitungan(@NonNull ItemDetail item);
        ItemDetail uraiBerkaitan(int berkaitanId);

        default int getKetergantunganSkor(int asalId, @Nullable ItemDetail item) {
            int skor = 0;
            if (item != null && item.isBerkaitan()) {
                if (item.berkaitan == asalId) {
                    return 0;
                } else if (skorGantung.containsKey(item.id)) {
                    return Objects.requireNonNull(skorGantung.get(item.id));
                } else {
                    ItemDetail berkaitan = uraiBerkaitan(item.berkaitan);
                    skor = hitungan(item) + getKetergantunganSkor(asalId, berkaitan);
                    skorGantung.put(item.id, skor);
                }
            }
            return skor;
        }
    }

    public void urutPalingPenting() {
        PenghitungSkor jalanSkor = new PenghitungSkor() {
            @Override
            public int hitungan(@NonNull ItemDetail item) {
                return item.getSkor() * (item.aktif ? 1 : -1);
            }

            @Override
            public ItemDetail uraiBerkaitan(int id) { return idMap.get(id); }
        };
        sort((item1, item2) -> {
            int skor1, skor2;
            skor1 = jalanSkor.getKetergantunganSkor(item1.id, item1);
            skor2 = jalanSkor.getKetergantunganSkor(item2.id, item2);
            return Integer.compare(skor2, skor1);
        });
    }

    public void urutNama() {
        ItemDetail item = posKecuali != KECUALI_KOSONG ? getItem(posKecuali) : null;
        sort(Comparator.comparing(ItemDetail::getNama));
        if (item != null) posKecuali = getPosition(item);
    }

    public int jalurUntukMenyimpan(ItemDetail itemDetail) {
        return allItem.contains(itemDetail) ? DATABASE_UPDATE : DATABASE_INSERT;
    }
}
