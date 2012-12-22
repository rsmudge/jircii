package rero.util;

//
// jerk.util.JerkUtils
// -------------------
// little utility functions that no IRC client coder should ever leave
// home without.
//
import java.util.*;
import java.io.*;
import java.net.*;

import java.awt.*;
import javax.swing.*;

import rero.config.*;

import text.*;
import rero.dialogs.*;

import java.lang.reflect.*; 	// For attention getting routines (we have to call indirectly)
import rero.ircfw.*;
import rero.ircfw.interfaces.*;
import rero.client.*;
import rero.client.server.*;
import rero.client.user.*;
import rero.client.output.*;
import rero.net.*;
import rero.client.script.*;
import rero.client.notify.*;
import rero.gui.*;
import rero.util.*;

public class ClientUtils
{
    public static void invokeLater(Runnable doIt)
    {
        if (SwingUtilities.isEventDispatchThread())
            doIt.run();
        else
            SwingUtilities.invokeLater(doIt);
    }

    public static JFrame getFrameForComponent(JComponent c)
    {
        Component temp = (Component)c.getParent();
        while (temp != null)
        {
           if (temp instanceof JFrame)
           {
              return (JFrame)temp;
           }

           temp = temp.getParent();
        }

        return null;
    }

    public static String strip(String text)
    {
        return AttributedString.CreateAttributedString(text).getText();
    }

    public static String ShowVersion()
    {
        String tagline = ClientUtils.tagline();

        return "jIRCii " + ClientState.getClientState().getString("version.string", ClientDefaults.version_string) + " " + System.getProperty("os.name").replaceAll(" ", "") + " : " + ClientState.getClientState().getString("version.addons", ClientUtils.tagline());
    }

    public static String tagline()
    {
        String taglines[] = {
            "highly caffeinated",
            "Fat butane, grubbin' on French fries",
            "No rest for the wicked",
            "Clean. Christian. Comprehensive.",
	    "a series of tubes",
        };
        int r = ctime() % taglines.length;
        return taglines[r];
    }

   // Return version strings

   public String getVersionBase() {
	   return getVersionMajor() + "." + getVersionMinor();
   }
   public String getVersionBaseRevision() {
	   String vString = getVersionBase();

	   if (ClientDefaults.version_rev != null && !ClientDefaults.version_rev.isEmpty())
		   vString = vString + "-" + ClientDefaults.version_rev;

	   return vString;
   }
   public String getVersionFull() {
	   String vString = getVersionBase();

	   if (ClientDefaults.version_rev != null && !ClientDefaults.version_rev.isEmpty())
		   vString = vString + "-" + ClientDefaults.version_rev;

	   if (ClientDefaults.version_extra != null && !ClientDefaults.version_extra.isEmpty())
		   vString = vString + "+" + ClientDefaults.version_extra;

	   return vString;
   }
   public String getVersionMajor() {
	   return ClientDefaults.version_major;
   }
   public String getVersionMinor() {
	   return ClientDefaults.version_minor;
   }
   public String getVersionRevision() {
	   return ClientDefaults.version_rev;
   }
   public String getVersionExtra() {
	   return ClientDefaults.version_extra;
   }

    public static String formatTime(long seconds)
    {
        StringBuffer rv = new StringBuffer();

        if (seconds < 60)
        {
          return seconds + " seconds";
        }

        if (seconds < (60 * 60))
        {
           return (seconds / 60) + " minutes";
        }

        if (seconds < (60 * 60 * 24))
        {
           return (seconds / (60 * 60)) + " hours";
        }

        return (seconds / (60 * 60 * 24)) + " days";
    }

    public static String formatTime2(long secs)
    {
        StringBuffer rv = new StringBuffer();

        int seconds = (int)secs, minutes = 0, hours = 0, days = 0;

        days    = seconds / (60 * 60 * 24);
        hours   = seconds / (60 * 60) % 24;
        minutes = (seconds / 60) % 60;
        seconds = seconds % 60;

        if (days > 0) {
            rv.append(days);
            rv.append(":");
        }

        if (hours > 0) {
            if (hours < 10) rv.append("0");
            rv.append(hours);

            rv.append(":");
        }

        if (minutes > 0) {
            if (minutes < 10) rv.append("0");
            rv.append(minutes);

            rv.append(":");
        }

        if (seconds < 10) rv.append("0");
        rv.append(seconds);

        return rv.toString();
    }

