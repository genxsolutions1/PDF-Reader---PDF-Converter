# PDF Converter - PDF Reader

PDF Converter - PDF Reader is a simple Android app (Jetpack Compose + Kotlin) that lets you import images, edit (crop/draw/filter) them and convert multiple images into a single PDF. The app also lets you save the generated PDF to the device (Downloads) and share it with other apps.

This repository contains the app source code and utilities for combining images into PDFs, saving/sharing, and a lightweight UI flow built with Jetpack Compose.

## Key features

- Import multiple images from device gallery
- Preview images and apply edits:
  - Crop (UCrop)
  - Draw (freehand)
  - Filters (grayscale, sepia, brightness/contrast etc.)
- Combine multiple images into a single PDF (A4-like pages)
- Save generated PDF to Downloads (via MediaStore on modern Android)
- Share generated PDF using Android Sharesheet
- Recent PDFs list on Home screen (shows up to 10 most-recently created PDFs)

## Where PDFs are stored

By default PDFs are created in the app's external files directory under Documents (i.e. `getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)`) and then can be exported/saved to the system Downloads folder using the Save action.

## Build & run (Windows PowerShell)

Prerequisites:
- Java 11 or compatible JDK
- Android SDK + platform (matching compileSdk in the project)
- Android device or emulator

From the repository root run (PowerShell):

```powershell
# Build debug APK
.\gradlew.bat assembleDebug

# Install to a connected device/emulator (debug variant)
.\gradlew.bat installDebug
```

You can also open the project in Android Studio (recommended) and run on a device or emulator from the IDE.

## Development notes

- UI: Jetpack Compose with Material3
- Image cropping: UCrop
- PDF generation: Android PdfDocument API
- Sharing: Uses FileProvider; see `app/src/main/res/xml/file_paths.xml` for allowed paths

If you modify storage locations, update `file_paths.xml` and `AndroidManifest.xml` if necessary to ensure FileProvider can serve the generated files.

## Contributing

Contributions are welcome! A few quick guidelines to help contributions go smoothly:

1. Fork the repository and create a feature branch for your changes.
2. Keep commits small and focused; use clear commit messages.
3. Open a PR against the `main` branch (or the branch indicated in the repo). Include:
   - A short summary of the change
   - Screenshots or short recordings for UI changes (if applicable)
   - Any migration notes or special steps to test
4. Follow Kotlin/Android coding conventions and prefer small, testable units.

If you want to add new features (e.g., cloud upload, OCR, password-protection for PDFs), open an issue first to discuss the design.

## License

This repository does not include a license file by default. If you'd like this project to be open-source under a specific license (MIT, Apache 2.0, GPL, etc.), please add a LICENSE file or propose one in a PR.

## Getting help

- For immediate help, open an issue describing the problem and include logs/screenshots.
- If you opened a PR, mention maintainers or reviewers in the description.

---

Open source contributions are welcomed — thanks for taking a look!
