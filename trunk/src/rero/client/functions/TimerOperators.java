package rero.client.functions;

import sleep.engine.*;
import sleep.runtime.*;
import sleep.interfaces.*;
import sleep.bridges.*;

import rero.client.*;
import rero.util.*;
import java.util.*;

public class TimerOperators extends Feature implements Loadable
{
   public void init()
   {
      getCapabilities().getScriptCore().addBridge(this);
   }

   public void scriptLoaded(ScriptInstance script)
   {
      script.getScriptEnvironment().getEnvironment().put("&addTimer", new addTimer());
      script.getScriptEnvironment().getEnvironment().put("&stopTimer", new stopTimer());
      script.getScriptEnvironment().getEnvironment().put("&setTimerResolution", new setResolution());
   }

   public void scriptUnloaded(ScriptInstance script)
   {
   }

   private class addTimer implements Function
   {
      public Scalar evaluate(String f, ScriptInstance si, Stack locals)
      {
         int repeats = -1;
         
         SleepClosure func = BridgeUtilities.getFunction(locals, si);
         int          time = BridgeUtilities.getInt(locals);

         if (!locals.isEmpty())
         {
            repeats = BridgeUtilities.getInt(locals);
         }

         ScriptedTimer timer;

         if (!locals.isEmpty())
         {
            timer = new ScriptedTimer(func, si, BridgeUtilities.getScalar(locals));
         }
         else
         {
            timer = new ScriptedTimer(func, si, null); 
         }

         getCapabilities().getTimer().addTimer(timer, time, repeats);

         return SleepUtils.getScalar(timer);
      }
   }

   private class stopTimer implements Function
   {
      public Scalar evaluate(String f, ScriptInstance si, Stack locals)
      {
         ScriptedTimer timer = (ScriptedTimer)BridgeUtilities.getObject(locals);

         getCapabilities().getTimer().stopTimer(timer);

         return SleepUtils.getEmptyScalar();
      }
   }

   private class setResolution implements Function
   {
      public Scalar evaluate(String f, ScriptInstance si, Stack locals)
      {
         getCapabilities().getTimer().setResolution((long)BridgeUtilities.getInt(locals));
         return SleepUtils.getEmptyScalar();
      }
   }

   private static class ScriptedTimer implements TimerListener
   {
      protected ScriptInstance si;
      protected SleepClosure   func;
      protected Scalar         args;

      public ScriptedTimer(SleepClosure f, ScriptInstance script, Scalar a)
      { 
         func   = f;
         si     = script;
         args   = a;
      }

      public void timerExecute()
      {
         if (si == null || !si.isLoaded())
         {
            args = null;
            si   = null;
            func = null;
            return;
         }

         Stack arg_stack = new Stack();
         if (args != null)
            arg_stack.push(args);

         func.callClosure("timer", si, arg_stack);
      }
   } 
}
