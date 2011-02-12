package rero.client.functions;

import sleep.engine.*;
import sleep.runtime.*;
import sleep.interfaces.*;
import sleep.bridges.BridgeUtilities;

import rero.client.*;
import rero.client.user.*;
import rero.client.output.*;
import rero.ircfw.data.*;
import rero.ircfw.*;

import rero.util.*;
import rero.config.*;

import java.util.*;

public class BuiltInOperators extends Feature implements Predicate, Function, Loadable
{
   protected InternalDataList data;
   protected UserHandler      commands;
 
   public void init()
   {
      getCapabilities().getScriptCore().addBridge(this);
      
      data     = (InternalDataList)getCapabilities().getDataStructure("clientInformation");
      commands = (UserHandler)getCapabilities().getDataStructure("commands");
   }

   public void scriptLoaded(ScriptInstance script)
   {
      String[] contents = new String[] { 
          "&call",
          "&mask",
          "&sendRaw",
          "&sendNotice",
          "&sendAction",
          "&sendMessage",
          "&sendReply",
          "&sendRequest",
          "&nickComplete",
          "&nickCompleteAll",
          "&echo",
          "&echoRaw",
          "&echoStatus",
          "&echoAll",
          "&setQuery",
          "&cycleQuery",
          "&processInput",
          "&openCommand",
          "&getSupportHints"
      };

      for (int x = 0; x < contents.length; x++)
      {
         script.getScriptEnvironment().getEnvironment().put(contents[x], this);
      }       

      script.getScriptEnvironment().getEnvironment().put("&say", new say());
      script.getScriptEnvironment().getEnvironment().put("&getAliasList", new getAliasList());
      script.getScriptEnvironment().getEnvironment().put("&parseSet", new parseSet());
      script.getScriptEnvironment().getEnvironment().put("&fireEvent", new fireEvent());
      script.getScriptEnvironment().getEnvironment().put("&echoColumns", new echoColumns());
   }

   private class echoColumns implements Function
   {
      public Scalar evaluate(String function, ScriptInstance si, Stack locals)
      {
          String window = BridgeUtilities.getString(locals, "");
          String text   = BridgeUtilities.getString(locals, "");
          double percen = BridgeUtilities.getDouble(locals, 1.0);

          getCapabilities().getUserInterface().printChunk(window, text.replace('\t', ' '), text.split("\t"), percen);
          return SleepUtils.getEmptyScalar();
      }
   }


   private class getAliasList implements Function
   {
      public Scalar evaluate(String function, ScriptInstance si, Stack parms)
      {
          LinkedList rv = new LinkedList();
          rv.addAll(rero.dialogs.HelpWindow.getBuiltInAliases());
          rv.addAll(commands.getScriptedAliases());

          return SleepUtils.getArrayWrapper(rv);
      }
   }

   private class parseSet implements Function
   {
      public Scalar evaluate(String name, ScriptInstance si, Stack parms)
      {
         String set    = BridgeUtilities.getString(parms, "UNKNOWN_SET");

         if (!parms.isEmpty() && ((Scalar)parms.peek()).getHash() != null)
         {
            ScalarHash hashish = ((Scalar)parms.pop()).getHash();

            HashMap temp = new HashMap();
            Iterator i = hashish.keys().scalarIterator();
            while (i.hasNext())
            {
               Scalar key = (Scalar)i.next();
               temp.put(key.toString(), hashish.getAt(key).toString());
            }
            
            return SleepUtils.getScalar(getCapabilities().getOutputCapabilities().parseSet(temp, set));
         }
         else
         {
            String target = BridgeUtilities.getString(parms, "<unknown>");
            String data   = BridgeUtilities.getString(parms, "");

            return SleepUtils.getScalar(getCapabilities().getOutputCapabilities().parseSet(ClientUtils.getEventHashMap(target, data), set));
         }
      }
   }

   private class fireEvent implements Function
   {
      public Scalar evaluate(String fname, ScriptInstance si, Stack parms)
      {
         if (parms.size() == 1)
         {
            getCapabilities().injectEvent(BridgeUtilities.getString(parms, ""));
         }      
         else
         {
            String name = BridgeUtilities.getString(parms, "UNKNOWN_EVENT");
            Scalar temp = BridgeUtilities.getScalar(parms);

            if (temp.getHash() != null)
            {
               HashMap eventData = new HashMap();
               eventData.put("$event", name);

               Iterator i = temp.getHash().keys().scalarIterator();
               while (i.hasNext())
               {
                  Scalar key = (Scalar)i.next();
                  eventData.put(key.toString(), temp.getHash().getAt(key).toString());
               }

               getCapabilities().dispatchEvent(eventData);
            }
         }

         return SleepUtils.getEmptyScalar();
      }
   }

