#!/bin/bash
# deploy-arkanoid.sh — Download latest Arkanoid web build from GitHub and deploy
# Usage: ./deploy-arkanoid.sh [target_dir]
# Default target: ~/www/kuba/gm005

set -e

REPO="k0fis/arkanid"
TARGET="${1:-$HOME/www/kuba/gm005}"
TMP=$(mktemp -d)

echo "Fetching latest release from github.com/$REPO ..."
URL=$(curl -s "https://api.github.com/repos/$REPO/releases/latest" \
  | grep -o '"browser_download_url": *"[^"]*"' \
  | head -1 \
  | cut -d'"' -f4)

if [ -z "$URL" ]; then
  echo "ERROR: No release found."
  rm -rf "$TMP"
  exit 1
fi

echo "Downloading: $URL"
curl -sL "$URL" -o "$TMP/arkanoid-web.tar.gz"

echo "Deploying to: $TARGET"
rm -rf "$TARGET"
mkdir -p "$TARGET"
tar -xzf "$TMP/arkanoid-web.tar.gz" -C "$TARGET"

rm -rf "$TMP"
echo "Done. Deployed to $TARGET"
