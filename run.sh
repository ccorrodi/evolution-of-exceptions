#!/bin/bash

if [ -z $CLOC ]; then
    CLOC=cloc
fi

if [ -z $JAVA ]; then
    JAVA=java
fi

repo_list="$1"
home_dir=`pwd`
interval_months=3

cd github-projects

# read from repo list (see line at end of while)
while read current_repo; do
	echo $current_repo

    # clone in github-projects/
    # TODO: do not clone, but checkout main branch (not always master!)
	cd $home_dir/github-projects
	git clone $current_repo

    # get project name
	basename=$(basename $current_repo)
	foldername=$basename
	#foldername=${basename%.*} # what? how's that different from basename?

	cd $home_dir/github-projects/$foldername

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
		# is checked out after the clone) or at least $interval_months before the commit that
		# was checked out in the last iteration
		if $initial || ((${current[0]}<(last_checkout - 60*60*24*31*$interval_months))) ; then
			initial=false

			cd $home_dir/github-projects/$foldername
			git checkout ${current[1]}

			# use cloc for getting Java stats, put it in an array
			cloc_out=$($CLOC ./ --include-lang=Java --csv --csv-delimiter=';' --quiet | tail -1)
			locmetric=(${cloc_out//;/ })

			# run the database tool with the arguments taken from the locmetric array; the parameters
			# correspond to the following:
			# path timestamp commithash foldername blanklines commentlines codelines
			cd $home_dir

			path=$home_dir/github-projects/$foldername
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

done < "$home_dir/$repo_list"

cd $home_dir
