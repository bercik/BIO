for f in *.bio
do
   echo "Compiling file $f"
   add="c"
   `../impl/bioc/bioc $f -o ./compiled/$f$add`
done
