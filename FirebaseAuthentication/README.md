# Compose Multiplatform + Firebase Authentication
![kotlin-version](https://img.shields.io/badge/kotlin-1.9.20-blue)
[![Platform](https://img.shields.io/badge/Platform-Android-brightgreen.svg)](#)
[![Platform](https://img.shields.io/badge/Platform-iOS-black.svg)](#)
[![Platform](https://img.shields.io/badge/Platform-jvm-blue.svg)](#)

![Twitter post - 1](https://github.com/dwarshb/KMM/assets/32565650/505a5fa0-61d8-40ce-bc55-ffce67f319dc)

The project was initial built using [Kotlin Multiplatform Wizard](https://kmp.jetbrains.com/)which gives us complete project setup with required dependencies and libraries. Later files for Authentication and other were added as per the requirement.

# Tech Stack 📚
- Kotlin Multiplatform
- Kotlin Coroutines
- Compose Multiplatform
- Ktor
- SQLDelight

This is a Kotlin Multiplatform project targeting Android, iOS, Desktop.

* `/composeApp` is for code that will be shared across your Compose Multiplatform applications.
  It contains several subfolders:
  - `commonMain` is for code that’s common for all targets.
  - Other folders are for Kotlin code that will be compiled for only the platform indicated in the folder name.
    For example, if you want to use Apple’s CoreCrypto for the iOS part of your Kotlin app,
    `iosMain` would be the right folder for such calls.

* `/iosApp` contains iOS applications. Even if you’re sharing your UI with Compose Multiplatform, 
  you need this entry point for your iOS app. This is also where you should add SwiftUI code for your project.


Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)…

---


<div style="width:100%">
	<div style="width:50%; display:inline-block">
		<h2 align="center">
      :handshake: Open for Contribution
		</h2>	
	</div>	
</div>

