package com.hfad.sqlite3example;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
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

public class MainActivity extends AppCompatActivity {

    private DBFridgeListHelper dbHelper;
    Cursor cursor;
    Dialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper = new DBFridgeListHelper(this);
        FloatingActionButton myFab = (FloatingActionButton) findViewById(R.id.floatingActionButton2);
        myFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialogWindow(v);


            }
        });
        FloatingActionButton toExpiredProducts = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        toExpiredProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,ExpiredProductsActivity.class);
                startActivity(intent);
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
        displayDatabase();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        // Закрываем подключение и курсор
        dbHelper.close();
        cursor.close();
    }

    private void displayDatabase(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();


        ListView fridges = (ListView) findViewById(R.id.list_fridges);
        int index = fridges.getFirstVisiblePosition();
        if (fridges.getLastVisiblePosition() == fridges.getCount()-1) {
            index++;
        }
        View v = fridges.getChildAt(0);
        int top = (v == null) ? 0 : (v.getTop() - fridges.getPaddingTop());

        //Запрос
        cursor = db.rawQuery("SELECT * FROM " +FridgeList.Fridge.TABLE_NAME,null);
        String[] header = {FridgeList.Fridge._ID,FridgeList.Fridge.COLUMN_NAME};


        CursorAdapter userAdapter = new SimpleCursorAdapter(this,
                android.R.layout.two_line_list_item,
                cursor,
                header,
                new int[]{android.R.id.text1,android.R.id.text2},
                0);



        //перейти в активность ProductListActivity и передать ей название холодильника
        fridges.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String fridgeName = cursor.getString(cursor.getColumnIndex(FridgeList.Fridge.COLUMN_NAME));
                Intent intent = new Intent(MainActivity.this,ProductListActivity.class);
                intent.putExtra("fridgeName",fridgeName);
                startActivity(intent);
            }
        });

        fridges.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String fridgeName = cursor.getString(cursor.getColumnIndex(FridgeList.Fridge.COLUMN_NAME));
                deleteFridge(fridgeName);
                return true;
            }
        });


        fridges.setAdapter(userAdapter);

        // restore index and position
        fridges.setSelectionFromTop(index, top);
    }

    private void insertFridge(final String fridgeName) {
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                // Gets the database in write mode
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                // Создаем объект ContentValues, где имена столбцов ключи,
                // а информация о госте является значениями ключей
                ContentValues values = new ContentValues();
                values.put(FridgeList.Fridge.COLUMN_NAME, fridgeName);
                db.insert(FridgeList.Fridge.TABLE_NAME, null, values);
                displayDatabase();
            }
        });
    }

    private void deleteFridge(final String fridgeName){
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
                db.delete(FridgeList.Fridge.TABLE_NAME,
                        FridgeList.Fridge.COLUMN_NAME + "=? ",
                        new String[]{fridgeName});
                db.delete(FridgeList.Product.TABLE_NAME,
                        FridgeList.Product.COLUMN_FRIDGE_NAME + "=? ",
                        new String[]{fridgeName});
                displayDatabase();
                dialog.dismiss();
            }
        });

        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void dialogWindow(View view){
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_fridge,null);
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setPositiveButton("OK",null)
                .setNegativeButton("Cancel",null)
                .setView(dialogView)
                .setTitle("1234")
                .show();
        //dialog.setView(dialogView);
        dialog.setTitle("Добавить новый холодильник");
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        final EditText editText = (EditText)dialogView.findViewById(R.id.etComments);

        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editText.getText().toString().equals("")){
                    String addFridge = editText.getText().toString();
                    insertFridge(addFridge);
                    dialog.dismiss();
                }
                else {
                    Toast.makeText(MainActivity.this, "Введите название", Toast.LENGTH_SHORT).show();
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
