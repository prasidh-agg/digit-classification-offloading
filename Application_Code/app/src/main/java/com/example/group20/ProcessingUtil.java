package com.example.group20;

import android.graphics.Bitmap;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.regex.Pattern;

public class ProcessingUtil {
    public static String encodeImageToBase64(Bitmap image) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] b = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(b,Base64.DEFAULT);
    }

    public static boolean validateIpAddress(String ip) {
        String validIpPattern = "^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
        Pattern pattern = Pattern.compile(validIpPattern);
        return pattern.matcher(ip).matches();
    }
    public static Bitmap[] divideBitmap(Bitmap src) {
        Bitmap[] imageParts = new Bitmap[4];
        imageParts[0] = Bitmap.createBitmap(src, 0, 0, src.getWidth() / 2, src.getHeight() / 2);
        imageParts[1] = Bitmap.createBitmap(src, src.getWidth() / 2, 0, src.getWidth() / 2, src.getHeight() / 2);
        imageParts[2] = Bitmap.createBitmap(src, 0, src.getHeight() / 2, src.getWidth() / 2, src.getHeight() / 2);
        imageParts[3] = Bitmap.createBitmap(src, src.getWidth() / 2, src.getHeight() / 2, src.getWidth() / 2, src.getHeight() / 2);
        return imageParts;
    }
    public static int findDigitWithMaxConfidence(String[] resultsArray) {
        HashMap<Integer, Float> resultMap = new HashMap<>();
        float maxVal = Float.MIN_VALUE;
        int digit = 0;
        for(int i = 0; i < 4; i++) {
            String[] splitValues = resultsArray[i].split(",");
            int digitClassified = Integer.parseInt(splitValues[0]);
            float confidence = Float.parseFloat(splitValues[1]);
            resultMap.put(digitClassified, resultMap.containsKey(digitClassified) ? resultMap.get(digitClassified) + confidence : confidence);
            if(resultMap.get(digitClassified) > maxVal) {
                digit = digitClassified;
                maxVal = resultMap.get(digitClassified);
            }
        }
        return digit;
    }

}