   private class say implements Function
   {
      public Scalar evaluate(String name, ScriptInstance si, Stack parms)
      {
         getCapabilities().getChatCapabilities().sendMessage(getCapabilities().getUserInterface().getQuery(), parms.pop().toString());
         return SleepUtils.getEmptyScalar();
      }
   }

   public void scriptUnloaded(ScriptInstance script)
   {
   }

   public Scalar evaluate(String function, ScriptInstance script, Stack locals)
   {
      if (function.equals("&echo"))
      {
         switch (locals.size())
         {
            case 1:
              getCapabilities().getUserInterface().printActive(getString(locals));
              break;
            case 2:
              getCapabilities().getUserInterface().printNormal(getString(locals), getString(locals));
              break;
            case 3:
              int ck = BridgeUtilities.getInt(locals);
              getCapabilities().getOutputCapabilities().echoToTarget(getString(locals), getString(locals), ck == 2);
              break;
            default:
         }

         return SleepUtils.getEmptyScalar();
      }
      else if (function.equals("&openCommand") && locals.size() == 1)
      {
         try
         {
            Runtime.getRuntime().exec(ClientState.getClientState().getString("ui.openfiles", ClientDefaults.ui_openfiles) + " " + locals.pop().toString());
         }
         catch (Exception ex)
         {
            ex.printStackTrace();
         }
      }
      else if (function.equals("&echoStatus") && locals.size() == 1)
      {
         getCapabilities().getUserInterface().printStatus(getString(locals));
         
      }
      else if (function.equals("&echoRaw") && locals.size() == 2)
      {
         getCapabilities().getUserInterface().printRaw(BridgeUtilities.getString(locals, ""), BridgeUtilities.getString(locals, ""));
      }
      else if (function.equals("&echoAll") && locals.size() == 1)
      {
         getCapabilities().getUserInterface().printAll(getString(locals));
      }
      else if (function.equals("&setQuery") && locals.size() == 1)
      {
         getCapabilities().getUserInterface().setQuery(getString(locals));
      }
      else if (function.equals("&cycleQuery"))
      {
         getCapabilities().getOutputCapabilities().cycleQuery();
      }
      else if (function.equals("&sendRaw") && locals.size() == 1)
      {
         getCapabilities().sendln(getString(locals));
      }      
      else if (function.equals("&mask") && locals.size() == 2)
      {
         return SleepUtils.getScalar(ClientUtils.mask(getString(locals), getInt(locals)));
      }
      else if (function.equals("&call") && locals.size() == 1)
      {
         String temp = getString(locals);

         if (temp.charAt(0) != '/')
             temp = "/" + temp;

         commands.processCommand(temp);
      }
      else if (function.equals("&call") && locals.size() == 2)
      {
         String temp = getString(locals);

         if (temp.charAt(0) != '/')
             temp = "/" + temp;

         commands.processCommandBuiltIn(temp);
      }
      else if (function.equals("&processInput") && locals.size() == 1)
      {
         commands.processInput(getString(locals));
      }
      else if (function.equals("&nickComplete") && locals.size() == 2)
      {
         return SleepUtils.getScalar(data.nickComplete(getString(locals), getString(locals)));
      }
      else if (function.equals("&nickCompleteAll") && locals.size() == 2)
      {
         Scalar rv = SleepUtils.getArrayScalar();
      
         Iterator i = data.nickCompleteAll(getString(locals), getString(locals)).iterator();
         while (i.hasNext())
         {
            rv.getArray().push(SleepUtils.getScalar(i.next().toString()));
         }
         return rv; 
      }
      else if (function.substring(1, 5).equals("send") && locals.size() >= 2)
      {
         String target = getString(locals);
         String text   = getString(locals);
 
         if (function.equals("&sendNotice")) { getCapabilities().getChatCapabilities().sendNotice(target, text); }
         else if (function.equals("&sendMessage")) { getCapabilities().getChatCapabilities().sendMessage(target, text); }
         else if (function.equals("&sendAction")) { getCapabilities().getChatCapabilities().sendAction(target, text); }
         else if (function.equals("&sendReply")) { getCapabilities().getChatCapabilities().sendReply(target, text, getString(locals)); }
         else if (function.equals("&sendRequest")) { getCapabilities().getChatCapabilities().sendRequest(target, text, getString(locals)); }

         return SleepUtils.getEmptyScalar();
      }
      if (function.equals("&getSupportHints"))
      {
         return SleepUtils.getHashWrapper(data.getSupportInfo());         
      }
 
      return SleepUtils.getEmptyScalar();
   }

   private String getString(Stack l)
   {
      if (l.isEmpty())
         return "";

      return ((Scalar)l.pop()).getValue().toString();
   }

   private int getInt(Stack l)
   {
      if (l.isEmpty())
          return 0;

      return ((Scalar)l.pop()).getValue().intValue();
   }

   public boolean decide(String predicate, ScriptInstance script, Stack terms)
   {
      return false;
   }
}
