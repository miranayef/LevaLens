package com.example.levalens.Helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraControl;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import com.example.levalens.Activity.MainActivity;
import com.example.levalens.Activity.ResultActivity;
import com.example.levalens.BitmapUtils;
import com.example.levalens.Constants;
import com.example.levalens.R;
import com.google.common.util.concurrent.ListenableFuture;

import java.nio.ByteBuffer;
import java.util.concurrent.ExecutionException;

import android.media.MediaPlayer;

public class CameraHelper {
    private final Context context;
    private final PreviewView previewView;
    private Camera camera;
    private CameraControl cameraControl;
    private ImageCapture imageCapture;
    private boolean isTorchEnabled;

    public CameraHelper(Context context, PreviewView previewView) {
        this.context = context;
        this.previewView = previewView;
    }

    public void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(context);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                Log.e(Constants.TAG_MAIN_ACTIVITY, Constants.CAMERA_START_ERROR, e);
            }
        }, ContextCompat.getMainExecutor(context));
    }

    private void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String cameraPreference = preferences.getString("lens_facing", "back");

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(cameraPreference.equals("front") ? CameraSelector.LENS_FACING_FRONT : CameraSelector.LENS_FACING_BACK)
                .build();

        Preview preview = new Preview.Builder().build();
        imageCapture = new ImageCapture.Builder().build();

        cameraProvider.unbindAll();
        camera = cameraProvider.bindToLifecycle((MainActivity) context, cameraSelector, preview, imageCapture);
        cameraControl = camera.getCameraControl();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());
        isTorchEnabled = isTorchEnabled();
        enableTorch(isTorchEnabled);
    }

    public void takePhoto(MainActivity mainActivity) {
        if (camera == null || imageCapture == null) return;

        imageCapture.takePicture(ContextCompat.getMainExecutor(context), new ImageCapture.OnImageCapturedCallback() {
            @Override
            public void onCaptureSuccess(@NonNull ImageProxy imageProxy) {
                playShutterSound();

                Bitmap bitmap = imageProxyToBitmap(imageProxy);
                imageProxy.close();
                if (bitmap != null) {
                    ResultActivity.bitmap = bitmap;
                    mainActivity.navigateToResultActivity();
                }
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                Log.e(Constants.TAG_MAIN_ACTIVITY, "Photo capture failed: " + exception.getMessage(), exception);
            }
        });
    }

    private Bitmap imageProxyToBitmap(ImageProxy imageProxy) {
        ByteBuffer buffer = imageProxy.getPlanes()[0].getBuffer();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        int rotationDegrees = imageProxy.getImageInfo().getRotationDegrees();
        return BitmapUtils.rotateBitmap(bitmap, rotationDegrees);
    }

    private void playShutterSound() {
        MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.shutter_sound);
        mediaPlayer.setOnCompletionListener(MediaPlayer::release);
        mediaPlayer.start();
    }

    public void toggleTorch() {
        enableTorch(!isTorchEnabled);
    }

    public boolean isTorchEnabled() {
        return isTorchEnabled;
    }

    public void enableTorch(boolean enable) {
        isTorchEnabled = enable;
        if (cameraControl != null) {
            cameraControl.enableTorch(enable);
        }
        // Store the torch state in SharedPreferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("torch_state", enable);
        editor.apply();
    }
}