    public static String formatTime3(long secs) {
        int seconds = (int) secs;
        int minutes = 0;
        int hours = 0;
        int days = 0;

        days = seconds / (60 * 60 * 24);
        hours = seconds / (60 * 60) % 24;
        minutes = (seconds / 60) % 60;
        seconds %= 60;

        StringBuffer buf = new StringBuffer();
        if (days > 0)
            buf.append(days).append(days != 1 ? " days, " : " day, ");
        if (days > 0 || hours > 0)
            buf.append(hours).append(hours != 1 ? " hours, " : " hour, ");
        if (hours > 0 || minutes > 0)
            buf.append(minutes).append(minutes != 1 ? " minutes, " : " minute, ");

        buf.append(seconds).append(seconds != 1 ? " seconds" : " second");

        return buf.toString();
    }

    public static String formatBytes(long bytes)
    {
        if (bytes < 1024)
        {
           return bytes + "b";
        }

        bytes = bytes / 1024;

        if (bytes < 1024)
        {
           return bytes + "kb";
        }

        bytes = bytes / 1024;

        if (bytes < 1024)
        {
           return bytes + "mb";
        }

        bytes = bytes / 1024;

        return bytes + "GB";
    }

    public static String encodeFont(Font value)
    {
        StringBuffer saved = new StringBuffer();
        saved.append(value.getFamily());
        saved.append("-");

        if (value.isBold())
        {
           saved.append("BOLD");
        }
        else if (value.isItalic())
        {
           saved.append("ITALIC");
        }
        else
        {
           saved.append("PLAIN");
        }

        saved.append("-");
        saved.append(value.getSize());

        return saved.toString();
    }

    public static void openURL(String location)
    {
        try
        {
           Desktop.getDesktop().browse(new URL(location).toURI());
        }
        catch(Exception ex)
        {
           ex.printStackTrace();
        }
    }

    public static String mask(String address, int type)
    {
        if (address.length() == 0 || address.indexOf('!') < 1 || address.indexOf('@') < 1 || address.indexOf('.') < 1)
        {
           return "<bad address, use nick!user@host format>";
        }

        String n, u, h, d;
        n = address.substring(0, address.indexOf('!'));
        u = address.substring(address.indexOf('!')+1, address.indexOf('@'));
        h = address.substring(address.indexOf('@')+1, address.length());
        if (address.lastIndexOf('.',  address.lastIndexOf('.') - 1) > -1)
        {
           d = "*"+address.substring(address.lastIndexOf('.',  address.lastIndexOf('.') - 1), address.length());
        }
        else
        {
           d = h;
        }

        switch (type)
        {
           case 0:  return "*!"+u+"@"+h;
           case 1:  return "*!*"+u+"@"+h;
           case 2:  return "*!*@"+h;
           case 3:  return "*!*"+u+"@"+d;
           case 4:  return "*!*@"+d;
           case 5:  return n+"!"+u+"@"+h;
           case 6:  return n+"!*"+u+"@"+h;
           case 7:  return n+"!*@"+h;
           case 8:  return n+"!*"+u+"@"+d;
           case 9:  return n+"!*@"+d;
        }
        return address;
    }

    public static String longip(String l)
    {
		// 192.168.1.2
		//  a   b  c d
        TokenizedString s = new TokenizedString(l);
        s.tokenize(".");

        long a, b, c, d, x;

	if (s.getTotalTokens() == 4)
	{
           a = Long.parseLong(s.getToken(0));
	   b = Long.parseLong(s.getToken(1));
	   c = Long.parseLong(s.getToken(2));
	   d = Long.parseLong(s.getToken(3));

	   x = (a << 24) + (b << 16) + (c << 8) + d;
	   return x+"";
        }
        else
        {
           x = Long.parseLong(l);
           a = (x & 0xff000000) >> 24;
           b = (x & 0x00ff0000) >> 16;
           c = (x & 0x0000ff00) >> 8;
           d = (x & 0x000000ff);

	   return a+"."+b+"."+c+"."+d;
	}
    }

