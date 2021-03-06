package com.reckordp.looktheway;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;

public class ItemDetail implements Parcelable {
    public static final int LEPAS_KAITAN = 0;
    public static final String TABLE_NAME = "item";

    int id;
    String nama;
    boolean penting;
    boolean darurat;
    boolean terkini;
    int berkaitan;
    boolean aktif;

    private Parcel dulu;

    ItemDetail() {
        nama = "";
        penting = false;
        darurat = false;
        terkini = false;
        berkaitan = LEPAS_KAITAN;
        aktif = true;
        buatDulu();
    }

    protected ItemDetail(Parcel in) {
        id = in.readInt();
        nama = in.readString();
        fromTanda((byte) in.readInt());
        berkaitan = in.readInt();
        buatDulu();
        if (nama == null) nama = "";
    }

    private void buatDulu() {
        dulu = Parcel.obtain();
        writeToParcel(dulu, 0);
    }

    public boolean sebanding(ItemDetail bandingan) {
        return bandingan.nama.equals(nama) &&
                bandingan.createTanda() == createTanda() &&
                bandingan.berkaitan == berkaitan;
    }

    public String getNama() {
        return nama;
    }

    public int getSkor() {
        return skorDari(0, aktif) +
                skorDari(1, terkini) +
                skorDari(2, darurat) +
                skorDari(3, penting);
    }

    private int skorDari(int tanda, boolean ada) {
        int skor;
        switch (tanda) {
            case 0:
                skor = 1000;
                break;
            case 1:
                skor = 200;
                break;
            case 2:
                skor = 300;
                break;
            case 3:
                skor = 100;
                break;
            default: skor = 0;
        }
        return skor * boolToInt(ada);
    }

    public static final Creator<ItemDetail> CREATOR = new Creator<ItemDetail>() {
        @Override
        public ItemDetail createFromParcel(Parcel in) {
            return new ItemDetail(in);
        }

        @Override
        public ItemDetail[] newArray(int size) {
            return new ItemDetail[size];
        }
    };

    public void fromTanda(byte tanda) {
        penting = ((tanda & 0x08) >> 3) == 1;
        darurat = ((tanda & 0x04) >> 2) == 1;
        terkini = ((tanda & 0x02) >> 1) == 1;
        aktif = ((tanda & 0x01) >> 0) == 1;
    }

    public int createTanda() {
        return ((byte)(boolToInt(penting) << 3) |
                (byte)(boolToInt(darurat) << 2) |
                (byte)(boolToInt(terkini) << 1) |
                (byte)(boolToInt(aktif)));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(nama);
        dest.writeInt(createTanda());
        dest.writeInt(berkaitan);
    }

    private int boolToInt(boolean aliran) {
        return aliran ? 1 : 0;
    }

    public void saveDatabase() {
        int jalur;
        SQLiteDatabase db;
        ContentValues deretan;
        String[] whereArgs = { String.valueOf(id) };

        if (sebanding(CREATOR.createFromParcel(dulu))) return;
        jalur = AllItem.adapterAbadi.jalurUntukMenyimpan(this);
        db = AllItem.adapterAbadi.db;
        deretan = new ContentValues();
        deretan.put("nama", nama);
        deretan.put("tanda", createTanda());
        deretan.put("berkaitan", berkaitan);

        switch (jalur) {
            case ItemAdapter.DATABASE_INSERT:
                id = (int) db.insert(TABLE_NAME, null, deretan);
                AllItem.adapterAbadi.add(this);
                break;

            case ItemAdapter.DATABASE_UPDATE:
                db.update(TABLE_NAME, deretan, "id=?", whereArgs);
                break;
        }
    }

    public void hapus() {
        String[] args = new String[1];
        ItemAdapter adapt;

        adapt = AllItem.adapterAbadi;
        args[0] = String.valueOf(id);
        adapt.db.delete(TABLE_NAME, "id=?", args);
        adapt.remove(this);
    }

    public boolean isBerkaitan() {
        return berkaitan != LEPAS_KAITAN;
    }
}
