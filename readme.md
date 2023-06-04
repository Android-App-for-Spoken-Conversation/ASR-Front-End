# ASR-Frontend

ASR-Frontend is an Android application developed using the native Android tech stack. It allows users to upload and manage MP3 files using Firebase Storage. This repository contains the frontend code for the ASR (Automatic Speech Recognition) application.

## Tech Stack

The ASR-Frontend application is built using the following technologies:

- **Android**: The ASR-Frontend application is developed natively for the Android platform, utilizing the Android SDK and framework.

- **Firebase Storage**: Firebase Storage is a cloud storage service provided by Firebase, which is used to store and manage the MP3 files uploaded by users in the ASR-Frontend application.

- **OkHttp**: OkHttp is an open-source HTTP client for Android that provides a simple and efficient way to make network requests. It is used in the ASR-Frontend application to send requests to the backend server.

- **Demux**: Demux is a module used in the ASR-Frontend application to separate audio streams and extract specific audio channels from the uploaded MP3 files.

## Workflow

The workflow of the ASR-Frontend application is as follows:

1. The frontend Android application records the voice of the user.

2. The recorded voice is sent to Firebase Storage for storage and management.

3. Upon receiving the response from Firebase, the frontend sends another request to the Flask API. The request includes the URL of the file in the request body.

4. In the backend, the Flask API receives the request and retrieves the file from Firebase Storage using the provided URL.

5. The Flask API converts the file to WAV format to prepare it for processing with the ASR model.

6. The ASR model uses the converted WAV file to perform Automatic Speech Recognition, converting the speech into text.

7. The resulting text is sent back to the Android app running on the mobile device.

8. The Android app receives the text response and performs further actions based on the ASR output.

## Getting Started

To run the ASR-Frontend application on your local machine, follow these steps:

1. Clone the repository:

   ```
     https://github.com/Android-App-for-Spoken-Conversation/ASR-Front-End.git
   ```

2. Open the project in Android Studio.

3. Connect your Android device or start an emulator.

4. Build and run the application.

## Configuration

Before running the ASR-Frontend application, make sure to configure the Firebase Storage. Follow these steps:

1. Create a new Firebase project on the [Firebase console](https://console.firebase.google.com/).

2. Enable Firebase Storage service for your project.

3. Download the `google-services.json` file from the Firebase console.

4. Place the `google-services.json` file in the `app` directory of the ASR-Frontend project.

## Contributing

Contributions to the ASR-Frontend project are welcome! If you find any bugs or want to add new features, please open an issue or submit a pull request. Make sure to follow the project's code style and guidelines.

## License

ASR-Frontend is released under the [MIT License](LICENSE). Feel free to use, modify, and distribute the code as per the terms of the license.

## Contact

If you have any questions or suggestions regarding the ASR-Frontend application, feel free to contact the development team at (mailto:20bcs048@iiitdwd.ac.in, 20bcs112@iiitdwd.ac.in).
