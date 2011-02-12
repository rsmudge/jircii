package rero.test;

import java.net.URI;

public class QuickConnect
{
   private static QuickConnect qc = null;
   private URI    info;

   public static boolean IsQuickConnect()
   {
      return qc != null;
   }

   public QuickConnect(URI _info)
   {
      qc    = this;
      info  = _info;
   }

   public String getConnectCommand()
   {
      StringBuffer doit = new StringBuffer();
      doit.append("/server ");

      if (isSSL())
      {
         doit.append("-ssl ");
      }

      doit.append(getServer());
      doit.append(" ");
      doit.append(getPort());

      return doit.toString();
   } 

   public static QuickConnect GetInformation() 
   {
      return qc;
   }

   public URI getURL()
   {
      return info;
   }

   public String getNickname()
   {
      if (info.getUserInfo() != null)
         return info.getUserInfo();

      return "Guest_" + (System.currentTimeMillis() % 100);
   }

   public boolean isSSL()
   {
       System.out.println(info.getScheme());
       return "ircs".equals(info.getScheme());
   }

   public String getPort()
   {
      if (info.getPort() > -1)
          return info.getPort() + "";

      return "6667"; 
   }

   public String getServer()
   {
      String temp = info.getHost();

      if (info.getPath() == null || info.getPath().length() == 0)
      {
         qc = null; info = null;
      }

      return temp;
   }

   public String getCommand()
   {
      String temp = "/join #" + info.getPath().substring(1, info.getPath().length()) + " " + info.getQuery(); 

      qc   = null;
      info = null;

      return temp;
   }
}

