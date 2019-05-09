package com.hfad.sqlite3example;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.hfad.sqlite3example.data.DBFridgeListHelper;
import com.hfad.sqlite3example.data.FridgeList;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

public class ProductListActivity extends AppCompatActivity {
    private DBFridgeListHelper dbHelper;
    Cursor cursor;
    Dialog dialog;
    String fridgeName;
    String[] header = {FridgeList.Product.COLUMN_NAME,
            FridgeList.Product.COLUMN_EXPIRATION_DATE};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new DBFridgeListHelper(this);
        setContentView(R.layout.activity_product_list);
        Intent intent = getIntent();
        fridgeName = intent.getStringExtra("fridgeName");
        TextView label = (TextView) findViewById(R.id.textView);
        label.setText(fridgeName);
        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogWindow(view);
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();



    }

    @Override
    protected void onResume() {
        super.onResume();
        displayDatabase(fridgeName,header);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        // Закрываем подключение и курсор
        dbHelper.close();
        cursor.close();
    }

    public void displayDatabase(final String fridgeName,final String[] header) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();


        ListView products = (ListView) findViewById(R.id.product_list);
        int index = products.getFirstVisiblePosition();
        if (products.getLastVisiblePosition() == products.getCount() - 1) {
            index++;
        }
        View v = products.getChildAt(0);
        int top = (v == null) ? 0 : (v.getTop() - products.getPaddingTop());

        //Запрос
        if(fridgeName != null) {
            cursor = db.rawQuery(String.format("SELECT * FROM %s WHERE %s=? ",
                    FridgeList.Product.TABLE_NAME,
                    FridgeList.Product.COLUMN_FRIDGE_NAME),
                    new String[]{fridgeName});
        }
        else{
            Date i = new Date();

            cursor = db.rawQuery(String.format("SELECT * FROM %s WHERE %s<?",
                    FridgeList.Product.TABLE_NAME,
                    FridgeList.Product.COLUMN_EXPIRATION_DATE),new String[]{String.valueOf(i.getTime())});
        }

        final SimpleCursorAdapter userAdapter = new SimpleCursorAdapter(this,
                android.R.layout.two_line_list_item,
                cursor,
                header,
                new int[]{android.R.id.text1, android.R.id.text2},

                0);
        if (fridgeName!=null) {
            userAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
                @Override
                public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                    if (columnIndex == cursor.getColumnIndex(FridgeList.Product.COLUMN_EXPIRATION_DATE)) {
                        Long dateInMs = cursor.getLong(columnIndex);
                        TextView dateTextView = (TextView) view;
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.mm.yyyy");
                        String dateAsString = dateFormat.format(dateInMs);
                        dateTextView.setText("Годен до: " + dateAsString);
                        return true;
                    }
                    return false;
                }
            });

        }
        products.setAdapter(userAdapter);

        products.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String productName = cursor.getString(cursor.getColumnIndex(FridgeList.Product.COLUMN_NAME));
                Date i = new Date();
                Toast.makeText(ProductListActivity.this,
                        String.format("%d",
                                cursor.getLong(cursor.getColumnIndex(FridgeList.Product.COLUMN_EXPIRATION_DATE)) -
                                i.getTime()),
                        Toast.LENGTH_LONG).show();


                deleteProduct(productName);
                return true;
            }
        });

        // restore index and position
        products.setSelectionFromTop(index, top);
    }

    private void insertProduct(final String fridgeName,final String productName,
                              final long productionDate, final long expirationDate) {
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                // Gets the database in write mode
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                // Создаем объект ContentValues, где имена столбцов ключи,
                // а информация о госте является значениями ключей
                ContentValues values = new ContentValues();
                values.put(FridgeList.Product.COLUMN_NAME, productName);
                values.put(FridgeList.Product.COLUMN_FRIDGE_NAME, fridgeName);
                values.put(FridgeList.Product.COLUMN_PRODUCTION_DATE, productionDate);
                values.put(FridgeList.Product.COLUMN_EXPIRATION_DATE, expirationDate);
                db.insert(FridgeList.Product.TABLE_NAME, null, values);
                displayDatabase(fridgeName,header);
            }
        });
    }

    public void deleteProduct(final String productName){
        // Gets the database in write mode


        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setPositiveButton("OK",null)
                .setNegativeButton("Cancel",null)
                .setTitle("Удаление продукта")
                .setMessage("Вы хотите удалить продукт?")
                .show();
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final ContentValues values = new ContentValues();
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Handler handler = new Handler();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        db.delete(FridgeList.Product.TABLE_NAME,
                                FridgeList.Product.COLUMN_NAME + "=? ",
                                new String[]{productName});
                        displayDatabase(fridgeName,header);
                        dialog.dismiss();
                    }
                });
            }
        });

        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    public void dialogWindow(View view){
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_new_product,null);
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setPositiveButton("OK",null)
                .setNegativeButton("Cancel",null)
                .setView(dialogView)
                .setTitle("1234")
                .show();
        //dialog.setView(dialogView);
        dialog.setTitle("Добавить новый Продукт");
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        final EditText product = (EditText)dialogView.findViewById(R.id.productName);
        final EditText productionDate = (EditText)dialogView.findViewById(R.id.productionDate);
        final EditText expiratationDate = (EditText)dialogView.findViewById(R.id.expirationDate);

        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    CheckDate.Pair<Long,Long> pair = CheckDate.legitDate
                            (productionDate.getText().toString()
                                    ,expiratationDate.getText().toString());

                    if (!product.getText().toString().equals("")){
                        if(CheckDate.CheckSecondOverFirst(pair)){
                            String productName = product.getText().toString();
                            insertProduct(fridgeName,productName,pair.getFirst(),pair.getSecond());

                            dialog.dismiss();
                        }
                        else{
                            Toast.makeText(ProductListActivity.this, "Дата изготовления позже даты выхода из годности!", Toast.LENGTH_SHORT).show();
                        }

                    }
                    else{
                        Toast.makeText(ProductListActivity.this, "Введите название!", Toast.LENGTH_SHORT).show();
                    }
                }
                catch (NullPointerException npe){
                    Toast.makeText(ProductListActivity.this, "Проверьте даты!", Toast.LENGTH_SHORT).show();

                }
            }
        });

        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

}
