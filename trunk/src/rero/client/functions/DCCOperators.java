package rero.client.functions;

import sleep.engine.*;
import sleep.runtime.*;
import sleep.interfaces.*;
import sleep.bridges.BridgeUtilities;

import rero.client.*;
import rero.util.*;
import java.util.*;

import rero.dcc.*;
import rero.client.dcc.*;

public class DCCOperators extends Feature implements Loadable
{
   protected DataDCC dccData;

   public void init()
   {
      dccData = (DataDCC)getCapabilities().getDataStructure(DataStructures.DataDCC);
      getCapabilities().getScriptCore().addBridge(this);
   }

   public void scriptLoaded(ScriptInstance script)
   {
      script.getScriptEnvironment().getEnvironment().put("&getActiveConnections", new getActiveConnections());
      script.getScriptEnvironment().getEnvironment().put("&getWaitingConnections", new getWaitingConnections());
      script.getScriptEnvironment().getEnvironment().put("&getInactiveConnections", new getInactiveConnections());

      script.getScriptEnvironment().getEnvironment().put("&getSpecificConnection", new getSpecificConnection());
      script.getScriptEnvironment().getEnvironment().put("&getAllConnections", new getAllConnections());
      script.getScriptEnvironment().getEnvironment().put("&getDCCConnection", new getDCCConnection());

      script.getScriptEnvironment().getEnvironment().put("&getConnectionType", new getConnectionType());
      script.getScriptEnvironment().getEnvironment().put("&getConnectionState", new getConnectionState());

      script.getScriptEnvironment().getEnvironment().put("&getLocalPort", new getLocalPort());
      script.getScriptEnvironment().getEnvironment().put("&getRemotePort", new getRemotePort());

      script.getScriptEnvironment().getEnvironment().put("&getDCCAddress", new getConnectionAddress());
      script.getScriptEnvironment().getEnvironment().put("&getDCCNickname", new getConnectionNickname());
      script.getScriptEnvironment().getEnvironment().put("&getDCCIdleTime", new getIdleTime());
      script.getScriptEnvironment().getEnvironment().put("&getDCCStartTime", new getStartTime());
      script.getScriptEnvironment().getEnvironment().put("&getDCCTotalTime", new getTotalTime());

      script.getScriptEnvironment().getEnvironment().put("-isdccopen", new isOpen());

      script.getScriptEnvironment().getEnvironment().put("&closeDCC", new closeDCC());

      script.getScriptEnvironment().getEnvironment().put("&getFileSizeOffset", new B_getStartOffset());
      script.getScriptEnvironment().getEnvironment().put("&getDCCFileName", new B_getFileName());
      script.getScriptEnvironment().getEnvironment().put("&getDCCFilePath", new B_getFilePath());
      script.getScriptEnvironment().getEnvironment().put("&getTransferRate", new B_getTransferRate());
      script.getScriptEnvironment().getEnvironment().put("&getTimeRemaining", new B_getTimeRemaining());

      script.getScriptEnvironment().getEnvironment().put("&getAcknowledgedSize", new S_getAcknowledgedSize());
      script.getScriptEnvironment().getEnvironment().put("&getBytesSent", new S_getBytesSent());

      script.getScriptEnvironment().getEnvironment().put("&getBytesReceived", new R_getBytesReceived());
      script.getScriptEnvironment().getEnvironment().put("&getExpectedSize", new R_getExpectedSize());

      script.getScriptEnvironment().getEnvironment().put("&localip", new localip());
      script.getScriptEnvironment().getEnvironment().put("&getNextPort", new getNextPort());
   }

   public void scriptUnloaded(ScriptInstance script)
   {
   }

   private class localip implements Function
   {
      public Scalar evaluate(String f, ScriptInstance si, Stack locals)
      {
         LocalInfo linfo = (LocalInfo)getCapabilities().getDataStructure(DataStructures.LocalInfo);
         return SleepUtils.getScalar(linfo.localip());
      }
   }

   private static class getNextPort implements Function
   {
      public Scalar evaluate(String f, ScriptInstance si, Stack locals)
      {
         return SleepUtils.getScalar(ListenDCC.getNextPort());
      }
   }


   private class getActiveConnections implements Function
   {
      public Scalar evaluate(String f, ScriptInstance si, Stack locals)
      {
         return SleepUtils.getArrayWrapper(dccData.getConnections(-1, ProtocolDCC.STATE_OPEN));
      }
   }

   private class getSpecificConnection implements Function
   {
      public Scalar evaluate(String f, ScriptInstance si, Stack locals)
      {
         String nick  = locals.pop().toString();
         String type  = BridgeUtilities.getString(locals, "Unknown");

         return SleepUtils.getScalar(dccData.getSpecificConnection(nick, getType(type)));
      }
   }

   private class getDCCConnection implements Function
   {
      public Scalar evaluate(String f, ScriptInstance si, Stack locals)
      {
         String nick = locals.pop().toString();
         return SleepUtils.getScalar(dccData.getConnectionFromHash(nick));
      }
   }

