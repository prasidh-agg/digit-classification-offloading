package com.example.group20;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private static final int picID = 123;
    Button cameraOpenButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cameraOpenButton = findViewById(R.id.button_1);
        cameraOpenButton.setOnClickListener( v->{
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, picID); 
        });
    }

    protected void onActivityResult( int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode,resultCode,data);

        if(requestCode == picID)
        {
            if( data.getExtras() != null) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                Intent uploadPhotoIntent = new Intent(MainActivity.this, UploadPhoto.class);
                uploadPhotoIntent.putExtra("photo", photo);
                startActivity(uploadPhotoIntent);
            }
        }
    }

}