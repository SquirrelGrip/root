#!/usr/bin/env python3
import base64
import json
import os
import shlex
import subprocess
import sys
import urllib.request
import urllib.error


def section(title: str):
    print("\n==> " + title)


def need(cmd: str):
    # For ./mvnw ensure the file exists and is executable; for others rely on PATH
    if cmd.startswith("./"):
        if not (os.path.exists(cmd) and os.access(cmd, os.X_OK)):
            sys.stderr.write(f"ERROR: Required command '{cmd}' not found or not executable\n")
            sys.exit(1)
        return
    from shutil import which
    if which(cmd) is None:
        sys.stderr.write(f"ERROR: Required command '{cmd}' not found in PATH\n")
        sys.exit(1)


def run(cmd, check=True, quiet=False):
    if isinstance(cmd, str):
        cmd_list = shlex.split(cmd)
    else:
        cmd_list = cmd
    stdout = subprocess.PIPE if (quiet or check or True) else None
    stderr = subprocess.STDOUT
    proc = subprocess.run(cmd_list, stdout=stdout, stderr=stderr, text=True)
    if not quiet and proc.stdout:
        print(proc.stdout, end="")
    if check and proc.returncode != 0:
        sys.stderr.write(f"ERROR: command failed ({' '.join(cmd_list)}) with code {proc.returncode}\n")
        sys.exit(proc.returncode)
    return proc


def http_get(url: str, headers: dict | None = None) -> tuple[int, str]:
    req = urllib.request.Request(url, headers=headers or {})
    try:
        with urllib.request.urlopen(req) as resp:
            return resp.getcode(), resp.read().decode("utf-8", errors="replace")
    except urllib.error.HTTPError as e:
        return e.code, e.read().decode("utf-8", errors="replace")
    except urllib.error.URLError as e:
        sys.stderr.write(f"ERROR: HTTP request failed for {url}: {e}\n")
        return 0, ""


def resolve_version() -> str:
    section("Resolving project version")
    # Use -DforceStdout and strip trailing -SNAPSHOT only
    proc = run([
        "./mvnw", "-q", "-Dexec.skip=true", "help:evaluate",
        "-Dexpression=project.version", "-DforceStdout"
    ], check=True, quiet=True)
    version = (proc.stdout or "").strip()
    if version.endswith("-SNAPSHOT"):
        version = version[: -len("-SNAPSHOT")]
    print(f"version={version}")
    if not version:
        sys.stderr.write("ERROR: Could not resolve project.version\n")
        sys.exit(1)
    return version


def resolve_project_name() -> str:
    section("Resolving project name")
    # Use -DforceStdout and strip trailing -SNAPSHOT only
    proc = run([
        "./mvnw", "-q", "-Dexec.skip=true", "help:evaluate",
        "-Dexpression=project.artifactId", "-DforceStdout"
    ], check=True, quiet=True)
    name = (proc.stdout or "").strip()
    print(f"name={name}")
    if not name:
        sys.stderr.write("ERROR: Could not resolve project.name\n")
        sys.exit(1)
    return name


