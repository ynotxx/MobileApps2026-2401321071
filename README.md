# MovieLog Android Application

**Developer**: Antonio Antonov  
**Faculty Number**: 2401321071  
**Package Name**: `com.movielog.mobileapps2026_2401321071`

MovieLog is a modern Android application built with Kotlin, Jetpack components, Room Local Database, OMDb REST API, Firebase Authentication, Cloud Firestore, and ZXing QR code sharing.

## Package & Application ID
- **Package Name**: `com.movielog.mobileapps2026_2401321071`
- **Min SDK**: API 24 (Android 7.0)
- **Target SDK**: API 35 (Android 15) / Compile SDK 35

## Material 3 Light / Dark Theme & UI Design
MovieLog fully supports Material 3 Dynamic Light and Dark themes (`Theme.Material3.DayNight.NoActionBar`). Color roles, Material Cards, Switches, and Text Inputs adapt to system theme preferences.

### Application Screenshots
- **Collection View**: Active collection with FloatingActionButton.
- **Search Screen**: Real-time OMDb API search results with Coil poster loading.
- **History Timeline**: Multi-view-type timeline grouped descending by year.
- **Profile & QR Code**: Profile account management and ZXing QR code generator/scanner.

## Architecture & Design Patterns
MovieLog follows the official Android Architecture Guidelines using the MVVM (Model-View-ViewModel) pattern and Repository layer:

- **UI Layer (`com.movielog.mobileapps2026_2401321071.ui`)**:
  - `MainActivity`: Single-activity hosting Jetpack Navigation with `BottomNavigationView`.
  - `CollectionFragment`: Displays active collection movies in a `RecyclerView` with `ListAdapter` and `DiffUtil`.
  - `SearchFragment`: Performs online movie searches via OMDb REST API.
  - `MovieDetailsFragment`: Displays movie details, rating input with validation, notes, watchlist toggle, and Share Intent.
  - `HistoryFragment`: Displays viewing history organized in a multi-view-type timeline grouped descending by year.
  - `ProfileFragment` & `ProfileDetailFragment`: Manages user profiles, QR code generation, and QR code scanning.

- **ViewModel Layer (`com.movielog.mobileapps2026_2401321071.viewmodel`)**:
  - `MovieViewModel`: Exposes `StateFlow` streams (`allMovies`, `watchlistMovies`, `watchedMovies`, `viewingHistory`, `searchState`) to the UI using `viewModelScope`.

- **Data Layer (`com.movielog.mobileapps2026_2401321071.data`)**:
  - `MovieRepository`: Centralizes access to local Room database and remote Retrofit network service.
  - `FirebaseAuthRepository`: Handles Firebase Authentication and Cloud Firestore user profiles.

## Room Database Schema
The local database uses Room v2 with two entities and a Foreign Key constraint:

1. **`movies` Entity**:
   - `localId` (Primary Key, Auto-generate Long)
   - `apiId` (String, OMDb IMDb ID for duplicate checking)
   - `title` (String)
   - `year` (Int)
   - `posterUrl` (String)
   - `genre` (String)
   - `synopsis` (String)
   - `inWatchlist` (Boolean)
   - `watched` (Boolean)
   - `rating` (Double?, 0.0 to 10.0 in 0.1 steps)
   - `notes` (String?)

2. **`movie_history` Entity**:
   - `historyId` (Primary Key, Auto-generate Long)
   - `movieId` (Foreign Key -> `movies.localId`, CASCADE delete)
   - `watchedAt` (Long timestamp in milliseconds)
   - `rating` (Float?, optional rating for this viewing)

## External REST API Integration
- **Service**: OMDb API (`https://www.omdbapi.com/`) via Retrofit 2 + Gson Converter + OkHttp Logging Interceptor.
- **Image Loading**: Coil (`io.coil-kt:coil:2.7.0`) for asynchronous image caching and display.
- **Duplicate Prevention**: Room queries `countByApiId` and `getMovieByApiId` check existing local entries before importing.

## Firebase & QR Code Profile Sharing
- **Firebase Auth & Firestore**: Authenticates users and stores `UserProfile` documents (`userId`, `username`, `email`, `bio`).
- **ZXing QR Generator & Scanner**:
  - `QrCodeUtils`: Generates a square `Bitmap` encoding the user's profile ID.
  - `ScanContract` (Activity Result API): Scans another user's QR code and opens their Firestore profile in `ProfileDetailFragment`.

## Setup & API Key Instructions
1. Open the project in Android Studio.
2. Ensure OMDb API key is set in `MovieApiService.kt` (`const val API_KEY = "b9bd48a6"`).
3. Verify that `app/google-services.json` is configured in the `app` directory for Firebase Authentication and Firestore before syncing Gradle.
4. Connect your Android device or start an emulator running API 24+.
5. Sync project with Gradle files and click **Run** (`Shift + F10`).

## Test Coverage & Execution
- **Unit Tests (`app/src/test`)**:
  - `RatingValidatorTest`: Validates 0.0–10.0 range, 0.1 step enforcement, null handling, out-of-range rejection, and malformed float rejection.
  - `MovieLogicTest`: Tests watched/unwatched toggling, history entry preservation, and watchlist/watched list filtering.
- **UI Tests (`app/src/androidTest`)**:
  - `NavigationEspressoTest`: Verifies activity launch, UI components, and navigation host initialization.

Execute unit tests via Gradle:
```bash
./gradlew testDebugUnitTest
```

Execute Espresso UI tests via Gradle:
```bash
./gradlew connectedDebugAndroidTest
```

## How to Build a Signed Release APK (Under 60 MB)

1. In Android Studio, select **Build -> Generate Signed Bundle / APK...**
2. Choose **APK** and click **Next**.
3. Select or create your keystore file (stored outside the Git repository), then enter the keystore password, key alias, and key password.
4. Select the **release** build variant and click **Create** (or **Finish**).
5. Locate the generated signed APK at `app/release/app-release.apk` and verify that its size is under the **60 MB** threshold (measured size: **12 MB**).
6. Install the release APK on a device or emulator to confirm API calls, Firebase authentication, and QR scanning work properly.
