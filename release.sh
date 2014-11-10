
git add . && git commit -m "release prepare" && git push
git checkout master
git merge develop
git add . && git commit -m "release prepare" && git push
mvn --batch-mode -s /Users/rm/Eclipse/m2/fms/settings.xml release:prepare release:perform
git checkout develop
