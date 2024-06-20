package com.example.levalens.Helper;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.levalens.BitmapUtils;
import com.example.levalens.Constants;

import java.io.IOException;

public class ImagePickerHelper {

    public static final String IMAGE_TYPE = "image/*";
    private final Activity activity;

    public ImagePickerHelper(Activity activity) {
        this.activity = activity;
    }

    public void openImagePicker(int requestCode) {
        Intent pickImageIntent = new Intent(Intent.ACTION_PICK);
        pickImageIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_TYPE);
        activity.startActivityForResult(pickImageIntent, requestCode);
    }

    @Nullable
    public Bitmap handleImagePickerResult(@NonNull Intent data) {
        Uri selectedImage = data.getData();
        if (selectedImage == null) {
            Log.e(Constants.TAG_MAIN_ACTIVITY, Constants.HANDLE_IMAGE_RESULT_NO_INTENT_ERROR);
            return null;
        }

        Bitmap imageBitmap = null;
        try {
            // Load the image bitmap
            imageBitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), selectedImage);

            int rotationDegrees = BitmapUtils.getRotationFromGallery(activity, selectedImage);

            if (rotationDegrees != 0) {
                imageBitmap = BitmapUtils.rotateBitmap(imageBitmap, rotationDegrees);
            }
        } catch (IOException e) {
            Log.e(Constants.TAG_MAIN_ACTIVITY, Constants.HANDLE_IMAGE_RESULT_LOAD_IMAGE_ERROR , e);
        }

        return imageBitmap;
    }
}
