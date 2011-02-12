package rero.dialogs.server;

import java.util.*;
import java.util.regex.*;

import rero.util.*;

public class Server implements Comparable
{
   // x0=server descriptionSERVER:host:portrange:passwordGROUP:network
   // x00=server descriptionSERVER:host:portrangeGROUP:network

   // Server w/  password regex - (\S)\S*=(.*)SERVER:(.*):(.*):(.*)GROUP:(.*)
   // Server w/o password regex - (\S)\S*=(.*)SERVER:(.*):(.*)GROUP:(.*)

   protected static Pattern isServerPassword = Pattern.compile("(\\S)\\S*=(.*)SERVER:(.*):(.*):(.*)GROUP:(.*)");
   protected static Pattern isServerNormal   = Pattern.compile("(\\S)\\S*=(.*)SERVER:(.*):(.*)GROUP:(.*)");

   protected String      description;
   protected String      host;
   protected String      portRange;
   protected String      network;
   protected boolean     isSSL;
   protected String      password;
   protected String      compare;

   public void setValues(String d, String h, String r, String n, boolean s, String p)
   {
       description = d;
       host        = h;
       portRange   = r;
       network     = n;
       isSSL       = s;
       password    = p;

       compare     = n.toUpperCase() + host.toUpperCase();
   }

   public Server() { }

   public Server(String d, String h, String r, String n, boolean s, String p)
   {
       setValues(d, h, r, n, s, p);
   }

   public String toString()
   {
       return toString(0);
//       return "[Server: " + host + ":" + portRange + ", Desc: " + description + ", Network: " + network + ", Password: " + password + ", Secure? " + isSSL + "]";
   }

   public boolean isRandom()
   {
      return getNetwork() == null || getNetwork().length() <= 2;
   }

   public String getCompare() { return compare; }

   public int compareTo(Object o)
   {
      Server arg = (Server)o;

      return getCompare().compareTo(arg.getCompare());
   }

   public String getPassword() 
   {
       return password;
   }

   public String getPorts()
   {
       return portRange;
   }

   public String getHost()
   {
       return host;
   }

   public String getConnectPort()
   {
       String myPort = portRange;

       if (myPort.indexOf("-") > -1)
       {
          myPort = myPort.substring(0, myPort.indexOf("-"));
       }

       if (myPort.indexOf(",") > -1)
       {
          myPort = myPort.substring(0, myPort.indexOf(","));
       }

       return myPort.trim();
   }

   public boolean isSecure()
   {
       return isSSL;
   }

   public String getDescription()
   {
       return description;
   }

   public String getNetwork()
   {
       return network;
   }

   public String getCommand()
   {
       StringBuffer command     = new StringBuffer("/server ");

       if (isSecure())
       {
          command.append("-ssl ");
       }

       if (getPassword() != null && getPassword().length() > 0)
       {
          command.append("-pass ");
          command.append(getPassword());
          command.append(" ");
       }

       command.append(getHost());
       command.append(" ");
       command.append(getConnectPort());

       return command.toString();
   }

   public String toString(int x)
   {
       StringBuffer value = new StringBuffer();

       if (isSSL)
       {
          value.append('s');
       }
       else
       {
          value.append('n');
       }

       value.append(x);
       value.append('=');
       value.append(description);
       value.append("SERVER:");
       value.append(host);
       value.append(":");
       value.append(portRange);
     
       if (password != null && password.length() > 0)
       {
          value.append(":");
          value.append(password);
       }
       
       value.append("GROUP:");
       value.append(network);

       return value.toString();
   }

   public static Server decode(String text)
   {
       StringParser check = new StringParser(text, isServerPassword);
 
       if (check.matches())
       {
          String[] values = check.getParsedStrings();

          boolean secure = values[0].charAt(0) == 's';

          return new Server(values[1], values[2], values[3], values[5], secure, values[4]);
       }

       check = new StringParser(text, isServerNormal);

       if (check.matches())
       {
          // 0: s
          // 1: Random US DALnet server
          // 2: irc.dal.net
          // 3: 6660-6667
          // 4: 01

          String[] values = check.getParsedStrings();

          boolean secure = values[0].charAt(0) == 's';

          return new Server(values[1], values[2], values[3], values[4], secure, null);
       }

       return null;
   }
}
