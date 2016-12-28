#!/bin/bash

jobs=$1
repo_list=$2

cat $repo_list | xargs -0 -d '\n' -n 1 -P $jobs ./run.sh