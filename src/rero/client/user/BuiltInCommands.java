package rero.client.user;

import rero.client.*;
import rero.client.server.*;
import rero.client.user.*;
import rero.client.output.*;

import rero.client.notify.*;
import rero.client.script.*;

import rero.dialogs.*;
import java.io.*;

import rero.net.*;
import java.net.*;

import rero.config.*;

import rero.ircfw.*;
import rero.ircfw.interfaces.*;

import rero.util.*;

import java.util.*;

import text.AttributedString;

import rero.gui.*;

public class BuiltInCommands extends Feature implements ClientCommand
{
   //
   // the numbers associated with each command represent the commands hashcode, if sun ever redefines how it calculates
   // hash codes for strings then I am in a world of hurt...   I may make some sort of ant process for generating these
   // later...
   //
   public static final int AME        = 64921;          // implemented
   public static final int AWAY       = 2022126;        // implemented
   public static final int BACK       = 2030823;        // implemented
   public static final int BANSTAT    = 381185731;      // implemented
   public static final int CHAT       = 2067288;        // implemented within DCC framework
   public static final int CLEAR      = 64208429;       // implemented
   public static final int CLEARALL   = 1572926516;     // implemented
   public static final int CLOAK      = 64218032;
   public static final int CLS        = 66826;          // implemented
   public static final int CPING      = 64331829;       // implemented
   public static final int CTCP       = 2078878;        // implemented
   public static final int CREPLY     = 1996016743;     // implemented
   public static final int CYCLE      = 64594118;       // implemented
   public static final int DEOP       = 2094626;        // implemented
   public static final int DESCRIBE   = 1800840907;     // implemented
   public static final int DEVOICE    = -2016999119;    // implemented
   public static final int DEBUG      = 64921139;       // implemented
   public static final int DH         = 2180;           // implemented
   public static final int DNS        = 67849;          // implemented
   public static final int DO         = 2187;           // implemented
   public static final int DOP        = 67877;          // implemented
   public static final int DV         = 2194;           // implemented
   public static final int EVAL       = 2140316;	// implemented
   public static final int EXEC       = 2142353;        // implemented
   public static final int EXIT       = 2142494;        // implemented
   public static final int HELP       = 2213697;        // implemented
   public static final int HO         = 2311;           // implemented
   public static final int HOP        = 71721;          // implemented
   public static final int IGNORE     = -2137067054;    // implemented
   public static final int INVITE     = -2130369783;    // implemented
   public static final int J          = 74;             // implemented
   public static final int JOIN       = 2282794;        // implemented - add key feature though
   public static final int K          = 75;             // implemented
   public static final int KB         = 2391;           // implemented
   public static final int KICK       = 2306630;        // implemented
   public static final int KILL       = 2306910;        // implemented
   public static final int L          = 76;             // implemented
   public static final int LEAVE      = 72308375;       // implemented
   public static final int LIST       = 2336926;        // implemented
   public static final int LL         = 2432;
   public static final int LOAD       = 2342118;        // implemented
   public static final int M          = 77;             // implemented
   public static final int ME         = 2456;           // implemented
   public static final int MODE       = 2372003;        // implemented
   public static final int MSG        = 76641;          // implemented
   public static final int MSGLOG     = -2011679709;                         // later - when away features added, maybe?
   public static final int N          = 78;             // implemented
   public static final int NEWSERVER  = -1201786685;    // implemented
   public static final int NOTICE     = -1986360616;    // implemented
   public static final int NOTIFY     = -1986360503;    // implemented in default script
   public static final int O          = 79;              // implemented
   public static final int OP         = 2529;            // implemented
   public static final int OV         = 2535;                                // later iff I decide to implement this command
   public static final int P          = 80;              // implemented
   public static final int PA         = 2545;            // implemented
   public static final int PART       = 2448371;         // implemented
   public static final int PING       = 2455922;         // implemented
   public static final int QUERY      = 77406376;        // implemented
   public static final int QUIT       = 2497103;         // implemented
   public static final int QUOTE      = 77416028;        // implemented
   public static final int RAW        = 80904;           // implemented
   public static final int REDIR      = 77851994;
   public static final int RELOAD     = -1881311847;     // implemented
   public static final int RUN        = 81515;           // implemented
   public static final int SC         = 2640;            // implemented
   public static final int SEND       = 2541448;         // implemented within DCC framework
   public static final int SERVER     = -1852497085;     // implemented
   public static final int ST         = 2657;            // implemented
   public static final int UT         = 2719;            // implemented
   public static final int SM         = 2650;            // implemeneted
   public static final int SV         = 2659;            // implemented
   public static final int THEME      = 79789481;        // implemented
   public static final int TOPIC      = 80008463;        // implemented
   public static final int TSEND      = 80117212;
   public static final int UNBAN      = 80888502;        // implemented
   public static final int UNIGNORE   = 478733739;       // implemented
   public static final int UNLOAD     = -1787112705;     // implemented
   public static final int V          = 86;              // implemented
   public static final int VER        = 84867;           // implemented
   public static final int VOICE      = 81848594;        // implemented
   public static final int WALL       = 2656714;         // implemented
   public static final int WALLEX     = -1741862915;     // implemented
   public static final int WALLOPS    = 1836833928;      // implemented
   public static final int WHOIS      = 82569544;        // implemented
   public static final int WHOLEFT    = 2039998629;
   public static final int WI         = 2770;            // implemented
   public static final int WII        = 85943;           // implemented
   public static final int WINDOW     = -1734422544;     // implemented
   public static final int WW         = 2784;            // implemented
   public static final int LAGC       = 2328849;         // debug

