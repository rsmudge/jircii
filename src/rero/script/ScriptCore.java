package rero.script;

import sleep.runtime.*;
import sleep.engine.*;

import sleep.error.*;
import sleep.parser.*;

import sleep.interfaces.*;

import java.util.*;

import rero.ircfw.*;
import rero.bridges.*;

import rero.client.DataStructures;

import rero.config.*;
import text.*; // for the burco constants

import java.io.*;

public class ScriptCore implements Loadable
{
   protected ScriptLoader    scriptLoader;  /* script loader baby! */
   protected Hashtable       environment;   /* shared script environment */
   protected ScriptVariables variables;     /* variables shared amongst loaded scripts */
   protected GlobalVariables globalData;    /* global variable data */

   protected BridgeKeeper    bridges;       /* all of our nifty bridges ... */

   protected static Scalar   GLOBAL_HASH;   /* a global hash for all... :) */

   public ScriptCore()
   {
      if (GLOBAL_HASH == null)
      {
         GLOBAL_HASH = SleepUtils.getHashScalar();
      }

      scriptLoader  = new ScriptLoader();
      environment   = new Hashtable();
      globalData    = new GlobalVariables();
      globalData.putScalar("%GLOBAL", GLOBAL_HASH);

      variables     = new ScriptVariables(globalData);

      bridges       = new BridgeKeeper();

      scriptLoader.setCharsetConversion(false); // tell sleep to NOT convert characters inside of a script file
      scriptLoader.setGlobalCache(true); // tell sleep to globally cache all script blocks...  makes loading/reloading quicker for multiple servers + it saves memory :)
      scriptLoader.addSpecificBridge(this);

      CodeGenerator.installEscapeConstant('B', AttributedString.bold+"");
      CodeGenerator.installEscapeConstant('U', AttributedString.underline+"");
      CodeGenerator.installEscapeConstant('R', AttributedString.reverse+"");
      CodeGenerator.installEscapeConstant('C', AttributedString.color+"");
      CodeGenerator.installEscapeConstant('O', AttributedString.cancel+"");

      CodeGenerator.installEscapeConstant('b', AttributedString.bold+"");
      CodeGenerator.installEscapeConstant('u', AttributedString.underline+"");
      CodeGenerator.installEscapeConstant('r', AttributedString.reverse+"");
      CodeGenerator.installEscapeConstant('c', AttributedString.color+"");
      CodeGenerator.installEscapeConstant('o', AttributedString.cancel+"");

      SleepUtils.addKeyword("wait");  // register certain scripting keywords with the parser
      SleepUtils.addKeyword("on");
      SleepUtils.addKeyword("alias");
      SleepUtils.addKeyword("bind");

      Loadable blist[] = bridges.getScriptBridges();
      for (int x = 0; x < blist.length; x++)
      {
         addBridge(blist[x]);
      }
   }

   // === Process Imports ===================================================================================

   public void announceFramework(ChatFramework ircfw)
   {
      bridges.announceFramework(ircfw);
   }

   public void addBridge(Loadable l)
   {
      scriptLoader.addGlobalBridge(l);
   }

   // === Export Data Structures ============================================================================

   public void storeDataStructures(WeakHashMap centralDataRepository)
   {
      centralDataRepository.put("scriptVariables", variables);
      centralDataRepository.put("globalVariables", globalData);

      centralDataRepository.put(DataStructures.ScriptLoader, scriptLoader);
      centralDataRepository.put(DataStructures.SharedEnv,    environment);

      bridges.storeDataStructures(centralDataRepository);
   }

   // === Export Capabilities ===============================================================================

   public Scalar callFunction(String function, Stack parameters)
   {
       if (function.charAt(0) != '&')
       {
          function = '&' + function;
       }

       ScriptInstance si = (ScriptInstance)scriptLoader.getScripts().getFirst();

       return si.callFunction(function, parameters);
   }

   /** convienence function for running some code and installing some local variables */
   public static void runCode(ScriptInstance owner, Block code, HashMap locals)
   {
       synchronized (owner.getScriptVariables())
       {
          ScriptVariables vars = owner.getScriptVariables();

          vars.pushLocalLevel();

          LocalVariables localLevel = (LocalVariables)vars.getLocalVariables();

          if (locals != null)
          {
              localLevel.setDataSource(locals);
          }

          //
          // execute the block of code
          //
          SleepUtils.runCode(code, owner.getScriptEnvironment());

          vars.popLocalLevel();
       }
   }

   // === Implement Interfaces ==============================================================================

   public void scriptLoaded(ScriptInstance si)
   {
      si.setScriptVariables(variables);
      ClientState.getClientState().fireChange("loaded.scripts", si.getName());
   }
   
   public void scriptUnloaded(ScriptInstance si)
   {
      ClientState.getClientState().fireChange("loaded.scripts", si.getName());
   }
}
