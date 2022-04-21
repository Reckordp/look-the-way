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
        penting = in.readByte() != 0;
        darurat = in.readByte() != 0;
        terkini = in.readByte() != 0;
        berkaitan = in.readInt();
        aktif = in.readByte() != 0;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nama);
        dest.writeByte((byte) (penting ? 0 : 1));
        dest.writeByte((byte) (darurat ? 0 : 1));
        dest.writeByte((byte) (terkini ? 0 : 1));
        dest.writeInt(berkaitan);
        dest.writeByte((byte) (aktif ? 0 : 1));
    }
}
