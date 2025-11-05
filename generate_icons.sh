#!/bin/bash

# Script to generate app icon WebP files from vector drawable
# This script requires Android SDK build tools

echo "Generating app launcher icons from vector drawable..."

# Define the sizes for different density folders
declare -A DENSITIES=(
    ["mdpi"]="48"
    ["hdpi"]="72"
    ["xhdpi"]="96"
    ["xxhdpi"]="144"
    ["xxxhdpi"]="192"
)

# Input vector drawable file
VECTOR_FILE="app/src/main/res/drawable/ic_launcher_legacy.xml"
OUTPUT_BASE="app/src/main/res/mipmap-"

# Generate for each density
for density in "${!DENSITIES[@]}"; do
    size=${DENSITIES[$density]}
    output_dir="${OUTPUT_BASE}${density}"
    
    echo "Generating ${size}x${size} icon for ${density}..."
    
    # Convert vector to PNG first (requires aapt2 or similar tool)
    # You would need to use Android Studio's vector asset tool or similar
    echo "  Target: ${output_dir}/ic_launcher.webp (${size}x${size})"
    echo "  Target: ${output_dir}/ic_launcher_round.webp (${size}x${size})"
done

echo ""
echo "Manual steps required:"
echo "1. Open Android Studio"
echo "2. Right-click on app/src/main/res"
echo "3. Select New > Image Asset"
echo "4. Choose 'Launcher Icons (Adaptive and Legacy)'"
echo "5. Set Source Asset to the vector drawable: ic_launcher_legacy.xml"
echo "6. Configure the icon and generate"
echo ""
echo "Alternatively, you can:"
echo "1. Use the provided vector drawables with adaptive icons (recommended)"
echo "2. The adaptive icons will work on Android 8.0+ devices"
echo "3. For older devices, manually create WebP files from ic_launcher_legacy.xml"