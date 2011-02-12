package rero.client.data;

import sleep.interfaces.*;
import sleep.engine.*;
import sleep.runtime.*;

import rero.client.*;
import rero.util.*;

import java.util.*;

import rero.ircfw.*;
import rero.ircfw.data.*;
import rero.net.*;
import rero.script.*;

import rero.client.notify.*;

public class DataStructureBridge extends Feature implements Variable
{
   protected InternalDataList data = null;

   public boolean scalarExists(String key)
   {
      if ("$time".equals(key)) { return true; }

      if (data == null) { return false; }
      if ("$me".equals(key)) { return true; }
      if ("$lag".equals(key)) { return true; }
      if ("$active".equals(key)) { return true; }
//      if ("$myserver".equals(key)) { return true; }
//      if ("$myserverport".equals(key)) { return true; }
      if ("$mymode".equals(key)) { return true; }

      return false;
   }

   public Scalar putScalar(String key, Scalar data) { return null; }
   public void removeScalar(String key) { }
   public Variable createLocalVariableContainer() { return null; }
   public Variable createInternalVariableContainer() { return null; }

   public Scalar getScalar(String key)
   {
      if ("$me".equals(key)) { return SleepUtils.getScalar(data.getMyNick()); }
      if ("$time".equals(key)) { return SleepUtils.getScalar(ClientUtils.TimeStamp()); }
      if ("$lag".equals(key) && getCapabilities().getDataStructure("lag") != null) { return SleepUtils.getScalar(((Lag)getCapabilities().getDataStructure("lag")).getLag()); }
      if ("$active".equals(key)) { return SleepUtils.getScalar(getCapabilities().getUserInterface().getQuery()); }
//      if ("$myserver".equals(key)) { return SleepUtils.getScalar(getCapabilities().getSocketConnection().getSocketInformation().hostname); }
//      if ("$myserverport".equals(key)) { return SleepUtils.getScalar(getCapabilities().getSocketConnection().getSocketInformation().port); }
      if ("$mymode".equals(key)) { return SleepUtils.getScalar(data.getMyUserInformation().getMode().toString()); }
      return SleepUtils.getEmptyScalar();
   }

   public void init()
   {
      data = (InternalDataList)getCapabilities().getDataStructure("clientInformation");

      GlobalVariables temp = (GlobalVariables)getCapabilities().getDataStructure("globalVariables");
      temp.setOtherVariables(this);
   }
}