   UICapabilities gui;
   ChatCapabilities chatCommands;
   InternalDataList ircData;

   public void init()
   {
      gui = getCapabilities().getUserInterface();
      chatCommands = getCapabilities().getChatCapabilities();
      ircData = (InternalDataList)getCapabilities().getDataStructure("clientInformation");
   }

   public void runAlias(String command, String parms)
   {
      command = command.toUpperCase();

      TokenizedString tokens = new TokenizedString(parms);
      tokens.tokenize(" ");

      String target, nick, channel, temp;

      switch (command.hashCode())
      {
         case AME:
            Iterator i = ircData.getMyUser().getChannels().iterator();
            while (i.hasNext())
            {
               chatCommands.sendAction(((Channel)i.next()).getName(), parms);
            }
            break;
         case AWAY:
            getCapabilities().sendln("AWAY :" + parms);
            break;
         case BACK:
            getCapabilities().sendln("AWAY");
            break;
         case BANSTAT:
            target = gui.getQuery();
            if (parms.length() > 0)
            {
               target = parms;
            }
            getCapabilities().sendln("MODE " + target + " +b");
            break;
         case CLS:
         case CLEAR:
            getCapabilities().getUserInterface().clearScreen(parms);
            break;
         case CLEARALL:
            getCapabilities().getUserInterface().clearScreen("%ALL%");
            System.gc();
            break;
         case CREPLY:
            getCapabilities().getChatCapabilities().sendReply(tokens.getToken(0), tokens.getToken(1), tokens.getTokenFrom(2));
            break;
         case CTCP:
            if (tokens.getToken(1).equals("KOW1"))
            {
               getCapabilities().getGlobalCapabilities().showCoolAbout();
               return;
            }

            getCapabilities().getChatCapabilities().sendRequest(tokens.getToken(0), tokens.getToken(1), tokens.getTokenFrom(2));
            break;
         case DEBUG:
            ((ScriptManager)getCapabilities().getDataStructure(DataStructures.ScriptManager)).setDebug(tokens.getToken(0), tokens.getTokenFrom(1));
            break;
         case DO:
         case DOP:
         case DEOP:
            temp = " -";
            target = "";
            for (int x = 0; x < tokens.getTotalTokens(); x++)
            {
               target = ircData.nickComplete(tokens.getToken(x), gui.getQuery()) + " " + target;
               temp   = temp + "o";
            }            
            getCapabilities().sendln("MODE " + gui.getQuery() + temp + " " + target.trim());
            break;
         case DV:
         case DEVOICE:
            temp = " -";
            target = "";
            for (int x = 0; x < tokens.getTotalTokens(); x++)
            {
               target = ircData.nickComplete(tokens.getToken(x), gui.getQuery()) + " " + target;
               temp   = temp + "v";
            }            
            getCapabilities().sendln("MODE " + gui.getQuery() + temp + " " + target);
            break;
         case DH:
            temp = " -";
            target = "";
            for (int x = 0; x < tokens.getTotalTokens(); x++)
            {
               target = ircData.nickComplete(tokens.getToken(x), gui.getQuery()) + " " + target;
               temp   = temp + "h";
            }            
            getCapabilities().sendln("MODE " + gui.getQuery() + temp + " " + target);
            break;
         case DESCRIBE:
            chatCommands.sendAction(tokens.getToken(0), tokens.getTokenFrom(1));
            break;
         case DNS:
            User userD = ircData.getUser(parms);
            if (userD != null && userD.getAddress().length() > 0)
            {
               new Thread(new ResolveHost(userD.getAddress().substring(userD.getAddress().indexOf('@') + 1, userD.getAddress().length()))).start();
            }
            else
            {             
               new Thread(new ResolveHost(parms)).start();
            }
            break;
         case EXEC:
         case RUN:
            Thread athread = new Thread(new QuickProcess(parms));
            athread.start();
            break;
         case EVAL:
            ((ScriptManager)getCapabilities().getDataStructure(DataStructures.ScriptManager)).evalScript(parms);
            break;
         case EXIT:
            getCapabilities().getGlobalCapabilities().QuitClient();
            break;
         case HELP:
            getCapabilities().getGlobalCapabilities().showHelpDialog(parms);
            break;
         case HO:
            temp = " +";
            target = "";
            for (int x = 0; x < tokens.getTotalTokens(); x++)
            {
               target = ircData.nickComplete(tokens.getToken(x), gui.getQuery()) + " " + target;
               temp   = temp + "h";
            }            
            getCapabilities().sendln("MODE " + gui.getQuery() + temp + " " + target);
            break;
         case HOP:
         case CYCLE:
            target = gui.getQuery();

            String keych = "";
            if (ircData.getChannel(target).getKey() != null && ircData.getChannel(target).getKey().length() > 0)
            {
               keych = " " + ircData.getChannel(target).getKey();
            }

            getCapabilities().sendln("PART " + target);
            getCapabilities().sendln("JOIN " + target + keych);
            break;
         case IGNORE:
            if (parms.length() < 1) { break; }

            if (! StringUtils.iswm("*!*@*", parms))
            {
               parms = parms + "!*@*";
            }

            StringList templ = ClientState.getClientState().getStringList("ignore.masks");
            templ.add(parms);           
            templ.save();

            getCapabilities().getOutputCapabilities().fireSetActive(ClientUtils.getEventHashMap("add", parms), "SET_IGNORE");
            break;
         case INVITE:
            if (parms.length() < 1) { break; }

            target = gui.getQuery();
        
            if (tokens.getTotalTokens() > 1)
            {
               target = tokens.getToken(1);
            }
             
            getCapabilities().sendln("INVITE " + tokens.getToken(0) + " " + target);
            break;
         case J:
         case JOIN:
            if (!ClientUtils.isChannel(parms))
            {
               parms = "#" + parms;
            }
            if (ircData.getChannel(parms) != null && ircData.isOn(ircData.getMyUser(), ircData.getChannel(parms)))
            {
               getCapabilities().getUserInterface().setQuery(parms);
            }
            else
            { 
               getCapabilities().sendln("JOIN " + parms);
            }
            break;
         case K:
            temp   = ClientState.getClientState().getString("kick.message", "I know... I'm a \002jIRC\002");
            target = ircData.nickComplete(tokens.getToken(0), gui.getQuery());
        
            if (tokens.getTotalTokens() > 1)
            {
               temp = tokens.getTokenFrom(1);
            }

            getCapabilities().sendln("KICK " + gui.getQuery() + " " + target + " :" + temp);
            break;
         case KB:
            temp   = ClientState.getClientState().getString("kick.message", "I know... I'm a \002jIRC\002");
            target = ircData.nickComplete(tokens.getToken(0), gui.getQuery());
        
            if (tokens.getTotalTokens() > 1)
            {
               temp = tokens.getTokenFrom(1);
            }

            User user = ircData.getUser(target);
            if (user != null && user.getAddress().length() > 0)
            {
               getCapabilities().sendln("MODE " + gui.getQuery() + " -o+b " + target + " " + ClientUtils.mask(user.getNick()+"!"+user.getAddress(), 2));
               getCapabilities().sendln("KICK " + gui.getQuery() + " " + target + " :" + temp);
            }    
            else
            {
               getCapabilities().sendln("MODE " + gui.getQuery() + " -o+b " + target + " " + target+"!*@*");
               getCapabilities().sendln("KICK " + gui.getQuery() + " " + target + " :" + temp);
            }
 
            break;     
         case KICK:
            temp   = ClientState.getClientState().getString("kick.message", "I know... I'm a \002jIRC\002");
        
            if (tokens.getTotalTokens() > 2)
            {
               temp = tokens.getTokenFrom(2);
            }

            getCapabilities().sendln("KICK " + tokens.getToken(0) + " " + tokens.getToken(1) + " :" + temp);
            break;
         case KILL:
            temp = ClientState.getClientState().getString("kill.message", "I know... I'm a \002jIRC\002");

            if (tokens.getTotalTokens() > 1)
            {
               temp = tokens.getTokenFrom(1);
            }

            getCapabilities().sendln("KILL " + tokens.getToken(0) + " :" + temp);
            break;
         case LIST:
            if (tokens.getTotalTokens() == 1)
            {
               if (tokens.getToken(0).equals("-gui"))
               {
                  gui.openListWindow();
               }
               else
               {
                  getCapabilities().addTemporaryListener(new ListFilter(tokens.getToken(0)));
               }

               getCapabilities().sendln("LIST");
            }
            else if (tokens.getTotalTokens() > 1)
            {
               getCapabilities().sendln("LIST :" + parms);
            }
            else
            {
               getCapabilities().sendln("LIST");
            }
            break;
         case LAGC:
            long freememory = Runtime.getRuntime().freeMemory();
            System.gc();
            freememory = Runtime.getRuntime().freeMemory() - freememory;

            System.out.println("Profiler Output:");
            System.out.println("<=================> ");
//            System.out.println("Attribted Strings : " + text.AttributedString.total_instances);

//            rero.util.ProfileTemp.enumerateThreads();

            System.out.println("Free'd Memory     : " + rero.util.ClientUtils.formatBytes(freememory));
            break;
         case LOAD:
            if (parms.length() == 0)
            {
               File tempf = DialogUtilities.showFileDialog("Select a script", "Load", null);
               if (tempf != null)
                 parms = tempf.getAbsolutePath();
            }

            if (parms == null) break;

            ((ScriptManager)getCapabilities().getDataStructure(DataStructures.ScriptManager)).addScript(parms);
            break;
         case M:
         case MSG:
            getCapabilities().getChatCapabilities().sendMessage(tokens.getToken(0), tokens.getTokenFrom(1));
            break;
         case ME:
            chatCommands.sendAction(gui.getQuery(), parms);
            break;
         case SM:
         case MODE:
            if (tokens.getTotalTokens() == 0)
            {
                getCapabilities().sendln("MODE " + gui.getQuery());
            }
            else if (tokens.getTotalTokens() == 1)
            {
                getCapabilities().sendln("MODE " + tokens.getToken(0));
            }
            else if (tokens.getTotalTokens() == 2)
            {
                getCapabilities().sendln("MODE " + tokens.getToken(0) + " " + tokens.getToken(1));
            }
            else
            {
                getCapabilities().sendln("MODE " + tokens.getToken(0) + " " + tokens.getToken(1) + " " + tokens.getTokenFrom(2));
            }
            break;
         case N:
         case NOTICE:
            getCapabilities().getChatCapabilities().sendNotice(tokens.getToken(0), tokens.getTokenFrom(1));
            break;
         case NOTIFY:
            if (tokens.getTotalTokens() <= 0)
               break;

            if (tokens.getTotalTokens() == 1)
            {
               ((NotifyData)getCapabilities().getDataStructure(DataStructures.NotifyData)).addUser(tokens.getToken(0));
               getCapabilities().getOutputCapabilities().fireSetActive(ClientUtils.getEventHashMap("add", tokens.getToken(0)), "SET_NOTIFY");
            }
            else if (tokens.getToken(0).equals("add") && tokens.getTotalTokens() == 2)
            {
               ((NotifyData)getCapabilities().getDataStructure(DataStructures.NotifyData)).addUser(tokens.getToken(1));
               getCapabilities().getOutputCapabilities().fireSetActive(ClientUtils.getEventHashMap("add", tokens.getToken(1)), "SET_NOTIFY");
            }
            else if ((tokens.getToken(0).equals("remove") || tokens.getToken(1).equals("rem")) && tokens.getTotalTokens() == 2)
            {
               ((NotifyData)getCapabilities().getDataStructure(DataStructures.NotifyData)).removeUser(tokens.getToken(1));
               getCapabilities().getOutputCapabilities().fireSetActive(ClientUtils.getEventHashMap("remove", tokens.getToken(1)), "SET_NOTIFY");
            }
            break;
         case NEWSERVER:
            getCapabilities().getGlobalCapabilities().createNewServer();

            if (parms.length() > 0)
               SessionManager.getGlobalCapabilities().getActiveSession().executeCommand("/server " + parms);

            break;
         case O:
         case OP:
            temp = " +";
            target = "";
            for (int x = 0; x < tokens.getTotalTokens(); x++)
            {
               target = ircData.nickComplete(tokens.getToken(x), gui.getQuery()) + " " + target;
               temp   = temp + "o";
            }            
            getCapabilities().sendln("MODE " + gui.getQuery() + temp + " " + target.trim());
            break;
         case PA:
            getCapabilities().sendln("JOIN 0");
            break;
         case L:
         case LEAVE:
         case PART:
            target = gui.getQuery();
            parms  = parms;
            if (parms.length() > 0 && ClientUtils.isChannel(tokens.getToken(0)))
            {
               target = tokens.getToken(0);
               parms  = tokens.getTokenFrom(1);
            }

            if (ClientState.getClientState().isOption("auto.part", ClientDefaults.auto_option) && gui.isWindow(target))
            {
               getCapabilities().getUserInterface().closeWindow(target);
            }
            else
            {
               getCapabilities().sendln("PART " + target + " :" + parms);
            }

            break;
         case P:
         case CPING:
         case PING:
            target = gui.getQuery();
            if (parms.length() > 0)
            {
               target = ircData.nickComplete(parms, gui.getQuery());
            }
            chatCommands.sendRequest(target, "PING", "");
            break;
         case QUERY:
            if (parms == null || parms.length() == 0)
            {
               Set mychs = ircData.getMyUser().getChannels();
               Iterator seti = mychs.iterator();
               
               StringBuffer rv = new StringBuffer();

               while (seti.hasNext())
               {
                  rv.append(((Channel)seti.next()).getName());
                  rv.append(" ");
               }

               getCapabilities().getOutputCapabilities().fireSetActive(ClientUtils.getEventHashMap("-", rv.toString()), "ON_CHANNELS");
            }
            else
            {
               getCapabilities().getUserInterface().setQuery(parms);
            }
            break;
         case QUOTE:
         case RAW:
            getCapabilities().sendln(parms);
            break;
         case QUIT:
            if (parms.length() == 0)
            {
               parms = ClientState.getClientState().getString("message.quit", ircData.getMyNick() + " has no reason");
            }

            if (getCapabilities().isConnected())
            {
               getCapabilities().sendln("QUIT :" + parms);
               ircData.reset();  // reset the data structures so we don't do an auto reconnect
               ((NotifyData)getCapabilities().getDataStructure(DataStructures.NotifyData)).reset();
            }
            break;
         case REDIR:
            break;
         case RELOAD:  // may be able to just "load" the script.
            ((ScriptManager)getCapabilities().getDataStructure(DataStructures.ScriptManager)).reloadScript(parms);
            break;
         case SC:
            target = gui.getQuery();
            if (parms.length() > 0)
            {
               target = parms;
            }
            getCapabilities().sendln("NAMES " + target);
            break;
         case SERVER:
            connectToServer(parms);
            break;
            
         // TODO: this command is redundant and should be removed unless
         // backwards compatability is an issue
         case ST:
            target = gui.getQuery();
            if (parms.length() > 0)
            {
               target = tokens.getToken(0);
            }
 
            getCapabilities().sendln("TOPIC " + target);
            break;
         case SV:
            chatCommands.sendMessage(gui.getQuery(), ClientUtils.ShowVersion());
            break;
         case THEME:
            ((ScriptManager)getCapabilities().getDataStructure(DataStructures.ScriptManager)).loadTheme(parms);
            break;
         case TOPIC:

            // Fetch possible target from currently active window
            target = gui.getQuery();
            
            // Check if there is a specified target parameter
            if (parms.length() > 0)
            {
               target = tokens.getToken(0);
            }
         
            // Check how many tokens there are
            if (tokens.getTotalTokens() > 1) {

                // Setting current topic
                getCapabilities().sendln("TOPIC " + target + " :" + tokens.getTokenFrom(1));
            }
            else {

                // Trying to fetch current topic
                getCapabilities().sendln("TOPIC " + target);                
            }
            break;

         // Unsets topic in the given channel
         case UT:
         
            // Fetch possible target from currently active window
            target = gui.getQuery();
            
            // Check if there is a specified target parameter
            if (parms.length() > 0)
            {
               target = tokens.getToken(0);
            }
 
            // Send the command to unset the topic
            getCapabilities().sendln("TOPIC " + target + " :");
            break;

         case UNBAN:
            if (parms.indexOf('!') > -1)
            {
               target = parms;
            }
            else
            {
               User luser = ircData.getUser(parms);

               if (luser != null && luser.getAddress().length() > 0)
               {
                  target = luser.getNick() + "!" + luser.getAddress();   
               }
               else
               {
                  target = parms + "!*@*";
               }
            }

            getCapabilities().addTemporaryListener(new UnbanHandler(target, gui.getQuery()));
            getCapabilities().sendln("MODE " + gui.getQuery() + " +b");
            break;
         case UNIGNORE:
            StringList templw = ClientState.getClientState().getStringList("ignore.masks");
            templw.remove(parms);           
            templw.save();

            getCapabilities().getOutputCapabilities().fireSetActive(ClientUtils.getEventHashMap("remove", parms), "SET_IGNORE");
            break;
         case UNLOAD:
            ((ScriptManager)getCapabilities().getDataStructure(DataStructures.ScriptManager)).removeScript(parms);
            break;
         case VER:
            target = gui.getQuery();
            if (parms.length() > 0)
            {
               target = ircData.nickComplete(parms, gui.getQuery());
            }
            chatCommands.sendRequest(target, "VERSION", "");
            break;
         case V:
         case VOICE:
            temp = " +";
            target = "";
            for (int x = 0; x < tokens.getTotalTokens(); x++)
            {
               target = ircData.nickComplete(tokens.getToken(x), gui.getQuery()) + " " + target;
               temp   = temp + "v";
            }            
            getCapabilities().sendln("MODE " + gui.getQuery() + temp + " " + target);
            break;
         case WALL:
            Set chops = ircData.getUsersWithMode(gui.getQuery(), 'o');
            chops.remove(ircData.getMyUser());
            String[] chopsZ = groupUsers(chops);
  
            for (int z = 0; z < chopsZ.length; z++)
            {
               getCapabilities().sendln("NOTICE " + chopsZ[z] + " :["+AttributedString.bold+"wall"+AttributedString.bold+"/" + gui.getQuery() + "]: " + parms);
            }

            getCapabilities().getOutputCapabilities().fireSetActive(ClientUtils.getEventHashMap(gui.getQuery(), parms), "SEND_WALL");

            break;
         case WALLEX:
            Set chops2 = new HashSet();
            chops2.addAll(ircData.getUsersWithMode(gui.getQuery(), 'o'));
            chops2.addAll(ircData.getUsersWithMode(gui.getQuery(), 'h'));
            chops2.addAll(ircData.getUsersWithMode(gui.getQuery(), 'v'));

            String[] removeUsers = tokens.getToken(0).split(",");
            for (int y = 0; y < removeUsers.length; y++)
            {
               if (removeUsers[y].equals("@"))
               {
                  chops2.removeAll(ircData.getUsersWithMode(gui.getQuery(), 'o'));
               }
               else if (removeUsers[y].equals("+"))
               {
                  chops2.removeAll(ircData.getUsersWithMode(gui.getQuery(), 'v'));
               }
               else if (removeUsers[y].equals("%"))
               {
                  chops2.removeAll(ircData.getUsersWithMode(gui.getQuery(), 'h'));
               }
               else
               {
                  removeUsers[y] = ircData.nickComplete(removeUsers[y], gui.getQuery());
                  chops2.remove(ircData.getUser(removeUsers[y]));            
               }
            }

            chops2.remove(ircData.getMyUser());

            String boozer = joinNicks(removeUsers);

            String[] chopsZZ = groupUsers(chops2);
 
            for (int z = 0; z < chopsZZ.length; z++)
            {
                getCapabilities().sendln("NOTICE " + chopsZZ[z] + " :["+AttributedString.bold+"wall-x"+AttributedString.bold+"/" + boozer  + "]" + tokens.getTokenFrom(1));
            }

            getCapabilities().getOutputCapabilities().fireSetActive(ClientUtils.getEventHashMap(boozer, tokens.getTokenFrom(1)), "SEND_WALLEX");

            break;
         case WALLOPS:
            getCapabilities().getOutputCapabilities().fireSetStatus(ClientUtils.getEventHashMap("<ircops>", parms), "SEND_WALLOPS");
            getCapabilities().sendln("WALLOPS :" + parms);
            break;
         case WHOIS:
         case WI:
            getCapabilities().sendln("WHOIS " + parms);
            break;
         case WII:
            getCapabilities().sendln("WHOIS " + parms + " " + parms);
            break;     
         case WINDOW: 
            if (parms.length() == 0)
                break;

            if (getCapabilities().getUserInterface().getQuery().toUpperCase().equals(parms.toUpperCase()))
            {
               getCapabilities().getOutputCapabilities().cycleQuery();
            }
            
            if (ClientUtils.isChannel(parms))
            {
               gui.openChannelWindow(ircData.getChannel(parms));
            }
            else
            {
               gui.openQueryWindow(parms, true);
            }

            break;
         case WW:
            getCapabilities().sendln("WHOWAS " + parms);
            break;
         default:
            getCapabilities().sendln(command + " " + parms);
      }
   }