    public static String TimeStamp()
    {
        String rv;

        Date rightNow = new Date();
        String am_pm = "am";
        int hours = rightNow.getHours();
        int minutes = rightNow.getMinutes();

        if (hours == 0)
        {
           hours = 12;
           am_pm = "am";
        }
        else if (hours > 12) { hours -= 12; am_pm = "pm"; }
        else if (hours == 12) { am_pm = "pm"; }

        if (minutes <= 9)
        {
           rv = hours+":0"+minutes+am_pm;
        }
        else
        {
           rv = hours+":"+minutes+am_pm;
        }

        return rv;
    }

    public static HashMap getEventHashMap(String target, String parms)
    {
        HashMap temp = new HashMap();
        temp.put("$data", target + " " + parms);
        temp.put("$parms", parms);

        return temp;
    }

    public static String TimeDateStamp(long l)
    {
           Date temp = new Date(l * 1000);
 	   String am_pm = "am";
	   int hours = temp.getHours();
	   int minutes = temp.getMinutes();

	   StringBuffer value = new StringBuffer("");
           StringBuffer rv = new StringBuffer("");

	   if (hours >= 12)
	   {
              hours -= 12;
	      am_pm = "pm";

              if (hours == 0)
              {
                 hours = 12;
              }
	   }
	   if (minutes <= 9)
	   {
	      value.append(hours);
              value.append(":0");
              value.append(minutes);
              value.append(am_pm);
	   }
	   else
	   {
	      value.append(hours);
              value.append(":");
              value.append(minutes);
              value.append(am_pm);
           }
           // Thu Jul 05
           rv.append(intToDay(temp.getDay()));
           rv.append(" ");
           rv.append(intToMonth(temp.getMonth()));
           rv.append(" ");
           rv.append(temp.getDate());
           rv.append(" ");
           rv.append(temp.getYear() + 1900);
           rv.append(" ");
           rv.append(value.toString());

           return rv.toString();
	}

    public static String intToDay(int d)
	{
                d = d % 7;

		switch (d)
		{
                case 0:
                        return "Sun";
		case 1:
			return "Mon";
		case 2:
			return "Tues";
		case 3:
			return "Wed";
		case 4:
			return "Thurs";
		case 5:
			return "Fri";
		case 6:
			return "Sat";
		}
        return "Unknown Day: "+d;
	}

    public static boolean isNumeric(String n)
    {
       return (Character.isDigit(n.charAt(0)));
    }

    public static String intToMonth(int m)
    {
       switch (m)
       {
         case 0:
           return "Jan";
		 case 1:
           return "Feb";
		 case 2:
		   return "Mar";
         case 3:
		   return "Apr";
         case 4:
		   return "May";
         case 5:
		   return "Jun";
         case 6:
           return "Jul";
		 case 7:
           return "Aug";
		 case 8:
           return "Sep";
		 case 9:
           return "Oct";
		 case 10:
           return "Nov";
		 case 11:
           return "Dec";
	   }
       return "Unknown Month: "+m;
	}

    // This is a static isChannel, and does not know what channels are supported for our current connected IRC server.
    public static boolean isChannel(String target)
    {
    	return (target.length() > 0 && "#&!+".indexOf(target.charAt(0)) > -1);
    }

    public static int ctime()
    {
       Long temp = new Long(System.currentTimeMillis() / 1000);
       return temp.intValue();
    }

