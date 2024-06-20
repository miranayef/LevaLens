package com.example.levalens.Activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.levalens.BanknoteClassifier;
import com.example.levalens.BanknoteResultProcessor;
import com.example.levalens.Constants;
import com.example.levalens.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.IOException;

public class ResultActivity extends AppCompatActivity {
    private BottomSheetDialog bottomSheetDialog;

    public static Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        ImageView resultImageView = findViewById(R.id.resultImageView);
        TextView banknoteNameTextView = findViewById(R.id.banknoteNameTextView);
        TextView confidenceScoreTextView = findViewById(R.id.confidenceScoreTextView);
        ImageView backButton = findViewById(R.id.backButton);
        ImageButton helpButton = findViewById(R.id.helpButton);

        if (bitmap != null) {
            Glide.with(this)
                    .load(bitmap)
                    .into(resultImageView);
        }

        // Initialize the classifier
        BanknoteClassifier classifier = null;
        try {
            classifier = new BanknoteClassifier(getAssets(), this);
        } catch (IOException e) {
            Log.e(Constants.TAG_MAIN_ACTIVITY, Constants.BANKNOTE_CLASSIFIER_LOAD_IMAGES_ERROR + e.getMessage());
        }

        // Initialize BanknoteResultProcessor
        BanknoteResultProcessor banknoteResultProcessor = new BanknoteResultProcessor(banknoteNameTextView, confidenceScoreTextView, classifier, this);
        banknoteResultProcessor.classifyImage(bitmap, resultImageView);

        helpButton.setOnClickListener(v -> showHelpBottomSheet());
        backButton.setOnClickListener(v -> finish());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bitmap = null;
    }

    private void showHelpBottomSheet() {
        if (bottomSheetDialog != null && bottomSheetDialog.isShowing()) {
            return;
        }

        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_help, null);
        bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

}
