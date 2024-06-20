package com.example.levalens;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.tensorflow.lite.Interpreter;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BanknoteClassifier {
    private static final String TENSORFLOW_MODEL = "bulgarian_banknote_classifierV1.4_optimized.tflite";
    private static final String LABELS_FILE = "labels.txt";
    private static final int IMAGE_SIZE = 224;
    private final Interpreter interpreter;
    private final List<String> labels;
    Context context;

    static {
        if (!OpenCVLoader.initDebug()) {
            Log.e(Constants.TAG_MAIN_ACTIVITY, Constants.INITIALIZE_OPENCV_ERROR);
        } else {
            Log.d(Constants.TAG_MAIN_ACTIVITY, Constants.INITIALIZE_OPENCV_SUCCESS);
        }
    }

    public BanknoteClassifier(AssetManager assetManager, Context context) throws IOException {
        this.context = context;
        MappedByteBuffer modelFile = loadModelFile(assetManager);
        interpreter = new Interpreter(modelFile);
        labels = loadLabels(assetManager);
        Log.d(Constants.TAG_MAIN_ACTIVITY, Constants.BANKNOTE_CLASSIFIER_LOAD_IMAGES_SUCCESS);
    }

    private MappedByteBuffer loadModelFile(AssetManager assetManager) throws IOException {
        AssetFileDescriptor fileDescriptor = assetManager.openFd(TENSORFLOW_MODEL);
        try (FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor())) {
            FileChannel fileChannel = inputStream.getChannel();
            long startOffset = fileDescriptor.getStartOffset();
            long declaredLength = fileDescriptor.getDeclaredLength();
            return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
        }
    }

    private List<String> loadLabels(AssetManager assetManager) throws IOException {
        List<String> labels = new ArrayList<>();
        try (InputStream is = assetManager.open(LABELS_FILE);
             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = br.readLine()) != null) {
                labels.add(line);
            }
        }
        return labels;
    }

    public Map<String, Float> predictImage(Bitmap bitmap, ImageView imageView) {
        Bitmap resizedBitmap = getResizedBitmap(bitmap);
        imageView.setImageBitmap(resizedBitmap);

        int[] intValues = new int[IMAGE_SIZE * IMAGE_SIZE];
        resizedBitmap.getPixels(intValues, 0, resizedBitmap.getWidth(), 0, 0, resizedBitmap.getWidth(), resizedBitmap.getHeight());

        float[][][][] input = new float[1][IMAGE_SIZE][IMAGE_SIZE][3];
        for (int i = 0; i < IMAGE_SIZE; ++i) {
            for (int j = 0; j < IMAGE_SIZE; ++j) {
                int val = intValues[i * IMAGE_SIZE + j];
                input[0][i][j][0] = ((val >> 16) & 0xFF) / 255.0f;
                input[0][i][j][1] = ((val >> 8) & 0xFF) / 255.0f;
                input[0][i][j][2] = (val & 0xFF) / 255.0f;
            }
        }

        float[][] output = new float[1][labels.size()];
        interpreter.run(input, output);

        return processResults(output);
    }

    private Bitmap getResizedBitmap(Bitmap bm) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        int minSize = Math.min(width, height);

        // Crop to square
        int x = (width - minSize) / 2;
        int y = (height - minSize) / 2;
        bm = Bitmap.createBitmap(bm, x, y, minSize, minSize);

        // Convert to Mat
        Mat src = new Mat();
        Utils.bitmapToMat(bm, src);

        // Resize
        Mat dst = new Mat();
        Imgproc.resize(src, dst, new Size(IMAGE_SIZE, IMAGE_SIZE), 0, 0, Imgproc.INTER_AREA);

        // Convert back to Bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(IMAGE_SIZE, IMAGE_SIZE, Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(dst, resizedBitmap);

        return resizedBitmap;
    }

    private Map<String, Float> processResults(float[][] output) {
        Map<String, Float> result = new HashMap<>();
        for (int i = 0; i < labels.size(); ++i) {
            result.put(labels.get(i), output[0][i]);
        }

        return result;
    }
}
