#!/bin/bash

realpath() {
  OURPWD=$PWD
  cd "$(dirname "$1")"
  LINK=$(readlink "$(basename "$1")")
  while [ "$LINK" ]; do
    cd "$(dirname "$LINK")"
    LINK=$(readlink "$(basename "$1")")
  done
  REALPATH="$PWD/$(basename "$1")"
  cd "$OURPWD"
  echo `dirname $REALPATH`
}

project_dir=`realpath $0`

local_maven=${HOME}/maven_rm

if [ ! -d ${local_maven} ]; then
	mkdirs -p ${local_maven}
	git clone git@github.com:rmalchow/maven.git ${local_maven}
fi

cd ${local_maven}
git pull

cd ${project_dir}

pwd

git diff --exit-code 2>&1 > /dev/null
if [ "$?" != "0" ]; then
	echo "there are unstaged changes"
	exit -1
fi

git diff --cached --exit-code  2>&1 > /dev/null
if [ "$?" != "0" ]; then
	echo "there are uncommitted changes"
	exit -2
fi



function current_version() {
	mvn org.apache.maven.plugins:maven-help-plugin:2.1.1:evaluate -Dexpression=project.version |grep "^[0-9]"
}

function current_project() {
	mvn org.apache.maven.plugins:maven-help-plugin:2.1.1:evaluate -Dexpression=project.name |grep -v "^\[" |grep -v "^Download"
}


function to_release() {
    echo "$1" | sed s:"[^0-9.]":"":g
}

# Advances the last number of the given version string by one.
function next_snapshot() {
    components=`echo "$1" | sed s:"[^0-9.]":"":g| sed s:"\.":" ":g`
    i=0
    for c in $components; do
        a[i]=$c
	let "i++"
    done
    d=${a[2]}
    let "d++"
    a[2]=$d
    echo "${a[0]}.${a[1]}.${a[2]}-SNAPSHOT"
}


#
# this is an example script. it does a basic build, deployment and tagging of 
# maven project. why not the maven deploy plugin, you ask? well ... i find it
# isn't flexible enough, and it also is quite difficult to debug.
#


cd `dirname $0`

set -e
set +x

num_args=${#@}
if [ $num_args -gt 0 ]; then
   cd $1
fi

if [ ! -f pom.xml ]; then
   echo "no POM found in folder `pwd`"
   false
fi

echo "trying to determine versions ... "

project=$(current_project)
curr=$(current_version)

echo "   project: ${project}"
echo "   curr_v : ${curr}"

rel=$(to_release $curr)
dev=$(next_snapshot $rel)

echo -n "PROJECT: $project --- $curr - $rel - $dev - OK? [y/n]: "
read check

if [ "$check" = "y" ]; then
   echo "Doing RELEASE ... "
else
   echo "aborting ... "
   false
fi

git add . && git commit -m "[ci-skip] release prepare $rel" && git push

mvn --batch-mode versions:set -DnewVersion=$rel
mvn deploy -DaltDeploymentRepository=local::default::file://${local_maven}
git tag -a $rel -m "[ci-skip] release $rel" && git push origin $rel

mvn --batch-mode versions:set -DnewVersion=$dev
git add . && git commit -m "[ci-skip] release ($rel) finished, prepare for development ... " && git push

cd ${local_maven}
git add . && git commit -m "[ci-skip] new release $rel of $project" && git push