   public void connectToServer(String parms)
   {
      boolean secure   = false;
      String  host     = "";
      int     port     = 6667;
      String  password = null;

      StringStack stack = new StringStack(parms);
 
      String temp = stack.pop();
      if (temp.equals("-ssl") || temp.equals("-s"))
      {
         secure = true;
         temp   = stack.pop();
      }

      if (temp.equals("-pass") || temp.equals("-p"))
      {
         password = stack.pop();
         temp     = stack.pop();
      }

      host = temp;

      if (!stack.isEmpty())
      {
         port = Integer.parseInt(stack.pop());
      }

      if (getCapabilities().isConnected())  // add some sort of check for isRegistered() as well.
      {
          ircData.reset();  // reset the data structures so we don't do an auto reconnect
          ((NotifyData)getCapabilities().getDataStructure(DataStructures.NotifyData)).reset();
          getCapabilities().sendln("QUIT :switching servers");
      }

      getCapabilities().getSocketConnection().connect(host, port, 0, password, secure);
      getCapabilities().getOutputCapabilities().fireSetStatus(ClientUtils.getEventHashMap(host, host + " " + port + " " + password + " " + secure), "IRC_ATTEMPT_CONNECT");
  }

   private String[] groupUsers(Set users)
   {
      StringBuffer rv = new StringBuffer();

      int x = 1;
      Iterator i = users.iterator();
      while (i.hasNext())
      {
         String temp = ((User)i.next()).getNick();
         rv.append(temp);
         if ((x % 4) == 0 && x > 1) { rv.append("="); }
         else { rv.append(","); }

         x++;
      }
    
      
      if (rv.toString().length() > 1)
      {
         return rv.toString().substring(0, rv.toString().length() - 1).split("=");
      }
      return new String[0];
   }

