#!/usr/bin/env bash
# Decide a semver bump from a pull request diff. Prints exactly one word:
#   patch | minor | major
#
# This is the deterministic classifier used by CI when OpenCode MuseSpark
# is not authenticated on the runner. The workflow tries the model first
# (prompt: "Reply with exactly one word: patch, minor, or major") and falls
# back here.
#
# Usage:
#   PR_TITLE="..." PR_BODY="..." scripts/decide-bump.sh [unified.diff]
# Diff is read from the file argument, or from stdin.
#
# Rules (first match wins):
#   major — breaking: title/body says breaking or major; applicationId value
#           changes; a Room migration drops data (DROP TABLE /
#           fallbackToDestructiveMigration); a public screen or nav graph
#           file is deleted.
#   minor — a feature: title is feat/feature; a new *Screen.kt (or new file
#           under ui/) is added.
#   patch — fixes, chores, docs, CI, and any other small tweak.
set -euo pipefail

if [[ "${1:-}" == "-h" || "${1:-}" == "--help" ]]; then
  sed -n '2,20p' "$0"
  exit 0
fi

if [[ $# -ge 1 ]]; then
  if [[ ! -f "$1" ]]; then
    echo "diff file not found: $1" >&2
    exit 1
  fi
  DIFF=$(cat "$1")
else
  DIFF=$(cat || true)
fi

TITLE="${PR_TITLE:-}"
BODY="${PR_BODY:-}"

# Added and removed lines only (ignore +++ / --- headers).
CHANGED=$(printf '%s\n' "$DIFF" | grep -E '^[+-]' | grep -Ev '^[+-]{3} ' || true)

is_major=0

if printf '%s\n%s\n' "$TITLE" "$BODY" | grep -Eiq '(^|[^[:alnum:]_])(breaking[[:space:]]*change|breaking!|breaking:|\[breaking\]|\[major\]|^major:|major:)'; then
  is_major=1
fi
# Conventional commit breaking marker in the title, e.g. "feat!:" or "fix(app)!:"
if printf '%s\n' "$TITLE" | grep -Eq '!:'; then
  is_major=1
fi

plus_id=$(printf '%s\n' "$CHANGED" | grep -E '^\+.*applicationId[[:space:]]*=' || true)
minus_id=$(printf '%s\n' "$CHANGED" | grep -E '^-.*applicationId[[:space:]]*=' || true)
if [[ -n "$plus_id" && -n "$minus_id" ]]; then
  norm_plus=$(printf '%s\n' "$plus_id" | sed 's/^[+]//' | tr -d '[:space:]')
  norm_minus=$(printf '%s\n' "$minus_id" | sed 's/^[-]//' | tr -d '[:space:]')
  if [[ "$norm_plus" != "$norm_minus" ]]; then
    is_major=1
  fi
fi

if printf '%s\n' "$CHANGED" | grep -Eiq 'DROP[[:space:]]+TABLE|fallbackToDestructiveMigration'; then
  is_major=1
fi

deleted_files=$(printf '%s\n' "$DIFF" | awk '
  /^diff --git / {
    file=$4
    sub(/^b\//, "", file)
    cur=file
  }
  /^deleted file mode/ { if (cur != "") print cur }
')
if printf '%s\n' "$deleted_files" | grep -Eiq '(Screen|NavGraph|navigation).*\.kt$|ui/.+\.kt$'; then
  is_major=1
fi

if [[ "$is_major" -eq 1 ]]; then
  echo major
  exit 0
fi

is_minor=0
if printf '%s\n' "$TITLE" | grep -Eiq '^(feat|feature)(\(|:|[[:space:]])|\[feat\]|\[feature\]'; then
  is_minor=1
fi

new_files=$(printf '%s\n' "$DIFF" | awk '
  /^diff --git / {
    file=$4
    sub(/^b\//, "", file)
    cur=file
  }
  /^new file mode/ { if (cur != "") print cur }
')
if printf '%s\n' "$new_files" | grep -Eiq 'Screen\.kt$|^app/.*/ui/.+\.kt$'; then
  is_minor=1
fi

if [[ "$is_minor" -eq 1 ]]; then
  echo minor
  exit 0
fi

echo patch
