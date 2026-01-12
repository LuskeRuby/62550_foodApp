This is a Kotlin Multiplatform project targeting Android, iOS.

* [/composeApp](./composeApp/src) is for code that will be shared across your Compose Multiplatform applications.
  It contains several subfolders:
    - [commonMain](./composeApp/src/commonMain/kotlin) is for code that’s common for all targets.
    - Other folders are for Kotlin code that will be compiled for only the platform indicated in the folder name.
      For example, if you want to use Apple’s CoreCrypto for the iOS part of your Kotlin app,
      the [iosMain](./composeApp/src/iosMain/kotlin) folder would be the right place for such calls.
      Similarly, if you want to edit the Desktop (JVM) specific part, the [jvmMain](./composeApp/src/jvmMain/kotlin)
      folder is the appropriate location.

* [/iosApp](./iosApp/iosApp) contains iOS applications. Even if you’re sharing your UI with Compose Multiplatform,
  you need this entry point for your iOS app. This is also where you should add SwiftUI code for your project.

### Build and Run Android Application

To build and run the development version of the Android app, use the run configuration from the run widget
in your IDE’s toolbar or build it directly from the terminal:

- on macOS/Linux
  ```shell
  ./gradlew :composeApp:assembleDebug
  ```
- on Windows
  ```shell
  .\gradlew.bat :composeApp:assembleDebug
  ```

### Running tests

This project uses Gradle/JUnit for unit tests. Tests are JVM unit tests (do not require an Android device).
Below are common workflows for running tests from PowerShell on Windows (adjust for macOS/Linux by removing the `.bat` prefix):

- Run all tests in the project:
  ```powershell
  .\gradlew.bat test
  ```

- Run only the `composeApp` module tests:
  ```powershell
  .\gradlew.bat :composeApp:test
  ```

- Run the debug unit tests for the module (common in Android projects):
  ```powershell
  .\gradlew.bat :composeApp:testDebugUnitTest
  ```

- Run a single test class or method (example uses fully-qualified class name):
  ```powershell
  .\gradlew.bat :composeApp:test --tests "com.example.a62550_foodapp.utils.ImageFileManagerTest"
  ```

- Run tests with extra logging (useful for troubleshooting):
  ```powershell
  .\gradlew.bat :composeApp:test --info
  ```

Test reports are generated under the module build folder. Example report paths (open these in a browser):

- HTML report for debug unit tests:
  `composeApp/build/reports/tests/testDebugUnitTest/index.html`
- XML/junit-style results:
  `composeApp/build/test-results/testDebugUnitTest/`

Troubleshooting notes

- If tests fail to compile, check the Gradle output for the first error; missing test dependencies are usually reported there. This repository adds JUnit as a test dependency in `composeApp/build.gradle.kts`.
- If you see warnings about Android Gradle Plugin vs compileSdk, the warnings are informational; they don't usually block unit tests but you may want to update AGP if you bump compileSdk.
- Unit tests that need Android framework APIs should be written as instrumentation or Robolectric tests; the commands above run JVM tests only.

### Build and Run iOS Application

To build and run the development version of the iOS app, use the run configuration from the run widget
in your IDE’s toolbar or open the [/iosApp](./iosApp) directory in Xcode and run it from there.

---

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)…