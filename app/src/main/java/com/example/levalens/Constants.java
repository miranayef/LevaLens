package com.example.levalens;

public class Constants {
    public static final String TAG_MAIN_ACTIVITY = "MainActivity";
    public static final String INITIALIZE_OPENCV_SUCCESS = "OpenCV initialized successfully";
    public static final String INITIALIZE_OPENCV_ERROR = "OpenCV initialization failed";

    public static final String ON_ACTIVITY_RESULT_RECEIVE_IMAGE_ERROR = "Failed to receive image";
    public static final String ON_ACTIVITY_RESULT_DATA_NULL_ERROR = "Data returned is null";
    public static final String ON_ACTIVITY_RESULT_UNKNOWN_REQUEST_CODE = "Unknown request code: ";

    public static final String HANDLE_IMAGE_RESULT_NO_INTENT_ERROR = "No image data found in intent";
    public static final String HANDLE_IMAGE_RESULT_LOAD_IMAGE_ERROR = "Failed to load image";

    public static final String CLASSIFY_IMAGE_LOADING_MSG = "Classifying image...";
    public static final String CLASSIFY_IMAGE_RESULTS_MSG = "Classification results: ";
    public static final String CLASSIFY_IMAGE_RESULT_USER_FRIENDLY = "Results: ";

    public static final String BANKNOTE_CLASSIFIER_LOAD_IMAGES_SUCCESS = "Model and labels loaded successfully";

    public static final String CAMERA_PERMISSION_GRANTED = "Camera permission granted";
    public static final String CAMERA_PERMISSION_DENIED = "Camera permission denied";
    public static final String CAMERA_START_ERROR = "Error starting camera: ";

    public static final String BANKNOTE_CLASSIFIER_LOAD_IMAGES_ERROR = "Failed to initialize classifier";
    public static final int REQUEST_CAMERA_PERMISSION = 200;
    public static final int REQUEST_IMAGE_PICKER = 2;
}
