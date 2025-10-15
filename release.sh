#!/usr/bin/env bash
set -euo pipefail

# Basic requirements check
need() { command -v "$1" >/dev/null 2>&1 || { echo "ERROR: Required command '$1' not found in PATH" >&2; exit 1; }; }
need ./mvnw
need curl
need jq
need gpg

# Helper to print section headers
section() { echo; echo "==> $*"; }

# Get the current version of the artifact (strip -SNAPSHOT only at the end)
section "Resolving project version"
VERSION=$(./mvnw -q -Dexec.skip=true help:evaluate -Dexpression=project.version -DforceStdout | sed 's/-SNAPSHOT$//')
echo "version=${VERSION}"
if [ -z "${VERSION}" ]; then
  echo "ERROR: Could not resolve project.version" >&2
  exit 1
fi

# Check if gpg is configured correctly (will try to sign built artifacts)
section "Verifying GPG signing setup by building and signing"
./mvnw --batch-mode -s settings.xml -U -q package gpg:sign -Dgpg.keyEnvName=GPG_KEYNAME -Dgpg.passphraseEnvName=GPG_PASSPHRASE -DskipTests || {
  echo "ERROR: GPG signing failed. Ensure GPG_KEYNAME and GPG_PASSPHRASE env vars are set and key is available." >&2
  exit 1
}

# Resolve GitHub token from common env var names
GITHUB_TOKEN_COMBINED=${GITHUB_TOKEN:-${GH_TOKEN:-${GIT_TOKEN:-}}}
section "Checking GitHub token authentication"
if [ -z "${GITHUB_TOKEN_COMBINED}" ]; then
  echo "WARNING: No GitHub token found (expected GITHUB_TOKEN, GH_TOKEN, or GIT_TOKEN). Skipping GitHub auth check."
else
  GHLOGIN=$(curl -s -H "Authorization: Bearer ${GITHUB_TOKEN_COMBINED}" https://api.github.com/user | jq -r '.login // empty')
  if [ -n "${GHLOGIN}" ]; then
    echo "Authenticated to GitHub as: ${GHLOGIN}"
  else
    echo "ERROR: GitHub authentication failed. Check your token." >&2
    exit 1
  fi
fi

# Check if the OSSRH token is configured correctly
section "Validating OSSRH credentials"
if [ -z "${OSSRH_TOKEN_NAME:-}" ]; then
    echo "ERROR: OSSRH_TOKEN_NAME is not specified" >&2; exit 1
fi
if [ -z "${OSSRH_TOKEN_PASSWORD:-}" ]; then
    echo "ERROR: OSSRH_TOKEN_PASSWORD is not specified" >&2; exit 1
fi
BASIC_TOKEN=$(printf '%s' "${OSSRH_TOKEN_NAME}:${OSSRH_TOKEN_PASSWORD}" | base64 | tr -d '\n')

# SpotlessCheck
section "Checking spotless"
./mvnw spotless:check || { echo "ERROR: Spotless check failed. Run './mvnw spotless:apply' to fix." >&2; exit 1; };

# Check if the current version is already deployed
section "Checking if version ${VERSION} is already published on Central"
PUBLISHED=$(curl -s "https://central.sonatype.com/api/v1/publisher/published?namespace=com.github.squirrelgrip&name=root&version=${VERSION}" \
  -H "accept: application/json" -H "Authorization: Basic ${BASIC_TOKEN}" | jq -r '.published')
echo "published=${PUBLISHED}"
if [ PUBLISHED == 'true' ]; then
    echo "Artifact already published. You need to update the version using './mvnw versions:set'"
    exit 1
fi

# Start the Release (intentionally commented; run manually once checks pass)
section "Starting release via jgitflow"
./mvnw --batch-mode -s settings.xml -U clean jgitflow:release-start -PjgitflowStart
if [ $? != 0 ]; then
    echo "Artifact failed to prepare for release"
    exit 1
fi

# Finish the Release (intentionally commented; run manually once checks pass)
section "Finishing release via jgitflow"
./mvnw --batch-mode -s settings.xml -U jgitflow:release-finish
if [ $? != 0 ]; then
    echo "Artifact failed to complete release"
    exit 1
fi

# Check if the artifact is deployed successfully (repeat published check)
section "Re-checking publication status for version ${VERSION}"
PUBLISHED=$(curl -s "https://central.sonatype.com/api/v1/publisher/published?namespace=com.github.squirrelgrip&name=root&version=${VERSION}" \
  -H "accept: application/json" -H "Authorization: Basic ${BASIC_TOKEN}" | jq -r '.published // false')
if [ PUBLISHED == 'true' ]; then
    echo "Artifact was not published."
    exit 1
fi

# Placeholders for additional repo checks
section "Post-release repo checks"
echo "(TODO) Verify release branch removal and git tag presence for ${VERSION}"
git rev-parse --verify "release/$VERSION" && echo "Branch release/$VERSION still exists."; exit 1;
