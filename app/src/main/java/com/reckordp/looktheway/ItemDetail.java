package com.reckordp.looktheway;

import android.os.Parcel;
import android.os.Parcelable;

public class ItemDetail implements Parcelable {
    static int LEPAS_KAITAN = 0;
    static String TABLE_NAME = "item";

    String nama;
    boolean penting;
    boolean darurat;
    boolean terkini;
    int berkaitan;
    boolean aktif;

    ItemDetail(String nm) {
        nama = nm;
        penting = false;
        darurat = false;
        terkini = false;
        berkaitan = LEPAS_KAITAN;
        aktif = true;
    }

    protected ItemDetail(Parcel in) {
        nama = in.readString();
        fromTanda((byte) in.readInt());
        berkaitan = in.readInt();
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
        return ((byte)((penting ? 0 : 1) << 3) |
                (byte)((darurat ? 0 : 1) << 2) |
                (byte)((terkini ? 0 : 1) << 1) |
                (byte)((aktif ? 0 : 1)));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nama);
        dest.writeInt(createTanda());
        dest.writeInt(berkaitan);
    }
}