   private class closeDCC implements Function
   {
      public Scalar evaluate(String f, ScriptInstance si, Stack locals)
      {
         GenericDCC temp = (GenericDCC)BridgeUtilities.getObject(locals);
         temp.getImplementation().close();
         return SleepUtils.getEmptyScalar();
      }
   }

   public static int getType(String type)
   {
      type = type.toUpperCase();

      if (type.equals("SEND"))
         return ProtocolDCC.DCC_SEND;

      if (type.equals("CHAT"))
         return ProtocolDCC.DCC_CHAT;

      if (type.equals("RECEIVE"))
         return ProtocolDCC.DCC_RECEIVE;

      return -1;
   }

   private static String getTypeString(int type)
   {
      if (type == ProtocolDCC.DCC_SEND)
         return "SEND";

      if (type == ProtocolDCC.DCC_CHAT)
         return "CHAT";

      if (type == ProtocolDCC.DCC_RECEIVE)
         return "RECEIVE";

      return "Unknown";
   }

   private static String getStateString(int type)
   {
      if (type == ProtocolDCC.STATE_OPEN)
         return "OPEN";

      if (type == ProtocolDCC.STATE_CLOSED)
         return "CLOSED";

      if (type == ProtocolDCC.STATE_WAIT)
         return "WAIT";

      return "Unknown";
   }

   private static int getState(String type)
   {
      if (type.equals("OPEN"))
         return ProtocolDCC.STATE_OPEN;

      if (type.equals("CLOSED"))
         return ProtocolDCC.STATE_CLOSED;

      if (type.equals("WAIT"))
         return ProtocolDCC.STATE_WAIT;

      return -1;
   }

   private class getWaitingConnections implements Function
   {
      public Scalar evaluate(String f, ScriptInstance si, Stack locals)
      {
         return SleepUtils.getArrayWrapper(dccData.getConnections(-1, ProtocolDCC.STATE_WAIT));
      }
   }

   private class getInactiveConnections implements Function
   {
      public Scalar evaluate(String f, ScriptInstance si, Stack locals)
      {
         return SleepUtils.getArrayWrapper(dccData.getConnections(-1, ProtocolDCC.STATE_CLOSED));
      }
   }

   private class getAllConnections implements Function
   {
      public Scalar evaluate(String f, ScriptInstance si, Stack locals)
      {
         return SleepUtils.getArrayWrapper(dccData.getAllConnections());
      }
   }

   private class getConnectionType implements Function
   {
      public Scalar evaluate(String f, ScriptInstance si, Stack locals)
      {
         GenericDCC temp = (GenericDCC)BridgeUtilities.getObject(locals);
         return SleepUtils.getScalar(getTypeString(temp.getTypeOfDCC()));
      }
   }

   private class getConnectionState implements Function
   {
      public Scalar evaluate(String f, ScriptInstance si, Stack locals)
      {
         GenericDCC temp = (GenericDCC)BridgeUtilities.getObject(locals);
         return SleepUtils.getScalar(getStateString(temp.getState()));
      }
   }

   private class getLocalPort implements Function
   {
      public Scalar evaluate(String f, ScriptInstance si, Stack locals)
      {
         GenericDCC temp = (GenericDCC)BridgeUtilities.getObject(locals);
         return SleepUtils.getScalar(temp.getImplementation().getLocalPort());
      }
   }

   private class getRemotePort implements Function
   {
      public Scalar evaluate(String f, ScriptInstance si, Stack locals)
      {
         GenericDCC temp = (GenericDCC)BridgeUtilities.getObject(locals);
         return SleepUtils.getScalar(temp.getImplementation().getPort());
      }
   }

   private class getConnectionAddress implements Function
   {
      public Scalar evaluate(String f, ScriptInstance si, Stack locals)
      {
         GenericDCC temp = (GenericDCC)BridgeUtilities.getObject(locals);
         return SleepUtils.getScalar(temp.getImplementation().getRemoteAddress());
      }
   }

   private class getConnectionNickname implements Function
   {
      public Scalar evaluate(String f, ScriptInstance si, Stack locals)
      {
         GenericDCC temp = (GenericDCC)BridgeUtilities.getObject(locals);
         return SleepUtils.getScalar(temp.getImplementation().getNickname());
      }
   }

   private class getIdleTime implements Function
   {
      public Scalar evaluate(String f, ScriptInstance si, Stack locals)
      {
         GenericDCC temp = (GenericDCC)BridgeUtilities.getObject(locals);
         return SleepUtils.getScalar(temp.getImplementation().getIdleTime());
      }
   }

   private class getStartTime implements Function
   {
      public Scalar evaluate(String f, ScriptInstance si, Stack locals)
      {
         GenericDCC temp = (GenericDCC)BridgeUtilities.getObject(locals);
         return SleepUtils.getScalar(temp.getImplementation().getStartTime());
      }
   }

