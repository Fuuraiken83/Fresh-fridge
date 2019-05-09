package com.hfad.sqlite3example.data;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import com.hfad.sqlite3example.data.FridgeList.Fridge;
import com.hfad.sqlite3example.data.FridgeList.Product;


public class DBFridgeListHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = DBFridgeListHelper.class.getSimpleName();

    /**
     * Имя файла базы данных
     */
    private static final String DATABASE_NAME = "fridgeApp.db";

    /**
     * Версия базы данных. При изменении схемы увеличить на единицу
     */
    private static final int DATABASE_VERSION = 1;

    /**
     *
     * Конструктор {@link DBFridgeListHelper}.
     *
     * @param context Контекст приложения
     */
     public DBFridgeListHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     *Создание таблицы
     */
    @Override
    public void onCreate(SQLiteDatabase db){

         //строка создания таблицы с холодильниками
         String SQL_CREATE_FRIDGE_TABLE = String.format("CREATE TABLE %s " +
                         "(%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                         "%s TEXT NOT NULL);",
                 Fridge.TABLE_NAME,
                 Fridge._ID,
                 Fridge.COLUMN_NAME);

        //строка создания таблицы с продуктами
        String SQL_CREATE_PRODUCT_TABLE = String.format("CREATE TABLE %s " +
                        "(%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "%s TEXT NOT NULL, " +
                        "%s TEXT NOT NULL, " +
                        "%s INTEGER, " +
                        "%s INTEGER);",
                Product.TABLE_NAME,
                Product._ID,
                Product.COLUMN_NAME,
                Product.COLUMN_FRIDGE_NAME,
                Product.COLUMN_PRODUCTION_DATE,
                Product.COLUMN_EXPIRATION_DATE);

        //Выполняем создание таблиц
        db.execSQL(SQL_CREATE_FRIDGE_TABLE);
        db.execSQL(SQL_CREATE_PRODUCT_TABLE);
    }
    /**
     * Вызывается при обновлении схемы базы данных
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }
}
