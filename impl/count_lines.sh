function mycloc()
{
   local bio=`cloc --csv --quiet --match-d=/src/ --match-f=.*.java $1`
   local bio="${bio##*,}"
   echo "$bio"
}

bio=`mycloc "./bio/"`
bioc=`mycloc "./bioc/"`
bio_shared=`mycloc "./bio_shared/"`
total=$(($bio+$bioc+$bio_shared))
echo "bio: $bio"
echo "bioc: $bioc"
echo "bio_shared: $bio_shared"
echo "total: $total"
