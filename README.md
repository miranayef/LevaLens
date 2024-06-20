# Bulgarian Banknote Identifier

## Overview

This application is designed to identify Bulgarian banknotes using machine learning. It was developed as part of a diploma thesis project and utilizes transfer learning with MobileNetV2 as the neural network. The application is built for Android and written in Java.

## Features

- **Banknote Identification**: The app identifies Bulgarian banknotes in real-time using the device's camera.
- **Machine Learning**: The app uses a custom trained machine learning model to identify the banknotes.
- **Data Augmentation**: This application uses data augmentation techniques to improve the performance of the model on different lighting conditions, angles, and other variations.
- **Mobile Deployment**: The model is converted to TensorFlow Lite for deployment on mobile devices.

## Dataset

The dataset for this project was collected manually, as there were no existing datasets available online for Bulgarian banknotes. The process involved taking pictures of different Bulgarian banknotes under various conditions and angles.

## Technologies Used

- **Transfer Learning**: This application uses transfer learning with the MobileNetV2 architecture. This allows for a highly accurate model without needing an extensive amount of training data.
- **TensorFlow & TensorFlow Lite**: TensorFlow is used for building the machine learning model, and TensorFlow Lite is used to convert the model for mobile deployment.
- **Android & Java**: The mobile application is developed for Android devices and is written in Java.

## How to Use

1. **Download the application**: Download and install the APK on your Android device.
2. **Open the application**: Open the app and grant the necessary permissions for the camera.
3. **Scan banknotes**: Point your device's camera at a Bulgarian banknote and take a picture. The app will attempt to identify the banknote and read the result.

## Contribution

This project is a part of a diploma thesis and is not open for contribution. However, suggestions and feedback are always welcome.

## Disclaimer

This app is a demonstration of machine learning capabilities and is not intended for real-world currency authentication. The accuracy of the app may vary depending on the quality of the image and the condition of the banknote.

## Contact

For any inquiries, suggestions, or feedback, please contact me at nayef.mira@gmail.com
