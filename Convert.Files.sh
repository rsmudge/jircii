#!/bin/bash
#
# Convert some text files from Mac OS X format to PC format
#

perl -pi -e 's/\r//g' resources/toplevel/whatsnew.txt
perl -pi -e 's/\r//g' resources/toplevel/readme.txt
perl -pi -e 's/\r//g' resources/toplevel/license.txt
perl -pi -e 's/\r//g' readme.txt
perl -pi -e 's/\r//g' license.txt

perl -pi -e 's/\r//g' resources/resource/default.irc
perl -pi -e 's/\r//g' resources/resource/menus.irc

perl -pi -e 's/\r//g' resources/toplevel/docs/jircii.faq


perl -pi -e 's/\n/\r\n/g' resources/toplevel/whatsnew.txt
perl -pi -e 's/\n/\r\n/g' resources/toplevel/readme.txt
perl -pi -e 's/\n/\r\n/g' resources/toplevel/license.txt
perl -pi -e 's/\n/\r\n/g' readme.txt
perl -pi -e 's/\n/\r\n/g' license.txt

perl -pi -e 's/\n/\r\n/g' resources/resource/default.irc
perl -pi -e 's/\n/\r\n/g' resources/resource/menus.irc

perl -pi -e 's/\n/\r\n/g' resources/toplevel/docs/jircii.faq

