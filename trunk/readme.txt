This is just a short, sweet, and simple readme for the actual source code.

This code should be reasonably stable.

If you need to contact me, my name is Raphael, I wrote most of this code. 
My email address is rsmudge@gmail.com

The official jIRCii homepage is at http://www.oldschoolirc.com/

You will need Apache Ant to compile my source code.  I use version 1.5.1.
Ant is easy to install and is available at http://ant.apache.org

I'm a big fan of ant.  Obviously I'm not using it to its maximum potential
as I'm lazy.  It is a great tool.

To compile jIRCii: from the top level of the extracted files type:

[raffi@beardsley ~/rero]$ ant

To run jIRCii from the command line... 

[raffi@beardsley ~/rero]$ java -classpath ./bin:./lib/sleep.jar:./resources rero.test.WindowTest

(use the -settings command line option to specify a directory to dump your test settings to)

To Package jIRCii

[raffi@beardsley ~/rero]$ chmod +x Package.sh
[raffi@beardsley ~/rero]$ ./Package.sh

To Build JavaDoc for jIRCii (dumped to the docs/api directory)

[raffi@beardsley ~/rero]$ ant docs

To be safe:
This code is (c) 2004-2011 Raphael Mudge.  We can go back as far as 1999 if 
you like.  That is when I started the jIRC project.  

The sleep library is (c) 2004-2011 Raphael Mudge.  Sleep is distributed under 
the LGPL license.  I like sleep.  

The sleep library is available at http://sleep.dashnine.org/

All of the jIRCii source code is released under the artistic license.  See 
license.txt for more details.

