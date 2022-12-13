package com.example.group20;

import static com.example.group20.ApplicationConstants.ATTACHMENT_NAME;
import static com.example.group20.ApplicationConstants.EXTENSION;
import static com.example.group20.ApplicationConstants.HTTP;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.annotation.RequiresApi;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ServerUtil {

    public static String sendPostRequest(String requestURL, HashMap<String,String> postDataParams){
        URL url;
        String flaskResponse = "";
        HttpURLConnection connection;
        try {
            url = new URL(requestURL);
            connection = (HttpURLConnection)  url.openConnection();
            connection.setUseCaches(false);
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setConnectTimeout(2000);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");

            OutputStream outputStream = connection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
            bufferedWriter.write(getPostDataString(postDataParams));
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();

            int responseCode = connection.getResponseCode();
            //Validating flaskResponse
            if(responseCode == HttpURLConnection.HTTP_OK) {
                String line;
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                while((line = bufferedReader.readLine()) != null ) {
                    flaskResponse += line;
                }
            }
            else {
                flaskResponse ="Error uploading photo";
            }
        } catch (Exception e) {
            e.printStackTrace();
            flaskResponse = e.getMessage();
        }
        return flaskResponse;
    }

    private static String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder stringBuilder = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first)
                first = false;
            else
                stringBuilder.append("&");
            stringBuilder.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            stringBuilder.append("=");
            stringBuilder.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        return stringBuilder.toString();
    }

    public static String buildDeviceUrl(String deviceIp, String portUpload) {
        return HTTP + deviceIp + portUpload;
    }

    public static void buildHashMap(String imagePart1String, HashMap<String, String> postData1) {
        postData1.put(ATTACHMENT_NAME, imagePart1String);
        postData1.put(EXTENSION, "png");
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static void saveToMasterMobile(Bitmap bitmapImage, String dirName, Context context){
        ContextWrapper contextWrapper = new ContextWrapper(context);
//        File directory = contextWrapper.getDir(dirName, Context.MODE_PRIVATE);
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
        String mImageName="MI_"+ timeStamp +".jpg";
        ContentResolver resolver = contextWrapper.getContentResolver();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME,mImageName);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE,"image/jpeg");
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES+File.separator+"uploads"+File.separator+dirName);
        Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);
        OutputStream outputStream;
        try {
            outputStream =  resolver.openOutputStream(Objects.requireNonNull(imageUri) );
            bitmapImage.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
            Objects.requireNonNull(outputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
