#!/bin/bash
# DCC post-processing script for KiliKili (multi-arch)
# Usage: ./tools/dcc-harden.sh [apk_dir]
set -e

PROJECT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
DCC_DIR="$PROJECT_DIR/tools/dcc"
DCC_PYTHON="$DCC_DIR/venv/bin/python3"
DCC_SCRIPT="$DCC_DIR/dcc.py"

# Default APK directory: release build output
APK_DIR="${1:-$PROJECT_DIR/app/build/outputs/apk/release}"
OUTPUT_DIR="$PROJECT_DIR/app/build/outputs/apk/dcc"

if [ ! -d "$APK_DIR" ]; then
    echo "Error: APK directory not found: $APK_DIR"
    echo "Usage: ./tools/dcc-harden.sh [apk_directory]"
    exit 1
fi

if [ ! -f "$DCC_PYTHON" ]; then
    echo "Error: DCC Python not found at $DCC_PYTHON"
    echo "Run: cd tools/dcc && python3 -m venv venv && venv/bin/pip install -r requirements.txt"
    exit 1
fi

mkdir -p "$OUTPUT_DIR"

echo "=== KiliKili DCC Hardening Script ==="
echo "APK source: $APK_DIR"
echo "Output dir: $OUTPUT_DIR"
echo ""

count=0
for apk in "$APK_DIR"/*.apk; do
    [ -f "$apk" ] || continue
    name=$(basename "$apk" .apk)
    echo "[$(( count + 1 ))] Processing: $name.apk ..."
    
    cd "$DCC_DIR"
    "$DCC_PYTHON" "$DCC_SCRIPT" "$apk" -o "$OUTPUT_DIR/${name}-hardened.apk"
    
    echo "    -> $OUTPUT_DIR/${name}-hardened.apk"
    count=$(( count + 1 ))
done

echo ""
if [ $count -eq 0 ]; then
    echo "WARNING: No APK files found in $APK_DIR"
    echo "Build release APK first: ./gradlew assembleRelease"
    exit 1
fi

echo "=== DCC hardening complete ==="
echo "Processed $count APK(s). Outputs:"
ls -lh "$OUTPUT_DIR"/*.apk 2>/dev/null || echo "(no output files)"
