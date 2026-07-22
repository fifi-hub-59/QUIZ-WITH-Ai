# Cosmic Trivia

A production-ready, fully native Android trivia and image-guessing game built with Jetpack Compose, Kotlin, and Room Database. 

## Features
- **Jetpack Compose UI**: 100% built with modern Compose, including Custom Animations (bounce clicks, breathing buttons, dynamic backgrounds).
- **Clean Architecture & MVVM**: Follows industry-standard architecture patterns using ViewModels, StateFlow, and Coroutines.
- **Room Database Integration**: Persistent local storage for user progress, XP, coins, unlocked avatars, and inventory items.
- **Simulated Monetization & Ads**: Pre-configured overlays for Rewarded Ads and In-App Purchases, ready to be swapped with actual AdMob and Google Play Billing dependencies.
- **Cosmic Theme**: Dark mode default, premium layout, neon styling, and adaptive spacing.

## Architecture
- **data/**: Contains Room Database entities, DAO, repository, and static datasets.
- **ui/**: Contains screens, components, view models, and the design system (theme, colors, typography).
- **ui/screens/**: Individual game screens (Splash, Home, Game, Settings, Store, Profile, Leaderboard, etc.).

## Setup & Build Instructions
1. Clone the repository and open in Android Studio.
2. Ensure JDK 17 and latest Android SDK (API 35) are installed.
3. Build the project using Gradle: `./gradlew assembleDebug` or run directly on an emulator/device.

## Publishing Checklist
- [ ] Replace SimulatedAdOverlay with real Google Mobile Ads SDK (AdMob)
- [ ] Replace SimulatedCheckoutOverlay with real Google Play Billing Library
- [ ] Set up Firebase (Analytics, Crashlytics) and add `google-services.json`
- [ ] Generate a production release keystore and configure `build.gradle.kts`
- [ ] Ensure all store assets (icon, screenshots, banner) are ready
- [ ] Update Privacy Policy URL in Play Console
