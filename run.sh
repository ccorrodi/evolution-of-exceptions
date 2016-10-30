#!/bin/bash

repo_list="$1"
home_dir=`pwd`
interval_months=3

cd github-projects

while read current_repo; do
	echo $current_repo

	git clone $current_repo

	basename=$(basename $current_repo)
	foldername=${basename%.*}
	cd $home_dir/github-projects/$foldername

	commit_hash=$(git rev-parse HEAD)
	repo_url=$(git config --get remote.origin.url)
	commits=$(git log --format=%at:%H)

	echo "----"
	##set -f

	initial=true
	last_checkout=0
	while read -r line; do
		current=(${line//:/ })
		if $initial || ((${current[0]}<(last_checkout - 60*60*24*31*$interval_months))) ; then
			initial=false
			cd $home_dir/github-projects/$foldername
			git checkout ${last_checkout[1]}
			cd $home_dir
		 	java -jar target/scg-seminar-exceptions-0.0.1-SNAPSHOT-jar-with-dependencies.jar $home_dir/github-projects/$foldername ${last_checkout[0]} ${last_checkout[1]} $foldername
			#run index java
			last_checkout=(${line//:/ })
		fi
	done <<< "$commits"

	echo "..."

	#echo $(git log --format=%at:%H --before=1265635806)



	#git log --reverse
	#git rev-list --max-parents=0 HEAD

done < "$home_dir/$repo_list"


cd $home_dir
