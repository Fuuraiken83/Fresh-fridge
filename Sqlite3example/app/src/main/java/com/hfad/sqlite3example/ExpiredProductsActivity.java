package com.hfad.sqlite3example;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.hfad.sqlite3example.data.DBFridgeListHelper;
import com.hfad.sqlite3example.data.FridgeList;

public class ExpiredProductsActivity extends ProductListActivity {
    String[] header = {FridgeList.Product.COLUMN_NAME,
            FridgeList.Product.COLUMN_FRIDGE_NAME};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expired_products);
        TextView label = (TextView) findViewById(R.id.textView);
        label.setText("Expired Products");

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void displayDatabase(String fridgeName,String[] header){

        super.displayDatabase(null,this.header);


    }
    public void deleteProduct(final String productName){
        super.deleteProduct(productName);
    }
    public void dialogWindow(View view){
        super.dialogWindow(view);
    }
}
