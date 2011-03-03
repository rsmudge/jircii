 ------- -------------------- ---------   ---------------  ------- ---------
jIRCii - 0.9 (Release ??.??.??) - README
-  ------------- -------- -   ---------------- --       -    -     -     - - -

The official jIRCii homepage is at: http://www.oldschoolirc.com/

jIRCii is a cross-platform IRC client for any platform with Java 1.6+.

For help and information about jIRCii run the application and view the 
Help menu.  

The jIRCii code and binaries are released under the artistic license.  

Enjoy.

				-- Raffi

 ------- -------------------- ---------   ---------------  ------- ---------
Running and Installation
-  ------------- -------- -   ---------------- --       -    -     -     - - -

1. -   Installation      - -

   Unarchive your respective jIRCii archive file. Copy the files where you
   want them.

2. --  Which Java to Use - -

   You need to have the Java 1.6+ (or later) runtime environment.  To 
   obtain this just visit http://www.java.com, find the Get Java now 
   button and download the appropriate JRE for your operating system.
 
   You do not need to download the software development kit, just the runtime 
   environment.
   
3. - - Running jIRCii    --

   Once the runtime environment is installed you just need to simply 
   launch jIRCii.
   
   To do this from a commandline you can type: java -jar jerk.jar

   === Command Line Options ===

   jIRC has a -settings command line option.  This is for specifying 
   where your settings files are located: 

   java -jar jerk.jar -settings c:\mystuff\jirc\

   jIRCii also has a -lnf command line option.  This is for specifying the
   desired Java look and feel to use with jIRCii:

   java -jar jerk.jar -lnf com.sun.java.swing.plaf.motif.MotifLookAndFeel

   You can also specify an irc:// URL on the jIRCii command line to specify
   a irc server and channel you want jIRCii to join on startup:

   java -jar jerk.jar irc://irc.prison.net/jircii

   === Windows ===

   The Windows distribution of jIRCii comes with a jircii.exe file.  You
   can use this to launch jIRCii and pass any of the above command line
   arguments to jIRCii.

   Unfortunately, jircii.exe does not work on Windows 64-bit. Double-click
   the jar file to launch jIRCii in this case.

   === Mac OS X ===

   If you have the Mac OS X specific archive of jIRCii just double click 

   jIRCii.app

4.  -- Upgrading       --

   If you are upgrading from an older version of jIRCii and you are 
   experiencing problems you may want to delete your jIRCii settings.

   Your jIRCii settings are located in your home directory under the folder 
   ".jIRC".  

   On a UNIX system your home directory is typically /home/<your-user-id>
   On MacOS X your home directory is typically /Users/<your-user-id>

   On Windows your home directory may appear in 
   c:\documents and settings\<your-user-id>

   Delete the ".jIRC" folder.  jIRCii will then recreate this folder with new
   preferences next time you run jIRCii.
