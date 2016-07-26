bin=$1/bin/
def_path=/usr/local/bin/
bio=bio
bioc=bioc
logs=logs

echo "#!/bin/bash
java -jar $bin$bio.jar \$*" > $def_path$bio
chmod 755 $def_path$bio

echo "#!/bin/bash
java -jar $bin$bioc.jar \$*" > $def_path$bioc
chmod 755 $def_path$bioc

mkdir $bin$logs
chmod 777 $bin$logs