def verify_gpg_and_build():
    section("Verifying GPG signing setup by building and signing")
    cmd = [
        "./mvnw", "--batch-mode", "-s", "settings.xml", "-U", "-q",
        "package", "gpg:sign",
        "-Dgpg.keyEnvName=GPG_KEYNAME",
        "-Dgpg.passphraseEnvName=GPG_PASSPHRASE",
        "-DskipTests"
    ]
    proc = subprocess.run(cmd, text=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
    if proc.returncode != 0:
        sys.stderr.write(proc.stdout or "")
        sys.stderr.write("ERROR: GPG signing failed. Ensure GPG_KEYNAME and GPG_PASSPHRASE env vars are set and key is available.\n")
        sys.exit(1)


def github_auth_check():
    section("Checking GitHub token authentication")
    token = os.getenv("GITHUB_TOKEN") or os.getenv("GH_TOKEN") or os.getenv("GIT_TOKEN")
    if not token:
        print("WARNING: No GitHub token found (expected GITHUB_TOKEN, GH_TOKEN, or GIT_TOKEN). Skipping GitHub auth check.")
        return
    code, body = http_get("https://api.github.com/user", headers={"Authorization": f"Bearer {token}"})
    try:
        data = json.loads(body) if body else {}
    except json.JSONDecodeError:
        data = {}
    login = data.get("login")
    if code == 200 and login:
        print(f"Authenticated to GitHub as: {login}")
    else:
        sys.stderr.write("ERROR: GitHub authentication failed. Check your token.\n")
        sys.exit(1)


def ossrh_validate_and_basic_token() -> str:
    section("Validating OSSRH credentials")
    name = os.getenv("OSSRH_TOKEN_NAME")
    pwd = os.getenv("OSSRH_TOKEN_PASSWORD")
    if not name:
        sys.stderr.write("ERROR: OSSRH_TOKEN_NAME is not specified\n")
        sys.exit(1)
    if not pwd:
        sys.stderr.write("ERROR: OSSRH_TOKEN_PASSWORD is not specified\n")
        sys.exit(1)
    raw = f"{name}:{pwd}".encode("utf-8")
    token = base64.b64encode(raw).decode("ascii")
    return token


def spotless_check():
    section("Checking spotless")
    proc = subprocess.run(["./mvnw", "spotless:check"], text=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
    if proc.returncode != 0:
        sys.stderr.write(proc.stdout or "")
        sys.stderr.write("ERROR: Spotless check failed. Run './mvnw spotless:apply' to fix.\n")
        sys.exit(1)


def is_published(name: str, version: str, basic_token: str) -> bool:
    url = (
        "https://central.sonatype.com/api/v1/publisher/published"
        f"?namespace=com.github.squirrelgrip&name={name}&version={urllib.parse.quote(version)}"
    )
    code, body = http_get(url, headers={
        "accept": "application/json",
        "Authorization": f"Basic {basic_token}",
    })
    try:
        data = json.loads(body) if body else {}
    except json.JSONDecodeError:
        data = {}
    published = data.get("published")
    print(f"published={published}")
    return bool(published)


def jgitflow_release_start() -> None:
    section("Starting release via jgitflow")
    proc = subprocess.run([
        "./mvnw", "--batch-mode", "-s", "settings.xml", "-U",
        "clean", "jgitflow:release-start", "-PjgitflowStart"
    ], text=True)
    if proc.returncode != 0:
        sys.stderr.write("Build failed to prepare for release\n")
        sys.exit(1)


def jgitflow_release_finish() -> None:
    section("Finishing release via jgitflow")
    proc = subprocess.run([
        "./mvnw", "--batch-mode", "-s", "settings.xml", "-U",
        "jgitflow:release-finish"
    ], text=True)
    if proc.returncode != 0:
        sys.stderr.write("Build failed to complete release\n")
        sys.exit(1)


def post_release_repo_checks(version: str):
    section("Post-release repo checks")
    print(f"(TODO) Verify release branch removal and git tag presence for {version}")
    # Intentionally not failing or running git commands automatically here to avoid side effects


def main():
    # Basic requirements
    need("./mvnw")
    need("gpg")

    version = resolve_version()
    name = resolve_project_name()
    verify_gpg_and_build()
    github_auth_check()
    basic_token = ossrh_validate_and_basic_token()
    spotless_check()

    section(f"Checking if version {version} is already published on Central")
    if is_published(name, version, basic_token):
        sys.stderr.write("Artifact already published. You need to update the version using './mvnw versions:set'\n")
        sys.exit(1)

    # Run the release
    jgitflow_release_start()
    jgitflow_release_finish()

    section(f"Re-checking publication status for version {version}")
    if not is_published(name, version, basic_token):
        sys.stderr.write("Artifact was not published.\n")
        sys.exit(1)

    post_release_repo_checks(version)


if __name__ == "__main__":
    try:
        main()
    except KeyboardInterrupt:
        sys.stderr.write("Aborted by user.\n")
        sys.exit(130)
