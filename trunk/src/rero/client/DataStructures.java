package rero.client;

public interface DataStructures
{
   public static final String InternalDataList = "clientInformation"; //
   public static final String LocalInfo        = "localInfo";         //
   public static final String ScriptManager    = "scriptManager";     // rero.client.script.ScriptManager
   public static final String ScriptLoader     = "scriptLoader";      // sleep.runtime.ScriptLoader
   public static final String SharedEnv        = "sharedEnvironment"; // java.util.Hashtable
   public static final String NotifyData       = "notify";            // rero.client.notify.NotifyData
   public static final String UserHandler      = "commands";          // rero.client.user.UserHandler
   public static final String DataDCC          = "dcc";               // rero.dcc.DataDCC 
   public static final String ScriptVariables  = "scriptVariables";   // global variable container used by all scripts...
}