   private class getTotalTime implements Function
   {
      public Scalar evaluate(String f, ScriptInstance si, Stack locals)
      {
         GenericDCC temp = (GenericDCC)BridgeUtilities.getObject(locals);
         return SleepUtils.getScalar(temp.getImplementation().getTotalTime());
      }
   }

   private class isOpen implements Predicate
   {
      public boolean decide(String f, ScriptInstance si, Stack locals)
      {
         GenericDCC temp = (GenericDCC)BridgeUtilities.getObject(locals);
         return temp.getState() == ProtocolDCC.STATE_OPEN;
      }
   }

   //
   // SEND Specific Functions
   // 

   private class S_getAcknowledgedSize implements Function
   {
      public Scalar evaluate(String f, ScriptInstance si, Stack locals)
      {
         Send temp = (Send)(((GenericDCC)BridgeUtilities.getObject(locals)).getImplementation());
         return SleepUtils.getScalar(temp.getAcknowledgedSize());
      }
   }

   private class S_getBytesSent implements Function
   {
      public Scalar evaluate(String f, ScriptInstance si, Stack locals)
      {
         Send temp = (Send)(((GenericDCC)BridgeUtilities.getObject(locals)).getImplementation());
         return SleepUtils.getScalar(temp.getBytesSent());
      }
   }

   //
   // RECEIVE Specific Functions
   //

   private class R_getExpectedSize implements Function
   {
      public Scalar evaluate(String f, ScriptInstance si, Stack locals)
      {
         Receive temp = (Receive)(((GenericDCC)BridgeUtilities.getObject(locals)).getImplementation());
         return SleepUtils.getScalar(temp.getExpectedSize());
      }
   }

   private class R_getBytesReceived implements Function
   {
      public Scalar evaluate(String f, ScriptInstance si, Stack locals)
      {
         Receive temp = (Receive)(((GenericDCC)BridgeUtilities.getObject(locals)).getImplementation());
         return SleepUtils.getScalar(temp.getBytesReceived());
      }
   }

   //
   // Common to both SEND and RECEIVE
   //

   private class B_getStartOffset implements Function
   {
      public Scalar evaluate(String f, ScriptInstance si, Stack locals)
      {
         ProtocolDCC temp = ((GenericDCC)BridgeUtilities.getObject(locals)).getImplementation();

         if (temp instanceof Send)
            return SleepUtils.getScalar(((Send)temp).getStartOffset());

         if (temp instanceof Receive)
            return SleepUtils.getScalar(((Receive)temp).getStartOffset());
 
         return SleepUtils.getEmptyScalar();
      }
   }

   private class B_getTransferRate implements Function
   {
      public Scalar evaluate(String f, ScriptInstance si, Stack locals)
      {
         ProtocolDCC temp = deriveImplementation(BridgeUtilities.getObject(locals));

         if (temp instanceof Send)
            return SleepUtils.getScalar(((Send)temp).getTransferRate());

         if (temp instanceof Receive)
            return SleepUtils.getScalar(((Receive)temp).getTransferRate());
 
         return SleepUtils.getEmptyScalar();
      }
   }

   private class B_getTimeRemaining implements Function
   {
      public Scalar evaluate(String f, ScriptInstance si, Stack locals)
      {
         ProtocolDCC temp = deriveImplementation(BridgeUtilities.getObject(locals));

         if (temp instanceof Send)
            return SleepUtils.getScalar(((Send)temp).getTimeRemaining());

         if (temp instanceof Receive)
            return SleepUtils.getScalar(((Receive)temp).getTimeRemaining());
 
         return SleepUtils.getEmptyScalar();
      }
   }

   private class B_getFileName implements Function
   {
      public Scalar evaluate(String f, ScriptInstance si, Stack locals)
      {
         ProtocolDCC temp = deriveImplementation(BridgeUtilities.getObject(locals));

         if (temp instanceof Send)
            return SleepUtils.getScalar(((Send)temp).getFile().getName());

         if (temp instanceof Receive)
            return SleepUtils.getScalar(((Receive)temp).getFile().getName());
 
         return SleepUtils.getEmptyScalar();
      }
   }   

   private class B_getFilePath implements Function
   {
      public Scalar evaluate(String f, ScriptInstance si, Stack locals)
      {
         ProtocolDCC temp = deriveImplementation(BridgeUtilities.getObject(locals));

         if (temp instanceof Send)
            return SleepUtils.getScalar(((Send)temp).getFile().getAbsolutePath());

         if (temp instanceof Receive)
            return SleepUtils.getScalar(((Receive)temp).getFile().getAbsolutePath());
 
         return SleepUtils.getEmptyScalar();
      }
   }   

   private static ProtocolDCC deriveImplementation(Object temp)
   {
       if (temp instanceof GenericDCC)
           return ((GenericDCC)temp).getImplementation();
       
       if (temp instanceof ProtocolDCC)
           return (ProtocolDCC)temp;

       return null;
   }
}
