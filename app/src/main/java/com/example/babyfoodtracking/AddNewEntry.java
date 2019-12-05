package com.example.babyfoodtracking;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class AddNewEntry extends AppCompatActivity {

    private Boolean isUpdate;
    private String currentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.entry_display);
        Intent intent = getIntent();

        this.isUpdate = false;
        Bundle message = intent.getExtras();
        if (message != null)
            this.isUpdate = true;

        this.currentId = intent.getStringExtra("id");

        String name = intent.getStringExtra("name");
        EditText auto_add_name = (EditText) findViewById(R.id.name);
        auto_add_name.setText(name);

        String category = intent.getStringExtra("category");
        EditText auto_add_category = (EditText) findViewById(R.id.category);
        auto_add_category.setText(category);

        String date = intent.getStringExtra("date");
        EditText auto_add_date = (EditText) findViewById(R.id.date);
        auto_add_date.setText(date);

        String amount = intent.getStringExtra("amount");
        EditText auto_add_amount = (EditText) findViewById(R.id.amount);
        auto_add_amount.setText(amount);

        String note = intent.getStringExtra("note");
        EditText auto_add_note = (EditText) findViewById(R.id.note);
        auto_add_note.setText(note);

        Button submit = findViewById(R.id.button);
        if (this.isUpdate) {
            submit.setText("Update");
        } else {
            submit.setText("Add");
        }
    }

    public void onClickAdd(View view) {

        ContentValues values = new ContentValues();
        values.put(BabyFood.NAME,
                ((EditText) findViewById(R.id.name)).getText().toString());
        values.put(BabyFood.CATEGORY,
                ((EditText) findViewById(R.id.category)).getText().toString());
        values.put(BabyFood.DATE,
                ((EditText) findViewById(R.id.date)).getText().toString());
        values.put(BabyFood.AMOUNT,
                ((EditText) findViewById(R.id.amount)).getText().toString());
        values.put(BabyFood.NOTE,
                ((EditText) findViewById(R.id.note)).getText().toString());

        if (this.isUpdate) {
            String URL = "content://com.example.babyfoodtracking.BabyFood";
            Uri entry = Uri.parse(URL + '/' + BabyFood.TABLE_NAME + '/' + this.currentId);
            getContentResolver().update(entry, values, null, null);
        } else {
            getContentResolver().insert(BabyFood.CONTENT_URI, values);
        }
        startActivity(new Intent(this, MainActivity.class));
    }

    public void onClickChooseCategory(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(AddNewEntry.this);
        String URL = "content://com.example.babyfoodtracking.BabyFood";
        Uri food = Uri.parse(URL);
        Cursor c = managedQuery(food, null, null, null, "name");

        List result = new ArrayList<String>();
        if (c.moveToFirst()) {
            do {
                result.add(c.getString(c.getColumnIndex(BabyFood.CATEGORY)));
            } while (c.moveToNext());
        }
        final String[] existedCategory = (String[]) result.toArray(new String[0]);

        builder.setTitle("category");
        builder.setItems(existedCategory, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                EditText auto_add = (EditText) findViewById(R.id.category);
                auto_add.setText(existedCategory[which]);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}

