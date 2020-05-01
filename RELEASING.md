# Prepare CI
```
travis login --com --github-token $GIT_TOKEN
gpg --pinentry-mode loopback --export-secret-keys --batch --passphrase $GPG_PASSPHRASE > travis/all.gpg
travis encrypt-file travis/all.gpg travis/all.gpg.enc --add --com -f --no-interactive
rm travis/all.gpg
travis env set OSSRH_TOKEN_NAME $OSSRH_TOKEN_NAME --com
travis env set OSSRH_TOKEN_PASSWORD $OSSRH_TOKEN_PASSWORD --com
travis env set GIT_USER $GIT_USER --com
travis env set GIT_TOKEN $GIT_TOKEN --com
travis env set GPG_KEYNAME $GPG_KEYNAME --com
travis env set GPG_PASSPHRASE $GPG_PASSPHRASE --com

git add travis/all.gpg.enc
git commit -m "Added travis/all.gpg.enc"
```

# Releasing
```
travis login --com --github-token $GIT_TOKEN
export SLUG=SquirrelGrip%2Fextensions
export TRAVIS_TOKEN=`travis token --no-interactive --com`
export BODY='{
    "request": {
        "message": "Starting a release",
        "branch":"develop",
        "config": {
            "env": {
                "jobs": [
                    "RELEASE=true"
                ]
            }
        }
    }
}'

curl -s -X POST \
   -H "Content-Type: application/json" \
   -H "Accept: application/json" \
   -H "Travis-API-Version: 3" \
   -H "Authorization: token $TRAVIS_TOKEN" \
   -d "$BODY" \
   https://api.travis-ci.com/repo/$SLUG/requests
```