#!/usr/bin/env bash
# Apply one semver bump to version.properties.
# Usage: scripts/bump-version.sh patch|minor|major [path/to/version.properties]
#   patch  0.1.0 -> 0.1.1
#   minor  0.1.0 -> 0.2.0
#   major  0.1.0 -> 1.0.0
# VERSION_CODE always increments by 1.
# Does not use floating point.
set -euo pipefail

KIND="${1:-}"
FILE="${2:-version.properties}"

case "$KIND" in
  patch|minor|major) ;;
  *)
    echo "usage: $0 patch|minor|major [version.properties]" >&2
    exit 2
    ;;
esac

if [[ ! -f "$FILE" ]]; then
  echo "missing $FILE" >&2
  exit 1
fi

NAME=$(grep -E '^VERSION_NAME=' "$FILE" | head -n1 | cut -d= -f2- | tr -d '[:space:]')
CODE=$(grep -E '^VERSION_CODE=' "$FILE" | head -n1 | cut -d= -f2- | tr -d '[:space:]')

if [[ ! "$NAME" =~ ^[0-9]+(\.[0-9]+){0,2}$ ]]; then
  echo "VERSION_NAME must be semver MAJOR.MINOR.PATCH (got '$NAME')" >&2
  exit 1
fi
if [[ ! "$CODE" =~ ^[0-9]+$ ]]; then
  echo "VERSION_CODE must be an integer (got '$CODE')" >&2
  exit 1
fi

IFS='.' read -r MAJOR MINOR PATCH <<<"$NAME"
MINOR="${MINOR:-0}"
PATCH="${PATCH:-0}"

case "$KIND" in
  patch)
    PATCH=$((PATCH + 1))
    ;;
  minor)
    MINOR=$((MINOR + 1))
    PATCH=0
    ;;
  major)
    MAJOR=$((MAJOR + 1))
    MINOR=0
    PATCH=0
    ;;
esac

NEW_NAME="${MAJOR}.${MINOR}.${PATCH}"
NEW_CODE=$((CODE + 1))

tmp=$(mktemp)
printf 'VERSION_NAME=%s\nVERSION_CODE=%s\n' "$NEW_NAME" "$NEW_CODE" >"$tmp"
mv "$tmp" "$FILE"
echo "bumped ${KIND}: ${NAME} (${CODE}) -> ${NEW_NAME} (${NEW_CODE})"
