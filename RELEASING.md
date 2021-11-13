# Manual Release
```
./mvnw --batch-mode -s settings.xml -U clean jgitflow:release-start -PgitflowStart && ./mvnw --batch-mode -s settings.xml -U jgitflow:release-finish
```

# Cleaning a Failed Deploy
There are situations where the artifact has been deployed, however the changes have not been merged into devloper and master branches. To achieve the branch consistency the follow commands are required...
```
mvn --batch-mode -U clean jgitflow:release-finish -DnoDeploy=true
```