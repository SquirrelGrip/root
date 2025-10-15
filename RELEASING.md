# Pre Release Steps
- Get current version
```
VERSION=./mvnw help:evaluate -Dexpression=project.version | grep -v '\[INFO\]' | sed 's/-SNAPSHOT//g'
```

- Confirm GPG is configured
```
./mvnw --batch-mode -s settings.xml -U package gpg:sign -Dgpg.keyEnvName=GPG_KEYNAME -Dgpg.passphraseEnvName=GPG_PASSPHRASE
```

- Confirm GITHUB token is valid
```
curl -H "Authorization: token $GIT_TOKEN" https://api.github.com/user
```
- Confirm OSS token is valid
```
BEARER_TOKEN=$(printf "$OSSRG_TOKEN_NAME:$OSSRH_TOKEN_PASSWORD"| base64)
curl -X 'GET' \
  'https://central.sonatype.com/api/v1/publisher/published?namespace=com.github.squirrelgrip&name=root&version=$VERSION' \
  -H 'accept: application/json' \
  -H 'Authorization: Basic $BEARER_TOKEN'
```

# Manual Release
```
./mvnw --batch-mode -s settings.xml -U clean jgitflow:release-start -PjgitflowStart && ./mvnw --batch-mode -s settings.xml -U jgitflow:release-finish
```

# Cleaning a Failed Deploy
There are situations where the artifact has been deployed, however the changes have not been merged into develop and master branches. To achieve the branch consistency the follow commands are required...
```
./mvnw --batch-mode -U clean jgitflow:release-finish -DnoDeploy=true -DskipPublishing=true
```

# Verify Deployment
```
curl -H 'accept: application/json' -H 'Authorization: Basic cXF5N1FtOm5uTHByMFVMNlJSM2JPVWs3SFdmYllVRXNZS3BPU0JTMA==' 'https://central.sonatype.com/api/v1/publisher/published?namespace=com.github.squirrelgrip&name=root&version=$VERSION'
```
# Getting a new GitHub token (Personal Access Token)
If you need to authenticate Git operations during a release (push branches/tags, trigger workflows, or access GitHub Packages), create a new GitHub Personal Access Token (PAT). As of 2025, GitHub recommends Fine-grained PATs.

## Option A: Fine-grained PAT (recommended)
- Navigate: GitHub → your avatar (top-right) → Settings → Developer settings → Personal access tokens → Fine-grained tokens → Generate new token.
- Name the token and set an expiration (shorter is safer; diarize renewal).
- Resource owner: select your user (or the organization) and then restrict repositories to only what you need.
- Permissions (minimum for typical release tasks in this repo):
    - Repository permissions:
        - Contents: Read and write (required to push commits/tags)
        - Metadata: Read (default)
        - Pull requests: Read and write (if your flow opens PRs)
        - Workflows: Read and write (if you need to re-run/trigger workflows)
    - Packages (only if using GitHub Packages):
        - Packages: Read and write
- Generate and copy the token. You will not be able to see it again.

## Option B: Classic PAT (legacy)
- Navigate: GitHub → Settings → Developer settings → Personal access tokens → Tokens (classic) → Generate new token (classic).
- Scopes commonly needed:
    - repo (full)
    - workflow (if you need to manage Actions/workflows)
    - write:packages and read:packages (only if using GitHub Packages)
- Prefer Fine-grained PATs when possible.

## Using the token locally
- Git over HTTPS:
    - Ensure your remote uses HTTPS: `git remote set-url origin https://github.com/<owner>/<repo>.git`
    - When prompted for username/password:
        - Username: your GitHub username
        - Password: the PAT value
    - To cache on macOS with Keychain: `git config --global credential.helper osxkeychain`
    - To cache on Linux: `git config --global credential.helper cache` (or use a secret manager)
- GitHub CLI (gh):
    - `gh auth login` (interactive) or set `GH_TOKEN`/`GITHUB_TOKEN` env var
    - Verify: `gh auth status`
- Environment variable (temporary shell session):
    - `export GITHUB_TOKEN=<paste-token>`

## Good practices
- Treat tokens like passwords. Store them in a password manager.
- Set the shortest practical expiration and rotate regularly.
- Revoke immediately if exposed: GitHub → Settings → Developer settings → Personal access tokens → Find token → Revoke.
- Update any CI secrets or local credential stores when you rotate tokens.