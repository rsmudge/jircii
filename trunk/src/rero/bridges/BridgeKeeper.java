package rero.bridges;

import java.util.*;

import rero.ircfw.*;
import rero.script.*;
import rero.bridges.event.*;
import rero.bridges.alias.*;
import rero.bridges.set.*;
import rero.bridges.bind.*;
import rero.bridges.menu.*;
import rero.bridges.subs.*;

import sleep.interfaces.*;
import sleep.runtime.*;

public class BridgeKeeper
{
   protected Loadable   scriptBridges[];

   public BridgeKeeper()
   {
      scriptBridges    = new Loadable[6];
      scriptBridges[0] = new EventBridge();
      scriptBridges[1] = new AliasEnvironment();
      scriptBridges[2] = new SetEnvironment();
      scriptBridges[3] = new BindEnvironment();
      scriptBridges[4] = new MenuBridge();
      scriptBridges[5] = new SubroutineEnvironment();
   }

   // === Process Imports ===================================================================================

   public void announceFramework(ChatFramework ircfw)
   {
       ((EventBridge)scriptBridges[0]).announceFramework(ircfw);
   }

   // === Export Data Structures ============================================================================

   public void storeDataStructures(WeakHashMap centralDataRepository)
   {
       centralDataRepository.put("eventBridge", scriptBridges[0]);
       centralDataRepository.put("aliasBridge", scriptBridges[1]);
       centralDataRepository.put("setBridge",   scriptBridges[2]);
       centralDataRepository.put("bindBridge",  scriptBridges[3]);
       centralDataRepository.put("menuBridge",  scriptBridges[4]);
   }

   // === Export Capabilities ===============================================================================

   public Loadable[] getScriptBridges()
   {
       return scriptBridges;
   }
}