   private class QuickProcess implements Runnable
   {
      private Process process;
      private BufferedReader reader = null;
      private String command;

      public QuickProcess(String parms)
      {
         command = parms;
      }

      public void run()
      {
         try
         {
            process = Runtime.getRuntime().exec(command); 
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String data;

            while ((data = reader.readLine()) != null)
            {
               getCapabilities().getOutputCapabilities().fireSetActive(ClientUtils.getEventHashMap("process", data), "PROCESS_DATA");
            }
         }
         catch (Exception ex)
         {
            getCapabilities().getOutputCapabilities().fireSetActive(ClientUtils.getEventHashMap("error", ex.getMessage()), "PROCESS_DATA");
         }
      }
   }

   private static String joinNicks(String[] stuff)
   {
      StringBuffer temp = new StringBuffer();
      for (int x = 0; x < stuff.length; x++)
      {
         temp.append(stuff[x]);
         if ((x + 1) < stuff.length)
         {
            temp.append(",");
         }
      }

      return temp.toString();
   }

   private class ResolveHost implements Runnable
   {
      private String host;

      public ResolveHost(String _host)
      {
         host = _host;
      }

      public void run()
      {
         try
         {
            InetAddress info = InetAddress.getByName(host);

            HashMap eventDescription = new HashMap();
            eventDescription.put("$data", host + " " + info.getHostAddress() + " " + info.getHostName());
            eventDescription.put("$parms", info.getHostAddress() + " " + info.getHostName());
            getCapabilities().getOutputCapabilities().fireSetActive(eventDescription, "RESOLVED_HOST");
         }
         catch (UnknownHostException ex)
         {
            HashMap eventDescription = new HashMap();
            eventDescription.put("$data", host);
            eventDescription.put("$parms", "");
            getCapabilities().getOutputCapabilities().fireSetActive(eventDescription, "RESOLVED_HOST");
         }
      }
   }

   private class UnbanHandler implements ChatListener
   {
      protected String channel;
      protected String target;

      public UnbanHandler(String _target, String _channel)
      {
         target  = _target.toUpperCase();
         channel = _channel.toUpperCase();
      }

      public boolean isChatEvent(String event, HashMap eventId)
      {
         return event.equals("367") || event.equals("368");
      }

      public int fireChatEvent(HashMap eventDescription)
      {
         if (eventDescription.get("$event").toString().equals("368"))
             return ChatListener.EVENT_HALT | ChatListener.REMOVE_LISTENER;

         TokenizedString data = new TokenizedString((String)eventDescription.get("$parms"));
         data.tokenize(" ");

         if (data.getToken(0).toUpperCase().equals(channel)) 
         {
             if (StringUtils.iswm(data.getToken(1).toUpperCase(), target))
             {
                 getCapabilities().sendln("MODE " + channel + " -b " + data.getToken(1));
             }
         }

         return ChatListener.EVENT_HALT;
      }
   }
}
