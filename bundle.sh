#!/bin/sh
echo Setting up VASSAL distribution directory "./temp/VASSAL"
rm -rf ./temp/VASSAL
mkdir -p ./temp/VASSAL/Contents/Resources
cp -r release-prepare/target/lib ./temp/VASSAL
cp dist/windows/VASSAL* ./temp/VASSAL
cp vassal-app/src/vassal.xml ./temp/VASSAL
cp -r release-prepare/target/doc ./temp/VASSAL/Contents/Resources
echo Changing directories
pushd ./temp/ 
mv VASSAL/lib/vassal-app-3.7.12.jar VASSAL/lib/Vengine.jar
mv VASSAL/lib/vassal-agent-3.7.12.jar VASSAL/lib/vassal-agent.jar
rm -f VASSAL/VASSAL.l4j.xml.in
echo Zipping up directory for distribution
zip -r -q VASSAL.zip VASSAL
echo Moving back to original directory
popd
echo Done
