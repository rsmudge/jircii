#!/bin/bash
#
# Build and Package jIRC
# 
# If I was sharper I would make ant do it.  I hate XML though.
#
# To Run:
#   chmod +x Package.sh
#   ./Package.sh
#
# The output will be 2-3 shiny files in this directory... jerk.tgz, jerk.zip, and jerkOSX.tgz (assuming your on MacOS X)
#

#
# Get rid of temporary stuff
#
rm -rf dist/jerk.tgz dist/jerk.zip dist/jerk.dmg

rm -rf bin
rm -rf temp
mkdir  temp

#
# Build the generalized jerk.jar file
#
ant
cp -R resources/help bin/
cp -R resources/resource bin/

cd bin
jar xf ../lib/sleep.jar 
cd ..

ant

#
# Compile Mac OS X specific code (loaded via reflection so we can distro it with all platforms)
#
if [ `uname` = "Darwin" ]; then
   ant apple-specific
fi

ant jar

#
# Create general package(s) for jIRC
#
mv jerk.jar temp
cd resources/toplevel
cp -R * ../../temp/
cd ../../

cp resources/resource/default.irc temp/extra/default.irc
cp resources/resource/menus.irc temp/extra/menus.irc

#
# Create Generic jIRCii Package
#
mv temp jIRCii
rm -rf `find jIRCii -type d -name .svn`
tar zcf ./dist/jerk.tgz jIRCii

#
# Create Windows jIRCii Package
#
cp src-windows/bin/jircii.exe jIRCii/jircii.exe
rm -rf `find jIRCii -type d -name .svn`
zip -r ./dist/jerk.zip jIRCii

rm -f jIRCii/jircii.exe

mv jIRCii temp

#
# Create Mac OS X jIRCii Package
#
if [ `uname` = "Darwin" ]; then
   cp -R src-apple/jIRCii.app temp/jIRCii.app
   mv temp/jerk.jar temp/jIRCii.app/Contents/Resources/Java/jerk.jar

   cd temp
   `which SetFile` -a B jIRCii.app
   cd ..

   mv temp jIRCii
#   tar zcf ./dist/jerkOSX.tgz ./jIRCii
   rm -rf `find jIRCii -type d -name .svn`
   hdiutil create -ov -volname jIRCii -srcfolder ./jIRCii dist/jerk.dmg
   mv jIRCii temp
fi
