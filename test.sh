
cd github-projects/lucene-solr
cloc_out=$(cloc ./ --include-lang=Java --csv --csv-delimiter=';' --quiet)
locmetric=(${cloc_out//;/ })
echo ${locmetric[16]} ${locmetric[17]} ${locmetric[18]}
