function mycloc()
{
   local bio=`cloc --csv --quiet --match-d=/src/ --match-f=.*.java $1`
   local bio="${bio##*,}"
   echo "$bio"
}

bio=`mycloc "./bio/"`
bio_modules=`mycloc "./bio/src/pl/rcebula/module/modules/"`
bioc=`mycloc "./bioc/"`
bio_shared=`mycloc "./bio_shared/"`
total=$(($bio+$bioc+$bio_shared))
echo "bio: $bio"
echo "  modules: $bio_modules"
echo "bioc: $bioc"
echo "bio_shared: $bio_shared"
echo "total: $total"
