#!/bin/sh
echo Setting up VASSAL distribution directory "/c/temp/VASSAL"
rm -rf /c/temp/VASSAL
mkdir /c/temp/VASSAL
cp -r release-prepare/target/lib /c/temp/VASSAL
cp dist/windows/VASSAL.* /c/temp/VASSAL
cp vassal-app/src/vassal.xml /c/temp/VASSAL
cp -r release-prepare/target/doc /c/temp/VASSAL
cp VASSAL_server.bat /c/temp/VASSAL
echo Changing directories
pushd /c/temp/ 
mv VASSAL/lib/vassal-app-3.6.0-SNAPSHOT.jar VASSAL/lib/Vengine.jar
rm -f VASSAL/l4j.xml.in
echo Zipping up directory for distribution
zip -r -q VASSAL.zip VASSAL
echo Moving back to original directory
popd
echo Done


