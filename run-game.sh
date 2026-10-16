#!/usr/bin/env bash
# Realm Stitchers — Cross-Platform Local Launcher
set -e

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
TARGET="$DIR/src/main/resources/static/index.html"

echo "========================================================"
echo "  Launching Realm Stitchers — Cross-Reality Multiplayer"
echo "========================================================"

if command -v xdg-open > /dev/null 2>&1; then
    xdg-open "$TARGET"
elif command -v open > /dev/null 2>&1; then
    open "$TARGET"
else
    echo "Please open $TARGET in your web browser."
fi

echo "Tip: Open in two tabs to auto-pair via WebRTC!"
echo "========================================================"
