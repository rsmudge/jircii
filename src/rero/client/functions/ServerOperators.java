package rero.client.functions;

import sleep.engine.*;
import sleep.runtime.*;
import sleep.interfaces.*;

import rero.util.*;
import rero.client.*;
import rero.config.*;
import java.util.*;
import java.text.*;

import rero.dialogs.server.*;

import sleep.bridges.BridgeUtilities;


public class ServerOperators extends Feature implements Loadable
{
   public void init()
   {
      getCapabilities().getScriptCore().addBridge(this);
   }

   public void scriptLoaded(ScriptInstance script)
   {
      script.getScriptEnvironment().getEnvironment().put("&getAllServers", new getAllServers());
      script.getScriptEnvironment().getEnvironment().put("&getAllNetworks", new getAllNetworks());

      script.getScriptEnvironment().getEnvironment().put("&getServerInfo", new getServerInfo());
      script.getScriptEnvironment().getEnvironment().put("&getServersForNetwork", new getServersForNetwork());

      script.getScriptEnvironment().getEnvironment().put("&serverInfoHost", new serverInfoHost());
      script.getScriptEnvironment().getEnvironment().put("&serverInfoPortRange", new serverInfoPorts());
      script.getScriptEnvironment().getEnvironment().put("&serverInfoNetwork", new serverInfoNetwork());
      script.getScriptEnvironment().getEnvironment().put("&serverInfoIsSecure", new serverInfoIsSecure());
      script.getScriptEnvironment().getEnvironment().put("&serverInfoPassword", new serverInfoPassword());
      script.getScriptEnvironment().getEnvironment().put("&serverInfoDescription", new serverInfoDescription());
      script.getScriptEnvironment().getEnvironment().put("&serverInfoConnectPort", new serverInfoConnectPort());
      script.getScriptEnvironment().getEnvironment().put("&serverInfoCommand", new serverInfoCommand());
   }

   public void scriptUnloaded(ScriptInstance script)
   {
   }

   private static class getAllServers implements Function
   {
      public Scalar evaluate(String f, ScriptInstance si, Stack locals)
      {
         return SleepUtils.getArrayWrapper(ServerData.getServerData().getAllServers());
      }
   }

   private static class getAllNetworks implements Function
   {
      public Scalar evaluate(String f, ScriptInstance si, Stack locals)
      {
         return SleepUtils.getArrayWrapper(ServerData.getServerData().getGroups());
      }
   }

   private static class getServerInfo implements Function
   {
      public Scalar evaluate(String f, ScriptInstance si, Stack locals)
      {
         String temp = BridgeUtilities.getString(locals, "");
         if (temp == null) return SleepUtils.getEmptyScalar();
         return SleepUtils.getScalar(ServerData.getServerData().getServerByName(temp).toString());
      }
   }

   private static class getServersForNetwork implements Function
   {
      public Scalar evaluate(String f, ScriptInstance si, Stack locals)
      {
         String temp = BridgeUtilities.getString(locals, "");
         if (temp == null) return SleepUtils.getEmptyScalar();
         return SleepUtils.getArrayWrapper(ServerData.getServerData().getGroup(temp).getServers());
      }
   }

   private static class serverInfoPassword implements Function
   {
      public Scalar evaluate(String f, ScriptInstance si, Stack locals)
      {
         Server temp = Server.decode(BridgeUtilities.getString(locals, ""));
         if (temp == null) return SleepUtils.getEmptyScalar();
         return SleepUtils.getScalar(temp.getPassword());
      }
   }

   private static class serverInfoDescription implements Function
   {
      public Scalar evaluate(String f, ScriptInstance si, Stack locals)
      {
         Server temp = Server.decode(BridgeUtilities.getString(locals, ""));
         if (temp == null) return SleepUtils.getEmptyScalar();
         return SleepUtils.getScalar(temp.getDescription());
      }
   }

   private static class serverInfoConnectPort implements Function
   {
      public Scalar evaluate(String f, ScriptInstance si, Stack locals)
      {
         Server temp = Server.decode(BridgeUtilities.getString(locals, ""));
         if (temp == null) return SleepUtils.getEmptyScalar();
         return SleepUtils.getScalar(temp.getConnectPort());
      }
   }

   private static class serverInfoCommand implements Function
   {
      public Scalar evaluate(String f, ScriptInstance si, Stack locals)
      {
         Server temp = Server.decode(BridgeUtilities.getString(locals, ""));
         if (temp == null) return SleepUtils.getEmptyScalar();
         return SleepUtils.getScalar(temp.getCommand());
      }
   }

   private static class serverInfoHost implements Function
   {
      public Scalar evaluate(String f, ScriptInstance si, Stack locals)
      {
         Server temp = Server.decode(BridgeUtilities.getString(locals, ""));
         if (temp == null) return SleepUtils.getEmptyScalar();
         return SleepUtils.getScalar(temp.getHost());
      }
   }

   private static class serverInfoPorts implements Function
   {
      public Scalar evaluate(String f, ScriptInstance si, Stack locals)
      {
         Server temp = Server.decode(BridgeUtilities.getString(locals, ""));
         if (temp == null) return SleepUtils.getEmptyScalar();
         return SleepUtils.getScalar(temp.getPorts());
      }
   }

   private static class serverInfoNetwork implements Function
   {
      public Scalar evaluate(String f, ScriptInstance si, Stack locals)
      {
         Server temp = Server.decode(BridgeUtilities.getString(locals, ""));
  
         if (temp == null) return SleepUtils.getEmptyScalar();


         return SleepUtils.getScalar(temp.getNetwork());
      }
   }

   private static class serverInfoIsSecure implements Function
   {
      public Scalar evaluate(String f, ScriptInstance si, Stack locals)
      {
         Server temp = Server.decode(BridgeUtilities.getString(locals, ""));
         if (temp == null) return SleepUtils.getEmptyScalar();

         if (temp.isSecure())
         {
            return SleepUtils.getScalar(1);
         }

         return SleepUtils.getEmptyScalar();
      }
   }
}

