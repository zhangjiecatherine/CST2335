package com.example.androidlabs;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.EditText;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;

import static com.example.androidlabs.MyDatabaseOpenHelper.*;

public class ChatRoomActivity extends AppCompatActivity {

    ArrayList<Message> objects = new ArrayList<>();

    BaseAdapter myAdapter;
    SQLiteDatabase db;
    Cursor result;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lab4_chat);

        ListView theList = findViewById(R.id.the_list);
        theList.setAdapter( myAdapter = new MyListAdapter() );
        theList.setOnItemClickListener( ( parent,  view,  position,  id) ->{
            Log.i("CLicked", "You clicked item:" + position);
        });
//get a database
        MyDatabaseOpenHelper dbOpener= new MyDatabaseOpenHelper(this);
        db = dbOpener.getWritableDatabase();
        //db.execSQL("delete from "+ TABLE_NAME);
        //query all the results from the database
        String[] colums = {COL_ID, COL_MESSAGE, COL_ISSENT};
        result = db.query(false, TABLE_NAME, colums,
                null,null,null, null,null,null);

        //find the column index
        int nameColIndex = result.getColumnIndex(COL_MESSAGE);
        int idConInex = result.getColumnIndex(COL_ID);
        int isSentConIndex = result.getColumnIndex(COL_ISSENT);

        //iterate over the results, return true if there is a next item;
        while (result.moveToNext()){
            String name = result.getString(nameColIndex);
            long id = result.getLong(idConInex);
            boolean isSent = result.getInt(isSentConIndex)>0;
            objects.add(new Message(name, isSent, id));
        }
        Button receiveButton = findViewById(R.id.receiveButton);
        receiveButton.setOnClickListener( clik -> {
            TextView msgText = findViewById(R.id.userTyped);
            ContentValues contentValues = new ContentValues();
            contentValues.put(COL_MESSAGE , msgText.getText().toString());
            contentValues.put(COL_ISSENT, true);
            long newId = db.insert(TABLE_NAME,null,contentValues);
            Message message = new Message(msgText.getText().toString(),true, newId);
            this.printCursor();
            objects.add(message);
            myAdapter.notifyDataSetChanged(); //update yourself
            msgText.setText("");
        } );

        Button sendButton = findViewById(R.id.sendButton);
        sendButton.setOnClickListener( clik -> {
            TextView msgText = findViewById(R.id.userTyped);
            ContentValues contentValues = new ContentValues();
            contentValues.put(COL_MESSAGE , msgText.getText().toString());
            contentValues.put(COL_ISSENT, false);
            long newId = db.insert(TABLE_NAME,null,contentValues);
            Message message = new Message(msgText.getText().toString(),false,newId);
            this.printCursor();
            objects.add(message);
            myAdapter.notifyDataSetChanged(); //update yourself
            msgText.setText("");
        } );


    }

    private class MyListAdapter extends BaseAdapter {

        public int getCount() {  return objects.size();  } //This function tells how many objects to show

        public Message getItem(int position) { return objects.get(position);  }  //This returns the string at position p

        public long getItemId(int p) { return p; } //This returns the database id of the item at position p

        public View getView(int p, View recycled, ViewGroup parent)
        {

            LayoutInflater inflater = getLayoutInflater();
            View thisRow = recycled;

            if(thisRow == null) {
                if (getItem(p).isSent) {
                    thisRow = inflater.inflate(R.layout.listview_item_receive, null);
                    TextView itemField = thisRow.findViewById(R.id.edit);
                    itemField.setText(getItem(p).message);
                } else {
                    thisRow = inflater.inflate(R.layout.listview_item_send, null);
                    TextView itemField = thisRow.findViewById(R.id.edit);
                    itemField.setText(getItem(p).message);
                }
            } else {
                if (getItem(p).isSent) {
                    thisRow = inflater.inflate(R.layout.listview_item_receive, null);
                    TextView itemField = thisRow.findViewById(R.id.edit);
                    itemField.setText(getItem(p).message);
                } else {
                    thisRow = inflater.inflate(R.layout.listview_item_send, null);
                    TextView itemField = thisRow.findViewById(R.id.edit);
                    itemField.setText(getItem(p).message);
                }
            }
            return thisRow;
        }
    }

    public void printCursor() {
        Log.e("MyDatabaseFile version:", db.getVersion() + "");
        Log.e("Number of columns:", result.getColumnCount() + "");
        Log.e("Name of the columns:", Arrays.toString(result.getColumnNames()));
        Log.e("Number of results", result.getCount() + "");
        Log.e("Each row of results :", "");
        result.moveToFirst();

        //find the column index

        for (int i = 0; i < result.getCount(); i++) {
            while (!result.isAfterLast()) {

                Log.e("id", result.getString(result.getColumnIndex(COL_ID)) + "");
                Log.e("isSent", result.getString(result.getColumnIndex(COL_ISSENT)) + "");
                Log.e("message", result.getString(result.getColumnIndex(COL_MESSAGE)) + "");

                result.moveToNext();
            }
        }
    }
}
