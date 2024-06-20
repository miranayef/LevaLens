package com.example.levalens;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.util.Pair;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.preference.PreferenceManager;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;

public class BanknoteResultProcessor {

    private static final String DEFAULT_CLASS = "No banknote detected";
    private final TextView banknoteNameTextView;
    private final TextView confidenceScoreTextView;
    private final BanknoteClassifier classifier;
    private final Context context;
    private TextToSpeech textToSpeech;
    private MediaPlayer player;

    public BanknoteResultProcessor(TextView banknoteNameTextView, TextView confidenceScoreTextView, BanknoteClassifier classifier, Context context) {
        this.banknoteNameTextView = banknoteNameTextView;
        this.confidenceScoreTextView = confidenceScoreTextView;
        this.classifier = classifier;
        this.context = context;
    }

    @SuppressLint("SetTextI18n")
    public void classifyImage(Bitmap bitmap, ImageView imageView) {
        Log.d(Constants.TAG_MAIN_ACTIVITY, Constants.CLASSIFY_IMAGE_LOADING_MSG);

        // Predict the banknote type using the classifier
        Map<String, Float> results = classifier.predictImage(bitmap, imageView);
        Log.d(Constants.TAG_MAIN_ACTIVITY, Constants.CLASSIFY_IMAGE_RESULTS_MSG + results.toString());

        // Get the detected class with the highest probability
        Pair<String, Float> detectedClassWithMaxProb = getDetectedClassWithMaxProbability(results);

        // Retrieve user preferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String language = preferences.getString("language", "en");
        boolean hapticFeedbackEnabled = preferences.getBoolean("haptic_feedback", false);

        // Update UI with the detected banknote information
        String resultText = getResultText(detectedClassWithMaxProb.first, language);
        banknoteNameTextView.setText(resultText);
        String confidenceText = String.format(Locale.getDefault(), "%.2f%%", detectedClassWithMaxProb.second * 100);
        confidenceScoreTextView.setText(confidenceText);

        // Play the appropriate audio feedback
        playAudio(resultText, detectedClassWithMaxProb.first, language);

        // Provide haptic feedback if enabled and the phone is in silent mode
        if (hapticFeedbackEnabled && isPhoneInSilentMode()) {
            provideHapticFeedback(detectedClassWithMaxProb.first);
        }

        Log.d(Constants.TAG_MAIN_ACTIVITY, Constants.CLASSIFY_IMAGE_RESULT_USER_FRIENDLY + " " + resultText);
    }

    private boolean isPhoneInSilentMode() {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        return currentVolume == 0;
    }

    private void provideHapticFeedback(String detectedClass) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null && vibrator.hasVibrator()) {
            long[] vibrationPattern;
            switch (getBanknoteValue(detectedClass)) {
                case "5":
                    vibrationPattern = new long[]{0, 200};
                    break;
                case "10":
                    vibrationPattern = new long[]{0, 200, 200, 200};
                    break;
                case "20":
                    vibrationPattern = new long[]{0, 200, 200, 200, 200, 200};
                    break;
                case "50":
                    vibrationPattern = new long[]{0, 200, 200, 200, 200, 200, 200, 200};
                    break;
                case "100":
                    vibrationPattern = new long[]{0, 200, 200, 200, 200, 200, 200, 200, 200, 200};
                    break;
                default:
                    return;
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createWaveform(vibrationPattern, -1));
            } else {
                vibrator.vibrate(vibrationPattern, -1); // Deprecated method for API level < 26
            }
        }
    }

    private Pair<String, Float> getDetectedClassWithMaxProbability(Map<String, Float> results) {
        String detectedClass = DEFAULT_CLASS;
        float maxProbability = 0;
        for (Map.Entry<String, Float> entry : results.entrySet()) {
            if (entry.getValue() > maxProbability) {
                maxProbability = entry.getValue();
                detectedClass = entry.getKey();
            }
        }

        // Determine the detected class based on the maximum probability - set to 50% threshold
        if (maxProbability < 0.50) {
            detectedClass = DEFAULT_CLASS;
        }
        return new Pair<>(detectedClass, maxProbability);
    }

    private String getResultText(String detectedClass, String language) {
        String resultText = BanknoteType.getValueByKey(detectedClass)
                .orElse(context.getString(R.string.noBanknoteDetected));

        if (!resultText.equals(context.getString(R.string.noBanknoteDetected))) {
            if (language.equals("bg")) {
                return resultText + " лева";
            } else if (language.equals("en")) {
                return resultText + " leva";
            }
        }
        return resultText;
    }

    private void playAudio(String resultText, String detectedClass, String language) {
        if (language.equals("bg")) {
            useMediaPlayer(detectedClass);
        } else {
            useTextToSpeech(resultText);
        }
    }

    private void useTextToSpeech(String text) {
        if (textToSpeech == null) {
            textToSpeech = new TextToSpeech(context, status -> {
                if (status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.ENGLISH);
                    textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
                }
            });
        } else {
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    private void useMediaPlayer(String detectedClass) {
        releaseMediaPlayer();

        player = new MediaPlayer();
        try {
            AssetFileDescriptor afd = context.getAssets().openFd(getAudioFilename(detectedClass));
            player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            player.prepare();
            player.start();
            player.setOnCompletionListener(mp -> {
                releaseMediaPlayer();
                Log.d(Constants.TAG_MAIN_ACTIVITY, "MediaPlayer released after playback");
            });
        } catch (IOException e) {
            Log.e(Constants.TAG_MAIN_ACTIVITY, "Failed to play audio", e);
            releaseMediaPlayer();
        }
    }

    private void releaseMediaPlayer() {
        if (player != null) {
            player.release();
            player = null;
        }
    }

    private String getAudioFilename(String detectedClass) {
        return BanknoteType.getByKey(detectedClass)
                .map(BanknoteType::getAudioFilename)
                .orElse("default.m4a");
    }

    private String getBanknoteValue(String detectedClass) {
        return BanknoteType.getByKey(detectedClass)
                .map(BanknoteType::getValue)
                .orElse("default");
    }
}