     private static String CP437TABLE = "\u0000\u0001\u0002\u0003\u0004\u0005\u0006\u0007\u0008\u0009\n\u000B\u000C\r\u000E\u000F\u0010\u0011\u0012\u0013\u0014\u0015\u0016\u0017\u0018\u0019\u001A\u001B\u001C\u001D\u001E\u001F\u0020\u0021\"\u0023\u0024\u0025\u0026'\u0028\u0029\u002A\u002B\u002C\u002D\u002E\u002F\u0030\u0031\u0032\u0033\u0034\u0035\u0036\u0037\u0038\u0039\u003A\u003B\u003C\u003D\u003E\u003F\u0040\u0041\u0042\u0043\u0044\u0045\u0046\u0047\u0048\u0049\u004A\u004B\u004C\u004D\u004E\u004F\u0050\u0051\u0052\u0053\u0054\u0055\u0056\u0057\u0058\u0059\u005A\u005B\\\u005D\u005E\u005F\u0060\u0061\u0062\u0063\u0064\u0065\u0066\u0067\u0068\u0069\u006A\u006B\u006C\u006D\u006E\u006F\u0070\u0071\u0072\u0073\u0074\u0075\u0076\u0077\u0078\u0079\u007A\u007B\u007C\u007D\u007E\u007F\u00C7\u00FC\u00E9\u00E2\u00E4\u00E0\u00E5\u00E7\u00EA\u00EB\u00E8\u00EF\u00EE\u00EC\u00C4\u00C5\u00C9\u00E6\u00C6\u00F4\u00F6\u00F2\u00FB\u00F9\u00FF\u00D6\u00DC\u00A2\u00A3\u00A5\u20A7\u0192\u00E1\u00ED\u00F3\u00FA\u00F1\u00D1\u00AA\u00BA\u00BF\u2310\u00AC\u00BD\u00BC\u00A1\u00AB\u00BB\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255D\u255C\u255B\u2510\u2514\u2534\u252C\u251C\u2500\u253C\u255E\u255F\u255A\u2554\u2569\u2566\u2560\u2550\u256C\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256B\u256A\u2518\u250C\u2588\u2584\u258C\u2590\u2580\u03B1\u00DF\u0393\u03C0\u03A3\u03C3\u00B5\u03C4\u03A6\u0398\u03A9\u03B4\u221E\u03C6\u03B5\u2229\u2261\u00B1\u2265\u2264\u2320\u2321\u00F7\u2248\u00B0\u2219\u00B7\u221A\u207F\u00B2\u25A0 ";

    public static String BuildCP437String(String text)
    {
       char[] temp = text.toCharArray();
       for (int x = 0; x < temp.length; x++)
       {
           int value = (int)temp[x];

           if (value < CP437TABLE.length())
              temp[x] = CP437TABLE.charAt(value);
       }

       return new String(temp);
    }

    public static void dump2 (HashMap data)
    {
       Iterator i = data.keySet().iterator();
       while (i.hasNext())
       {
           String key = (String)i.next();
           System.out.println(key + "=> " + data.get(key));
       }
       System.out.println("   ... Done ...");
    }

    public static String formatLongAsDecimal (long l)
    {
        String rv = l+"";
        if (rv.length() < 3)
        {
           while (rv.length() < 3)
           {
              rv = "0" + rv;
           }

           return "." + rv;
        }

        return rv.substring(0, rv.length() - 3) + "." + rv.substring(rv.length() - 3, rv.length());
    }

    public static void removeAll(Collection source, Set remove)
    {
        Iterator i = remove.iterator();
        while (i.hasNext())
        {
           source.remove(i.next());
        }
    }

    public static String generateThemeScript(String name)
    {
        if (name == null || name.length() == 0)
        {
            File file = DialogUtilities.showSaveDialog("Save Theme Script");
            if (file != null)
               name = file.getAbsolutePath();
        }

        if (name == null || name.length() == 0)
            return null;

        StringBuffer temp = new StringBuffer();

        temp.append("# jIRCii Theme File, scripters feel free to edit this file to export more settings\n");
        temp.append("#     by default only color settings are exported.\n\n");

        temp.append("# some miscellaneous colors\n");
        temp.append(generateThemeLine("statusbar.color", null));
        temp.append(generateThemeLine("window.color", null));
        temp.append(generateThemeLine("desktop.color", null));
        temp.append(generateThemeLine("switchbar.color", null));
        temp.append(generateThemeLine("ui.editcolor", null));

        temp.append("\n");

        temp.append("# the actual color map\n");

        for (int x = 0; x < 100; x++)
        {
            temp.append("setMappedColor(");
            temp.append(x);
            temp.append(", \"");
            temp.append(TextSource.colorTable[x].getRGB());
            temp.append("\");\n");
        }

        temp.append("\n# force jIRCii to update settings right away\n");
        temp.append("setProperty(\"desktop\", -1);\n");
        temp.append("setProperty(\"statusbar\", -1);\n");
        temp.append("setProperty(\"window\", -1);\n");

        temp.append("\nsaveColorMap();\n");

        try
        {
           PrintWriter output = new PrintWriter(new FileOutputStream(new File(name), false));
           output.println(temp.toString());
           output.close();
        }
        catch (Exception ex)
        {
           ex.printStackTrace();
        }

        return (new File(name)).getName();
    }

