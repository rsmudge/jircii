#!/bin/bash
#
# Package jIRCii Source
# 
# If I was sharper I would make ant do it.  I hate XML though.
#
# To Run:
#   chmod +x Package.Source.sh
#   ./Package.Source.sh

rm -rf temp
rm -rf rsmudge_irc
mkdir rsmudge_irc

tar cf rsmudge_irc/src.tar .
cd rsmudge_irc
tar xf src.tar
rm -f src.tar

rm -f jerk.tgz jerk.zip jerkOSX.tgz rsmudge_irc.tgz jerk.jar
rm -rf temp
rm -rf rsmudge_irc
rm -rf upload
rm -rf jIRCii

rm -rf docs/api/
rm -rf bin
rm -rf lib/sleep/

cd ..
tar zcf rsmudge_irc.tgz rsmudge_irc

