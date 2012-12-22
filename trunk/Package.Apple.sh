#!/bin/bash
#
# Build and Package jIRC (for Apple only)

# Get rid of temporary stuff
#
rm -rf dist/jerk.dmg

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

#cp resources/resource/default.irc temp/extra/default.irc
#cp resources/resource/menus.irc temp/extra/menus.irc

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

rm -rf temp
