# TouristTracker Android Application

This repository contains the documentation for the TouristTracker Android application. The TouristTracker system enables users to track their travel activities and share them with others. This readme file provides an overview of the Android application's architecture, functionality, and usage.

## 1. System Overview

### 1.1 System Architecture

The TouristTracker Android application follows a client-server architecture. The client-side component, which is the Android application, collects user-specific data such as GPS coordinates and current transportation vehicle information. The server-side component handles database services and back-end functionality. The communication between the Android application and the server occurs via JSON data interchange. The figure below illustrates the communication flow between the client and server:

### 1.2 Mobile Application

The TouristTracker Android application is designed for devices running Android 4.0 and above. It leverages various functionalities provided by the Android Application Programming Interface (API). The application is a self-contained unit and does not rely on any other Android-related software components except for the GPS and background operation modules.

The Android application communicates with the server to exchange GPS data, updates, and user-entered data. The communication takes place over TCP/IP using the HTTP protocol with JSON payloads. The Android platform provides abstractions for network communication and GPS interfaces, ensuring seamless integration with the device's hardware.

The application stores different types of data on the smartphone. User data, which is used only once, is stored in preferences on the smartphone. These preferences are private and can only be accessed by the TouristTracker app. Tracking data, choice of conveyance, and timestamps are stored in a MongoDB database on the server. The application also maintains a local SQLite database on the smartphone until the data is uploaded to the server.

To optimize storage and accuracy, the application utilizes the Minimal Time/Minimal Distance Method. This method determines the relevance and importance of GPS coordinates. By setting parameters for minimum time and distance, the application checks if the user has moved significantly within the specified time interval. If the user has moved beyond the minimum distance, the new coordinate is saved. Additionally, the application attempts to improve accuracy by making three more attempts if the coordinate's accuracy is less than 10.

The performance of the Android application is expected to be efficient and smooth. It does not involve resource-intensive or graphics-intensive operations. Most server queries involve small data sets, except for long-term offline mode where all collected position data is sent in a single transaction. Transitioning between screens and menus requires minimal computation and happens quickly. As long as there is a stable signal between the device and the server, transactions should complete within a few seconds. The distance calculation and sustainability rating algorithms execute in fractions of a second.

## 2. Functionality

The TouristTracker Android application provides the following functionality:

- GPS data collection: The application collects GPS coordinates to track the user's travel activities.
- Transport vehicle information: The user can provide information about the current transportation vehicle they are using.
- Synchronization with the server: The application frequently synchronizes with the TouristTracker server to exchange data using TCP/IP and the HTTP protocol with JSON payloads.
- Local data storage: The application stores relevant data on the device, including user preferences and tracking data, until it is uploaded to the server.
- Data filtering: The application uses the Minimal Time/Minimal Distance Method to filter and store only relevant GPS coordinates.
- Accuracy improvement: The application attempts to improve accuracy by making additional attempts to obtain coordinates with better accuracy.
- Minimal performance impact: The application is designed to have minimal performance impact, providing quick screen transitions and menu operations.

## Conclusion

The TouristTracker Android application is a key component of the client-server system, enabling users to track their

 travel activities. It collects GPS data, exchanges information with the server, and provides a seamless user experience. For detailed implementation instructions and code, please refer to the documentation and source code provided in this repository.
