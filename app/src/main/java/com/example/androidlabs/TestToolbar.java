package com.example.androidlabs;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

public class TestToolbar extends AppCompatActivity {

    String message;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_toolbar);
    message = "You clicked on the overflow menu";
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.example_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            //what to do when the menu item is selected:
            case R.id.choice4:
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                break;
            case R.id.choice1:
                Toast.makeText(this, "This is the initial message", Toast.LENGTH_LONG).show();
               break;

            case R.id.choice2:
                alertExample();
                break;
            case R.id.choice3:
                Snackbar snackbar = Snackbar.make(this.findViewById(R.id.choice3), "Go back?", Snackbar.LENGTH_LONG);
                snackbar.setAction(R.string.undo_string, new MyUndoListener());
                snackbar.show();
                break;
        }
        return true;
    }


    public class MyUndoListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            // Code to undo the user's last action
        }
    }



    public void alertExample()
    {

        View middle = getLayoutInflater().inflate(R.layout.view_extra_stuff, null);
        ImageView imageView = (ImageView) findViewById(R.id.image1);
        EditText et = (EditText)middle.findViewById(R.id.view_edit_text);
       //btn.setOnClickListener( clk -> et.setText("You clicked my button!"));

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("")
                .setPositiveButton("Positive", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        message = et.getText().toString();
                    }
                })
                .setNegativeButton("Negative", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // What to do on Cancel
                    }
                }).setView(middle);

        builder.create().show();
    }

}
