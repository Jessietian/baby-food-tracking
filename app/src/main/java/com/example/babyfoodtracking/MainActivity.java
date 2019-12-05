package com.example.babyfoodtracking;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;

import android.database.Cursor;

import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        this.updateList();
        Button add = (Button)findViewById(R.id.button2);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AddNewEntry.class));
            }
        });
    }

    void updateList() {
        final String URL = "content://com.example.babyfoodtracking.BabyFood";
        Uri food = Uri.parse(URL);
        final Cursor c = managedQuery(food, null, null, null, "name");

        final LinearLayout listView = findViewById(R.id.listview);
        listView.removeAllViews();

        if (c.moveToFirst()) {
            do {
                LinearLayout row = new LinearLayout(this);
                row.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                row.setOrientation(LinearLayout.HORIZONTAL);

                final String id = c.getString(c.getColumnIndex(BabyFood._ID));
                final String name = c.getString(c.getColumnIndex(BabyFood.NAME));
                final String category = c.getString(c.getColumnIndex(BabyFood.CATEGORY));
                final String date = c.getString(c.getColumnIndex(BabyFood.DATE));
                final String amount = c.getString(c.getColumnIndex(BabyFood.AMOUNT));
                final String note = c.getString(c.getColumnIndex(BabyFood.NOTE));
                final TextView content = new TextView(this);
                content.setText(String.format("%s, %s, %s, %s, %s", name, category, date, amount, note));

                Button update = new Button(this);
                update.setText("Update");
                update.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(MainActivity.this, AddNewEntry.class);
                        intent.putExtra("id", id);
                        intent.putExtra("name", name);
                        intent.putExtra("category", category);
                        intent.putExtra("date", date);
                        intent.putExtra("amount", amount);
                        intent.putExtra("note", note);
                        startActivity(intent);
                    }
                });

                Button delete = new Button(this);
                delete.setText("Delete");
                delete.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Uri item = Uri.parse(URL + '/' + BabyFood.TABLE_NAME + '/' + id);
                        getContentResolver().delete(item, null, null);
                        MainActivity.this.updateList();
                    }
                });

                row.addView(content);
                row.addView(update);
                row.addView(delete);

                listView.addView(row);
            } while (c.moveToNext());
        }
    }

}