bin=$1/bin/
lib=$1/lib/
def_path=/usr/local/bin/
bio=bio
bioc=bioc
logs=logs

mkdir $bin$logs

chown -R $SUDO_USER $1
chgrp -R $SUDO_USER $1

echo "#!/bin/bash
java -jar $bin$bio.jar \$*" > $def_path$bio
chmod 755 $def_path$bio

echo "#!/bin/bash
java -jar -Dlibpath=$lib $bin$bioc.jar \$*" > $def_path$bioc
chmod 755 $def_path$bioc
