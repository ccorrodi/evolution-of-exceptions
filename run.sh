#!/bin/bash

if [ -z $CLOC ]; then
    CLOC=cloc
fi

if [ -z $JAVA ]; then
    JAVA=java
fi

if [ -z $LOCAL_PROJECTS ]; then
    LOCAL_PROJECTS=$PWD/local_projects
fi

if [ -z $WORKDIR]; then
    WORKDIR=$PWD/workdir
fi

if [ -z $INTERVAL_MONTHS]; then
    INTERVAL_MONTHS=3
fi

repo="$1"
home_dir=`pwd`

mkdir -p $WORKDIR
cd $WORKDIR

foldername=$repo

# copy from local projects directory
cp -r $LOCAL_PROJECTS/$foldername .

cd $WORKDIR/$foldername

# get hash of current HEAD, the url of the repository,
# and a list of 'timestamp:commit_hash'
commit_hash=$(git rev-parse HEAD)
repo_url=$(git config --get remote.origin.url)
commits=$(git log --format=%ct:%H)

echo "----"
##set -f
initial=true
last_checkout=0

# iterate over all commits
while read -r line; do

    # split the line into a 2-element array
	current=(${line//:/ })

	# fill the database if we are either at the most recent commit (i.e., the commit that
	# is checked out after the clone) or at least $INTERVAL_MONTHS before the commit that
	# was checked out in the last iteration
	if $initial || ((${current[0]}<(last_checkout - 60*60*24*31*$INTERVAL_MONTHS))) ; then
		initial=false
    	cd $WORKDIR/$foldername
		git checkout ${current[1]}

		# use cloc for getting Java stats, put it in an array
		cloc_out=$($CLOC ./ --include-lang=Java --csv --csv-delimiter=';' --quiet | tail -1)
		locmetric=(${cloc_out//;/ })

		# run the database tool with the arguments taken from the locmetric array; the parameters
		# correspond to the following:
		# path timestamp commithash foldername blanklines commentlines codelines
		cd $home_dir

		path=$WORKDIR/$foldername
		timestamp=${current[0]}
		commithash=${current[1]}
		foldername=$foldername
		blanklines=${locmetric[2]}
		commentlines=${locmetric[3]}
		codelines=${locmetric[4]}

        #echo path: $path
        #echo timestamp: $timestamp
        #echo hash: $commithash
        #echo folder: $foldername
        #echo blank: $blanklines
        #echo comment: $commentlines
        #echo codelines: $codelines

	    $JAVA -jar target/scg-seminar-exceptions-0.0.1-SNAPSHOT-jar-with-dependencies.jar "$path" $timestamp $commithash $foldername $blanklines $commentlines $codelines

		last_checkout=(${line//:/ })
	fi
done <<< "$commits"

cd $home_dir