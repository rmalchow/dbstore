echo release version
read RV
echo development version
read DV
git add . && git commit -m "release prepare" && git push
git checkout master
#mvn versions:set -DgenerateBackupPoms=false
git merge develop
git add . && git commit -m "release prepare" && git push
mvn --batch-mode -Dtag=dbstore-$RV release:prepare -DreleaseVersion=$RV -DdevelopmentVersion=$DV -s /Users/rm/Eclipse/m2/fms/settings.xml 
mvn --batch-mode -s /Users/rm/Eclipse/m2/fms/settings.xml release:perform
git checkout develop
git merge master

