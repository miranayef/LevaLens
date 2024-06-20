package com.example.levalens.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.view.PreviewView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.preference.PreferenceManager;

import com.example.levalens.Constants;
import com.example.levalens.Helper.CameraHelper;
import com.example.levalens.Helper.ImagePickerHelper;
import com.example.levalens.Helper.NavigationHelper;
import com.example.levalens.PermissionManager;
import com.example.levalens.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final long CAPTURE_BUTTON_DEBOUNCE_TIME = 2000;

    // UI components
    private PreviewView previewView;
    private DrawerLayout drawerLayout;
    private ImageButton flashButton;
    private NavigationView navigationView;
    private BottomSheetDialog bottomSheetDialog;

    // Helpers
    private CameraHelper cameraHelper;
    private ImagePickerHelper imagePickerHelper;
    private NavigationHelper navigationHelper;

    // Button state
    private boolean isCaptureButtonEnabled = true;
    private final Handler handler = new Handler();
    private Runnable enableCaptureButtonRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        setupHelpers();
        applySettings();

        if (PermissionManager.checkPermission(this, Manifest.permission.CAMERA, Constants.REQUEST_CAMERA_PERMISSION)) {
            cameraHelper.startCamera();
        }
    }

    private void initializeViews() {
        previewView = findViewById(R.id.previewView);
        drawerLayout = findViewById(R.id.drawer_layout);
        flashButton = findViewById(R.id.flashButton);
        navigationView = findViewById(R.id.navigation_view);

        ImageButton captureButton = findViewById(R.id.captureButton);
        ImageButton uploadButton = findViewById(R.id.uploadButton);
        ImageButton helpButton = findViewById(R.id.helpButton);
        ImageButton drawerButton = findViewById(R.id.drawerButton);

        captureButton.setOnClickListener(v -> {
            if (isCaptureButtonEnabled) {
                isCaptureButtonEnabled = false;
                cameraHelper.takePhoto(this);
                handler.postDelayed(enableCaptureButtonRunnable, CAPTURE_BUTTON_DEBOUNCE_TIME);
            }
        });

        uploadButton.setOnClickListener(v -> imagePickerHelper.openImagePicker(Constants.REQUEST_IMAGE_PICKER));
        flashButton.setOnClickListener(v -> toggleFlash());
        helpButton.setOnClickListener(v -> showHelpBottomSheet());
        drawerButton.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        enableCaptureButtonRunnable = () -> isCaptureButtonEnabled = true;
    }

    private void toggleFlash() {
        cameraHelper.toggleTorch();
        flashButton.setImageResource(cameraHelper.isTorchEnabled() ? R.drawable.ic_torch_on : R.drawable.ic_torch_off);
    }

    private void showHelpBottomSheet() {
        if (bottomSheetDialog != null && bottomSheetDialog.isShowing()) return;

        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_help, null);
        bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    private void setupHelpers() {
        cameraHelper = new CameraHelper(this, previewView);
        imagePickerHelper = new ImagePickerHelper(this);
        navigationHelper = new NavigationHelper(this, drawerLayout, navigationView);
    }

    private void applySettings() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean torchOnStartup = preferences.getBoolean("torch_on_startup", false);
        String language = preferences.getString("language", "en");

        if (torchOnStartup) {
            cameraHelper.enableTorch(true);
            flashButton.setImageResource(cameraHelper.isTorchEnabled() ? R.drawable.ic_torch_on : R.drawable.ic_torch_off);
        }

        changeLanguage(language);
        reloadNavigationDrawer();
        cameraHelper.startCamera();
    }

    private void changeLanguage(String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }

    private void reloadNavigationDrawer() {
        if (navigationView != null) {
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.drawer_menu);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean storedTorchState = preferences.getBoolean("torch_state", false);
        cameraHelper.enableTorch(storedTorchState);
        flashButton.setImageResource(storedTorchState ? R.drawable.ic_torch_on : R.drawable.ic_torch_off);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP) && isCaptureButtonEnabled) {
            isCaptureButtonEnabled = false;
            cameraHelper.takePhoto(this);
            handler.postDelayed(enableCaptureButtonRunnable, CAPTURE_BUTTON_DEBOUNCE_TIME);
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            Log.e(Constants.TAG_MAIN_ACTIVITY, Constants.ON_ACTIVITY_RESULT_RECEIVE_IMAGE_ERROR);
            return;
        }
        if (data == null) {
            Log.e(Constants.TAG_MAIN_ACTIVITY, Constants.ON_ACTIVITY_RESULT_DATA_NULL_ERROR);
            return;
        }
        if (requestCode == Constants.REQUEST_IMAGE_PICKER) {
            Bitmap imageBitmap = imagePickerHelper.handleImagePickerResult(data);
            if (imageBitmap != null) {
                ResultActivity.bitmap = imageBitmap;
                navigateToResultActivity();
            }
        } else {
            Log.e(Constants.TAG_MAIN_ACTIVITY, Constants.ON_ACTIVITY_RESULT_UNKNOWN_REQUEST_CODE + requestCode);
        }
    }

    public void navigateToResultActivity() {
        Intent intent = new Intent(this, ResultActivity.class);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(Constants.TAG_MAIN_ACTIVITY, Constants.CAMERA_PERMISSION_GRANTED);
                Toast.makeText(this, Constants.CAMERA_PERMISSION_GRANTED, Toast.LENGTH_SHORT).show();
                cameraHelper.startCamera();
            } else {
                Log.e(Constants.TAG_MAIN_ACTIVITY, Constants.CAMERA_PERMISSION_DENIED);
                PermissionManager.showPermissionDeniedDialog(this);
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (bottomSheetDialog != null) {
            bottomSheetDialog.dismiss();
        }
        handler.removeCallbacks(enableCaptureButtonRunnable);
        super.onDestroy();
    }
}
