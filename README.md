# Notification2Webhook

## Table of Contents

1. [Introduction](#introduction)
2. [Features](#features)
3. [Installation](#installation)
4. [Usage](#usage)
5. [Troubleshooting](#troubleshooting)
6. [Contributing](#contributing)
7. [License](#license)

## Introduction

**Notification2Webhook** is an Android application designed to forward notifications from selected apps to a specified webhook URL. This is esspecially useful for Tradingview Indicators to send buy and sell signals to a trading bot without having to pay for a Tradingview account. 

## Features

- **Notification Forwarding**: Forward notifications from selected apps to a specified webhook URL.
- **Notification Listener**: Listen for notifications from selected apps.
- **Toggle Notification Forwarding**: Enable or disable notification forwarding with a toggle switch.
- **App Selection**: Select which apps to monitor for notifications.
- **Webhook URL Configuration**: Set the webhook URL where notifications will be forwarded.
- **Permission Management**: Request necessary permissions to read notifications and forward them.

## Installation

### Prerequisites

- Android device running Android 7.0 (Nougat) or higher.
- Internet connection for downloading the app and configuring the webhook URL.

### Steps

1. **Download the APK**:
   - Download the `Notification2Webhook.apk` file from the [releases page](https://github.com/BigShoots/NotificationWebhookApp/releases).

2. **Install the APK**:
   - Transfer the downloaded APK file to your Android device.
   - Open the file manager on your device and navigate to the location where you transferred the APK file.
   - Tap on the APK file to start the installation process.
   - Follow the on-screen instructions to complete the installation.

3. **Grant Permissions**:
   - After installation, open the app.
   - The app will prompt you to grant the necessary permissions to read notifications and forward them. Follow the on-screen instructions to grant these permissions.

## Usage

### Setting Up the Webhook URL

1. **Open the App**:
   - Launch `Notification2Webhook` from your app drawer.

2. **Configure the Webhook URL**:
   - In the main screen, tap on the "Set Webhook URL" button.
   - Enter the webhook URL where you want to forward the notifications.
   - Tap "Save" to save the webhook URL.

### Selecting Apps to Monitor

1. **Open the App**:
   - Launch `Notification2Webhook` from your app drawer.

2. **Select Apps**:
   - In the main screen, you will see a list of installed apps.
   - Check the box next to each app you want to monitor for notifications.

3. **Save App List**:
   - Tap the Save Apps button to save the app list.
  

### Enabling Notification Forwarding

1. **Open the App**:
   - Launch `Notification2Webhook` from your app drawer.

2. **Enable Notification Forwarding**:
   - Toggle the "Forward Notifications" switch to enable or disable notification forwarding.

## Troubleshooting

### Common Issues and Solutions

1. **App Crashes on Launch**:
   - **Cause**: The app might be crashing due to missing permissions or incorrect configuration.
   - **Solution**: Ensure that you have granted all necessary permissions. Check the logcat output for any error messages that can provide more information.

2. **Notifications Are Not Being Forwarded**:
   - **Cause**: The app might not have the necessary permissions to read notifications, or the webhook URL might be incorrect.
   - **Solution**: Verify that the app has the necessary permissions. Check the webhook URL configuration and ensure it is correct.



## Contributing

We welcome contributions from the community! If you have any suggestions, bug reports, or feature requests, please open an issue or submit a pull request on our [GitHub repository](https://github.com/BigShoots/NotificationWebhookApp).

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for more details.
