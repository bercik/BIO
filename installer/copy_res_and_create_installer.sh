mkdir -p ./res/doc
cp ../doc/modules/html/index.html ./res/doc/
cp ../doc/modules/html/index.css ./res/doc/

rsync -avm --include='*.bio' -f 'hide,! */' ../examples ./res

cp -r ../vim_plugin ./res/

mkdir -p ./res/src
cp -r ../impl/bio/src ./res/src/bio
cp -r ../impl/bioc/src ./res/src/bioc
cp -r ../impl/bio_shared/src ./res/src/bio_shared

mkdir -p ./res/bin
cp ../impl/bio/dist/bio.jar ./res/bin/
cp ../impl/bioc/dist/bioc.jar ./res/bin/

cp -r ../impl/bio/dist/lib ./res/bin/
cp -r ../impl/bioc/dist/lib ./res/bin/

~/IzPack/bin/compile install.xml -b . -o bio_installer.jar
