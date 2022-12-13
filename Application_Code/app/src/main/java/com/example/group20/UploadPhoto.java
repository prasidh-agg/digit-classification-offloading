package com.example.group20;

import static com.example.group20.ApplicationConstants.DEVICE_1_URL;
import static com.example.group20.ApplicationConstants.DEVICE_2_URL;
import static com.example.group20.ApplicationConstants.DEVICE_3_URL;
import static com.example.group20.ApplicationConstants.DEVICE_4_URL;
import static com.example.group20.ApplicationConstants.INVALID_IP;
import static com.example.group20.ApplicationConstants.PORT_UPLOAD;
import static com.example.group20.ProcessingUtil.divideBitmap;
import static com.example.group20.ProcessingUtil.encodeImageToBase64;
import static com.example.group20.ProcessingUtil.findDigitWithMaxConfidence;
import static com.example.group20.ProcessingUtil.validateIpAddress;
import static com.example.group20.ServerUtil.buildDeviceUrl;
import static com.example.group20.ServerUtil.buildHashMap;
import static com.example.group20.ServerUtil.saveToMasterMobile;
import static com.example.group20.ServerUtil.sendPostRequest;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;

public class UploadPhoto extends AppCompatActivity {

    public static final Boolean ENTER_IP_MANUALLY = false;

    ImageView imageView;
    Button uploadButton;
    Button backButton;
    EditText editTextIP1;
    EditText editTextIP2;
    EditText editTextIP3;
    EditText editTextIP4;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_photo);

        //Getting image from intent from previous activity
        Intent intent = getIntent();
        Bitmap photo = intent.getParcelableExtra("photo");
        Bitmap[] dividedImages = divideBitmap(photo);
        imageView = findViewById(R.id.imageView);
        imageView.setImageBitmap(photo);

        editTextIP1 = findViewById(R.id.editTextIP1);
        editTextIP2 = findViewById(R.id.editTextIP2);
        editTextIP3 = findViewById(R.id.editTextIP3);
        editTextIP4 = findViewById(R.id.editTextIP4);

        uploadButton = findViewById(R.id.uploadButton);
        uploadButton.setOnClickListener(v->{
            String device1Url, device2Url, device3Url, device4Url;
            if(ENTER_IP_MANUALLY){
                String device1Ip = editTextIP1.getText().toString();
                if(device1Ip.equals("") || !validateIpAddress(device1Ip)) {
                    Toast toast = Toast.makeText(getApplicationContext(), INVALID_IP, Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }
                String device2Ip = editTextIP2.getText().toString();
                if(device2Ip.equals("") || !validateIpAddress(device2Ip)) {
                    Toast toast = Toast.makeText(getApplicationContext(), INVALID_IP, Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }
                String device3Ip = editTextIP3.getText().toString();
                if(device3Ip.equals("") || !validateIpAddress(device3Ip)) {
                    Toast toast = Toast.makeText(getApplicationContext(), INVALID_IP, Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }
                String device4Ip = editTextIP4.getText().toString();
                if(device4Ip.equals("") || !validateIpAddress(device4Ip)) {
                    Toast toast = Toast.makeText(getApplicationContext(), INVALID_IP, Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }
                device1Url = buildDeviceUrl(device1Ip,PORT_UPLOAD);
                device2Url = buildDeviceUrl(device2Ip,PORT_UPLOAD);
                device3Url = buildDeviceUrl(device3Ip,PORT_UPLOAD);
                device4Url = buildDeviceUrl(device4Ip,PORT_UPLOAD);
            }
            else{
                device1Url = DEVICE_1_URL;
                device2Url = DEVICE_2_URL;
                device3Url = DEVICE_3_URL;
                device4Url = DEVICE_4_URL;
            }

            if(photo != null) {
                String imagePart1String = encodeImageToBase64(dividedImages[0]);
                String imagePart2String = encodeImageToBase64(dividedImages[1]);
                String imagePart3String = encodeImageToBase64(dividedImages[2]);
                String imagePart4String = encodeImageToBase64(dividedImages[3]);

                HashMap<String,String> postData1 = new HashMap<>();
                buildHashMap(imagePart1String, postData1);

                HashMap<String,String> postData2 = new HashMap<>();
                buildHashMap(imagePart2String, postData2);

                HashMap<String,String> postData3 = new HashMap<>();
                buildHashMap(imagePart3String, postData3);

                HashMap<String,String> postData4 = new HashMap<>();
                buildHashMap(imagePart4String, postData4);

                new Thread(() -> {
                    String[] resultsArray = new String[4];
                    resultsArray[0] = sendPostRequest(device1Url, postData1);
                    Log.d("ANSWERS : Quadrant 1", resultsArray[0]);
                    resultsArray[1] = sendPostRequest(device2Url, postData2);
                    Log.d("ANSWERS : Quadrant 2", resultsArray[1]);
                    resultsArray[2] = sendPostRequest(device3Url, postData3);
                    Log.d("ANSWERS : Quadrant 3", resultsArray[2]);
                    resultsArray[3] = sendPostRequest(device4Url, postData4);
                    Log.d("ANSWERS : Quadrant 4", resultsArray[3]);

                    int digit = findDigitWithMaxConfidence(resultsArray);
                    Log.d("ANSWERS : Answer", String.valueOf(digit));
                    String stringAns = "Digit identified: " + digit;
                    saveToMasterMobile(photo, Integer.toString(digit), this.getApplicationContext());

                    new Handler(Looper.getMainLooper()).post(() -> {
                        Toast toast = Toast.makeText(getApplicationContext(), stringAns, Toast.LENGTH_SHORT);
                        toast.show();
                    });
                }).start();
            }
            else {
                Toast toast = Toast.makeText(getApplicationContext(), "Invalid photo", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v->{
            super.onBackPressed();
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
    }


}