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

## Build & run (Windows PowerShell)

Prerequisites:
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

We welcome contributions from the community! Whether you're fixing bugs, adding features, or improving documentation, your help is appreciated.

How to Contribute

1. Fork the repository
   - Click the "Fork" button at the top right of this page

2. Clone your fork

```powershell
git clone https://github.com/YOUR-USERNAME/PDF-Reader---PDF-Converter.git
cd PDF-Reader---PDF-Converter
```

3. Create a feature branch

```powershell
git checkout -b feature/amazing-feature
```

4. Make your changes

- Write clean, readable code
- Follow Kotlin coding conventions
- Add comments where necessary
- Test your changes thoroughly

5. Commit your changes

```powershell
git add .
git commit -m "Add: amazing feature description"
```

6. Push to your fork

```powershell
git push origin feature/amazing-feature
```

7. Open a Pull Request

- Go to the original repository
- Click "New Pull Request"
- Select your fork and branch
- Describe your changes clearly

Contribution Guidelines

Code Style

- Follow Kotlin Coding Conventions
- Use meaningful variable and function names
- Keep functions small and focused
- Add KDoc comments for public APIs

Commit Messages

Use clear, descriptive commit messages:

- Add: new feature description
- Fix: bug description
- Update: improvement description
- Docs: documentation changes
- Refactor: code restructuring

Pull Request Process

- Update documentation if needed
- Ensure all tests pass
- Add screenshots for UI changes
- Link related issues in the PR description
- Wait for code review and address feedback

Areas for Contribution

We're especially interested in contributions for:

- 🎨 UI/UX Improvements - Enhanced animations, themes, accessibility
- 📝 Documentation - Tutorials, code examples, translations
- 🐛 Bug Fixes - Report and fix issues you encounter
- ⚡ Performance - Optimization and efficiency improvements
- 🧪 Testing - Unit tests, integration tests, UI tests

Reporting Issues

Found a bug or have a feature request?

1. Check if the issue already exists
2. Create a new issue
3. Provide a clear description and steps to reproduce
4. Include device info and Android version
5. Add screenshots if applicable

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Authors

GenX Solutions

GitHub: @genxsolutions1

## Acknowledgments

Thanks to all contributors who help improve this project

Inspired by the love of mobile development

Built with ❤️ using Jetpack Compose

## Support

Need help or have questions?

- Email: support@genxsolutions.com
- Issues: GitHub Issues
- Discussions: GitHub Discussions

## Show Your Support

If you like this project, please consider:

- ⭐ Starring the repository
- 🍴 Forking and contributing
- 📢 Sharing with friends
- 💖 Sponsoring the project

Made with ❤️ by GenX Solutions

⬆ Back to Top
