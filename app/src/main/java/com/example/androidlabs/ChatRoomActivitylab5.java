package com.example.androidlabs;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChatRoomActivitylab5 extends AppCompatActivity {

    int numObjects = 6;
    ArrayList<Message> messages = new ArrayList<>();
    private static int ACTIVITY_VIEW_CONTACT = 33;
    int positionClicked = 0;
    ChatAdapter adapter;
    SQLiteDatabase db;
    Cursor results;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lab4_chat);

        //ListAdapter adt = new MyArrayAdapter(new String[] {"A", "B", "C"});
        ListAdapter adt = new MyOwnAdapter();


        //Get the fields from the screen:
        EditText userTyped = (EditText) findViewById(R.id.userTyped);
        Button sendButton = (Button) findViewById(R.id.sendButton);
        Button receiveButton = (Button) findViewById(R.id.receiveButton);
        ListView theList = (ListView) findViewById(R.id.the_list);

        //get a database:
        MyDatabaseOpenHelper dbOpener = new MyDatabaseOpenHelper(this);
        //SQLiteDatabase db = dbOpener.getWritableDatabase();
        db = dbOpener.getWritableDatabase();

        //query all the results from the database:
        String[] columns = {MyDatabaseOpenHelper.COL_ID, MyDatabaseOpenHelper.COL_ISSENT, MyDatabaseOpenHelper.COL_MESSAGE};
        results = db.query(false, MyDatabaseOpenHelper.TABLE_NAME, columns, null, null, null, null, null, null);

        //find the column indices:
        int isSentColumnIndex = results.getColumnIndex(MyDatabaseOpenHelper.COL_ISSENT);
        int messageColIndex = results.getColumnIndex(MyDatabaseOpenHelper.COL_MESSAGE);
        int idColIndex = results.getColumnIndex(MyDatabaseOpenHelper.COL_ID);

        //iterate over the results, return true if there is a next item:
        while (results.moveToNext()) {
            boolean isSent = results.getInt(isSentColumnIndex) > 0;
            String message = results.getString(messageColIndex);
            long id = results.getLong(idColIndex);

            //add the new Contact to the array list:
            messages.add(new Message(message, isSent, id));
        }
        adapter = new ChatAdapter();
        theList.setAdapter(adapter);


        SwipeRefreshLayout refresher = (SwipeRefreshLayout) findViewById(R.id.refresher);
        refresher.setOnRefreshListener(() -> {
            numObjects *= 2;
            ((MyOwnAdapter) adt).notifyDataSetChanged();
            refresher.setRefreshing(false);
        });

        //This listens for items being clicked in the list view
        theList.setOnItemClickListener((parent, view, position, id) -> {
            Log.e("you clicked on :", "item " + position);
            numObjects = 20;
            ((MyOwnAdapter) adt).notifyDataSetChanged();
        });


        sendButton.setOnClickListener(c -> {
            String userType = userTyped.getText().toString();

            //add to the database and get the new ID
            ContentValues newRowValues = new ContentValues();
            //put string name in the NAME column:
            newRowValues.put(MyDatabaseOpenHelper.COL_ISSENT, true);
            //put string email in the EMAIL column:
            newRowValues.put(MyDatabaseOpenHelper.COL_MESSAGE, userType);
            //insert in the database:
            long newId = db.insert(MyDatabaseOpenHelper.TABLE_NAME, null, newRowValues);
            this.printCursor();
            userTyped.setText("");
            messages.add(new Message(userType, true, newId));
            adapter.notifyDataSetChanged();
            Snackbar.make(sendButton, "Inserted message id:" + newId, Snackbar.LENGTH_LONG).show();
        });


        receiveButton.setOnClickListener(c -> {
            String userType = userTyped.getText().toString();
            //add to the database and get the new ID
            ContentValues newRowValues = new ContentValues();
            //put string name in the NAME column:
            newRowValues.put(MyDatabaseOpenHelper.COL_ISSENT, false);
            //put string email in the EMAIL column:
            newRowValues.put(MyDatabaseOpenHelper.COL_MESSAGE, userType);
            //insert in the database:
            long newId = db.insert(MyDatabaseOpenHelper.TABLE_NAME, null, newRowValues);
            this.printCursor();
            userTyped.setText("");
            messages.add(new Message(userType, false, newId));
            adapter.notifyDataSetChanged();
            Snackbar.make(sendButton, "Inserted message id:" + newId, Snackbar.LENGTH_LONG).show();

        });


    }

    //This class needs 4 functions to work properly:
    protected class ChatAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return messages.size();
        }

        public Object getItem(int position) {
            return messages.get(position).getMessage();//"\nItem "+ (position+1) + "\nSub Item "+ (position+1) +"\n";
        }

        public View getView(int position, View old, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            boolean isSent = messages.get(position).getIsSent();
            View newView;
            if (!isSent) {
                newView = inflater.inflate(R.layout.listview_item_send, parent, false);
            } else {
                newView = inflater.inflate(R.layout.listview_item_receive, parent, false);
            }
            TextView rowText = (TextView) newView.findViewById(R.id.edit);
            String stringToShow = getItem(position).toString();
            rowText.setText(stringToShow);
            return newView;
        }

        public long getItemId(int position) {
            return position;
        }
    }


    //This class needs 4 functions to work properly:
    protected class MyOwnAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return numObjects;
        }

        public Object getItem(int position) {
            return "\nItem " + (position + 1) + "\nSub Item " + (position + 1) + "\n";
        }

        public View getView(int position, View old, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();

            View newView = inflater.inflate(R.layout.activity_lab4_chat, parent, false);


            TextView rowText = (TextView) newView.findViewById(R.id.userTyped);
            String stringToShow = getItem(position).toString();
            rowText.setText(stringToShow);
            //return the row:
            return newView;
        }

        public long getItemId(int position) {
            return position;
        }
    }

    //A copy of ArrayAdapter. You just give it an array and it will do the rest of the work.
    protected class MyArrayAdapter<E> extends BaseAdapter {
        private List<E> dataCopy = null;

        //Keep a reference to the data:
        public MyArrayAdapter(List<E> originalData) {
            dataCopy = originalData;
        }

        //You can give it an array
        public MyArrayAdapter(E[] array) {
            dataCopy = Arrays.asList(array);
        }


        //Tells the list how many elements to display:
        public int getCount() {
            return dataCopy.size();
        }


        public E getItem(int position) {
            return dataCopy.get(position);
        }

        public View getView(int position, View old, ViewGroup parent) {
            //get an object to load a layout:
            LayoutInflater inflater = getLayoutInflater();

            //Recycle views if possible:
            TextView root = (TextView) old;
            //If there are no spare layouts, load a new one:
            if (old == null)
                root = (TextView) inflater.inflate(android.R.layout.simple_list_item_1, parent, false);

            //Get the string to go in row: position
            String toDisplay = getItem(position).toString();

            //Set the text of the text view
            root.setText(toDisplay);

            //Return the text view:
            return root;
        }


        //Return 0 for now. We will change this when using databases
        public long getItemId(int position) {
            return 0;
        }
    }

    public void printCursor() {
        Log.e("aaaaa", results.getCount() + "");
        Log.e("MyDatabaseFile version:", db.getVersion() + "");
        Log.e("Number of columns:", results.getColumnCount() + "");
        Log.e("Name of the columns:", results.getColumnNames().toString());
        Log.e("Number of results", results.getCount() + "");
        Log.e("Each row of results :", "");
        results.moveToFirst();
        for (int i = 0; i < results.getCount(); i++) {
            while (!results.isAfterLast()) {
                boolean isSent = results.getInt(0) > 0;
                String message = results.getString(1);
                long id = results.getLong(2);
                Log.e("id", id + "");
                Log.e("isSent", isSent + "");
                Log.e("message", message + "");
                results.moveToNext();
            }
        }
    }
}
