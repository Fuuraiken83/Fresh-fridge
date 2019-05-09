package com.hfad.sqlite3example.data;

import android.provider.BaseColumns;

public class FridgeList {
    private FridgeList(){};
    public static final class Fridge implements BaseColumns{
        public final static String TABLE_NAME = "fridges";
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_NAME = "name";
    }
    public static final class Product implements BaseColumns{
        public final static String TABLE_NAME = "products";
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_NAME = "name";
        public final static String COLUMN_FRIDGE_NAME = "fridge_name";
        public final static String COLUMN_PRODUCTION_DATE = "production_date";
        public final static String COLUMN_EXPIRATION_DATE= "expiration_date";
    }
}
