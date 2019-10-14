package com.example.androidlabs;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;



public class ProfileActivity extends AppCompatActivity{
    protected static final String ACTIVITY_NAME = "LoginActivity";
    static final int REQUEST_IMAGE_CAPTURE = 1;
    ImageButton mImageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);


        mImageButton = (ImageButton)findViewById(R.id.imageButton);
        Intent emailFromPreviousPage = getIntent();
        String email = emailFromPreviousPage.getStringExtra("email");
        //get the values that were reserved under names "email"
        TextView editText = findViewById(R.id.userInput2);//enter your email edit view
        editText.setText(email);//set content of the edit view


        //function of picButton
        mImageButton.setOnClickListener(clk -> {

            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        });


        Button chatButton = findViewById(R.id.chatButton);
        chatButton.setOnClickListener( c -> {
            //Give directions to go from this page, to ChatRoomActivity
            Intent profilePage = new Intent(ProfileActivity.this, ChatRoomActivity.class);

            //EditText et = findViewById(R.id.userInput2);
            //profilePage.putExtra("typed", et.getText().toString());
            //Now make the transition:
            startActivityForResult( profilePage, 345);
        });


        Log.e(ACTIVITY_NAME, "In function:onCreate()");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            mImageButton.setImageBitmap(imageBitmap);
        }
    }

    protected void onStart() {
        super.onStart();
        Log.e(ACTIVITY_NAME, "In function:onStart()");
    }

    protected void onResume() {
        super.onResume();
        Log.e(ACTIVITY_NAME, "In function:onResume()");
    }


    protected void onPause() {
        super.onPause();
        Log.e(ACTIVITY_NAME, "In function:onPause()");
    }

    protected void onStop() {
        super.onStop();
        Log.e(ACTIVITY_NAME, "In function:onStop()");
    }

    protected void onDestroy() {
        super.onDestroy();
        Log.e(ACTIVITY_NAME, "In function:onDestroy()");
    }



}