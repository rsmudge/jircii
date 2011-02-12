package rero.client.user;

import rero.client.*;
import rero.util.*;

import java.util.*;
import java.util.regex.*;

import rero.ircfw.*;

import rero.bridges.alias.*;

import rero.gui.*;
import rero.gui.windows.*;
import rero.gui.input.*;

public class UserHandler extends Feature implements rero.gui.input.InputListener
{
   protected static Pattern commandParametersPattern = Pattern.compile("/(\\w*)\\s(.*)");  // alias pattern is: /(\w*)\s(.*)

   protected AliasEnvironment scriptedAliases;
   protected HashMap          commands;
   protected BuiltInCommands  builtInCommands;

   protected InternalDataList ircData;

   public UserHandler()
   {
      commands = new HashMap();

      builtInCommands = new BuiltInCommands();
   }

   public Collection getScriptedAliases()
   {
      return scriptedAliases.getAliasList();
   }

   public void storeDataStructures(WeakHashMap data)
   {
      data.put("commands", this);
   }
 
   public void init()
   {
      scriptedAliases = (AliasEnvironment) (getCapabilities().getDataStructure("aliasBridge"));
      ircData = (InternalDataList) (getCapabilities().getDataStructure("clientInformation"));

      LinkedList temp = new LinkedList();
      getCapabilities().setupFeature(builtInCommands, temp);
      getCapabilities().finalizeFeatures(temp);
   }

   public boolean isNickComplete(String text)
   {
      int colonIndex = text.indexOf(':');
 
      return (colonIndex > -1 && colonIndex == (text.indexOf(' ') - 1) && ClientUtils.isChannel(getQuery()));
   }

   public void processNickCompletion(String text)
   {
      String pnick = text.substring(0, text.indexOf(':'));
      text = text.substring(pnick.length() + 1, text.length());  
 
      getCapabilities().getChatCapabilities().sendMessage(getQuery(), ircData.nickComplete(pnick, getQuery()) + ":" + text);
   }

   public String getQuery()
   {
      return getCapabilities().getUserInterface().getQuery();
   }

   public void onInput(UserInputEvent ev)
   {
      if (ircData.getMyUser() != null)
      {
         boolean wasIdle = ircData.getMyUser().isIdle();

         ircData.getMyUser().touch();

         if (wasIdle)
         {
            getCapabilities().getUserInterface().notifyWindow(getQuery());
         }
      }

      if (!ev.isConsumed() && ev.text.length() > 0)
      {
         processInput(ev.text);
      }
   }

   public void processInput(String text)
   {
      if (text.charAt(0) == '/')
      {
         processCommand(text);
      }
      else if (isNickComplete(text))
      {
         processNickCompletion(text);
      }
      else
      {
         if (getQuery().length() > 0 && (getCapabilities().isConnected() || getQuery().charAt(0) == '='))
            getCapabilities().getChatCapabilities().sendMessage(getQuery(), text);
      }      
   }

   /** this is an internal API so there is no effort to remember "prior" commands.  It is assumed that the command being 
       registered doesn't exist otherwise */
   public void registerCommand(String name, ClientCommand command)
   {
      commands.put(name, command);
   }

   public void runAlias(String command, String parameters)
   {
      command = command.toUpperCase();

      if (scriptedAliases.isAlias(command))
      {
          ScriptAlias runme = scriptedAliases.getAlias(command);
          runme.runAlias(command, parameters);
          return;
      }

      runAliasBuiltIn(command, parameters);
   }

   public void runAliasBuiltIn(String command, String parameters)
   {
      if (commands.get(command) != null)
      {
          ClientCommand runme = (ClientCommand)commands.get(command);
          runme.runAlias(command, parameters);
          return;
      }

      builtInCommands.runAlias(command, parameters);
   }

   public void processCommand(String text)
   {
      String command, parms;

      text = text.trim(); // just to be safe, you never know with user input.

      StringParser parser = new StringParser(text, commandParametersPattern);

      if (parser.matches())
      {
          command = parser.getParsedString(0);
          parms   = parser.getParsedString(1);        
      }
      else
      {
          command = text.substring(1, text.length());
          parms   = "";
      }

      runAlias(command, parms);
   }

   public void processCommandBuiltIn(String text)
   {
      String command, parms;

      text = text.trim(); // just to be safe, you never know with user input.

      StringParser parser = new StringParser(text, commandParametersPattern);

      if (parser.matches())
      {
          command = parser.getParsedString(0);
          parms   = parser.getParsedString(1);        
      }
      else
      {
          command = text.substring(1, text.length());
          parms   = "";
      }

      runAliasBuiltIn(command, parms);
   }
}