    private static String generateThemeLine(String var, String comment)
    {
        StringBuffer temp = new StringBuffer();

        if (ClientState.getClientState().getString(var, null) != null)
        {
           if (comment != null)
              temp.append("# " + comment + "\n");

           temp.append("setProperty(\"");
           temp.append(var);
           temp.append("\", \"");
           temp.append(ClientState.getClientState().getString(var, null));
           temp.append("\");\n");
        }

        return temp.toString();
    }

    public static File getFile(String file)
    {
        if (file.length() > 0 && file.charAt(0) == '~' && System.getProperty("user.home") != null)
        {
            return new File(System.getProperty("user.home").toString(), file.substring(1));
        }

        return new File(file);
    }

    public static LinkedList fileCompleteAll(String pfile)
    {
        LinkedList rv = new LinkedList();

        File   cwd;
        String cwf = null;

        File temp = getFile(pfile);
        if (temp.isDirectory())
        {
           cwd = temp;
           cwf = null;
        }
        else if (temp.getParentFile() != null && temp.getParentFile().isDirectory())
        {
           cwd = temp.getParentFile();
           cwf = temp.getName();
        }
        else
        {
           cwd = new File(new File("").getAbsolutePath());
           cwf = pfile;
        }

        File[] files = cwd.listFiles();
        for (int x = 0; x < files.length; x++)
        {
           String tempn = files[x].getName();

           if (cwf == null)
           {
              rv.add(files[x].getAbsolutePath());
           }
           else if (tempn.length() >= cwf.length() && tempn.toUpperCase().substring(0, cwf.length()).equals(cwf.toUpperCase()))
           {
              rv.add(0, files[x].getAbsolutePath());
           }
           else if (tempn.toUpperCase().indexOf(cwf.toUpperCase()) > -1)
           {
              rv.add(files[x].getAbsolutePath());
           }
        }

        rv.add(pfile);

        return rv;
    }

    // Return operating system; OS X (0), Windows (1) or Linux (2)
    public static int GetOS()
    {
  	String OS = System.getProperty("os.name").toLowerCase();
		
	if (OS.indexOf("mac") >= 0)
		return 0;
	else if (OS.indexOf("win") >= 0)
		return 1;
	else if (OS.indexOf("linux") >= 0)
		return 2;

	return -1;

    }

    // Returns the operating system
    public static boolean isWindows() { return (GetOS() == 1); }
    public static boolean isLinux() { return (GetOS() == 2); }
    public static boolean isMac() { return (GetOS() == 0); }

    // Notify user of activity in status bar, dock, task bar, whatever
    public static void getAttention()
    {
	    if (isMac()) {
		    // OS X: We bounce the dock icon up and down.
		    try {
		    	Class osxAdapter = Class.forName("apple.OSXAdapter");
		  	  Class[] defArgs = new Class[] { boolean.class }; // We're going to be passing one argument to the method
		  	  Method gaMethod = osxAdapter.getDeclaredMethod("getAttention", defArgs);
		  	  if (gaMethod != null)
			  {
				  Boolean blnObj = new Boolean(ClientState.getClientState().isOption("option.attention.osx.bouncedock.repeat", ClientDefaults.attention_osx_bouncedock_repeat));
				  gaMethod.invoke(osxAdapter, new Object[] { blnObj } ); // apple.OSXAdapter.getAttention(boolean) equivalent; what a bitch to code this.
			  }
		    }
		    catch (Exception ex)
		    {
			    System.err.println("Exception while trying to call OSXAdapter.getAttention() (indirectly): ");
			    ex.printStackTrace();
		    }
	    }
	    else if (isWindows()) {
		    return;
	    }
	    else if (isLinux()) {
		    return;
	    }
    }
}
