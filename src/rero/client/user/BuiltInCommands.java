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
   public static final int DEHOP      = 64926728;	// implemented
   public static final int DNS        = 67849;          // implemented
   public static final int DO         = 2187;           // implemented
   public static final int DOP        = 67877;          // implemented
   public static final int DV         = 2194;           // implemented
   public static final int EVAL       = 2140316;	// implemented
   public static final int EXEC       = 2142353;        // implemented
   public static final int EXIT       = 2142494;        // implemented
   public static final int HELP       = 2213697;        // implemented
   public static final int HALFOP     = 2123661652;
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
   public static final int NAMES      = 74047272;	// implemented
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
   public static final int UNTOP      = 80906236;	 // implemented
   public static final int UT         = 2719;            // implemented
   public static final int SM         = 2650;            // implemeneted
   public static final int SPING      = 79108165;
   public static final int SV         = 2659;            // implemented
   public static final int THEME      = 79789481;        // implemented
   public static final int TOPIC      = 80008463;        // implemented
   public static final int TSEND      = 80117212;
   public static final int UMODE      = 80871288;	 // implemented
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

      ArrayList parametersArray = parmsToArrayList(parms, true);	// No empty parameters included (no whitespace-only parameters)
      ArrayList parametersArrayFull = parmsToArrayList(parms, false);	// Includes empty parameters (parameters can have whitespace)

      String target, temp;
      String channel = null;

      // If the current window is a channel, set channel variable
      if (ircData.isChannel(gui.getQuery()))
	      channel = gui.getQuery();

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
	    if (parms.trim().length() > 0)
		    target = parms.trim();
	    else if (channel != null)
		    target = channel;
	    else {
		    gui.printActive("BANSTAT: error: must be in or specify a channel.");
		    break;
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
            boolean debugSuccess = ((ScriptManager)getCapabilities().getDataStructure(DataStructures.ScriptManager)).setDebug(tokens.getToken(0), tokens.getTokenFrom(1));
            break;
         case DO:
         case DOP:
         case DEOP:
	    {
	    ArrayList modesToPush;
	    Iterator modei;

	    modesToPush = createHomogenousChannelModesList(gui.getQuery(), "-", "o", parametersArray);

	    if (modesToPush == null)
		    break;

	    modei = modesToPush.listIterator();

	    while (modei.hasNext())
            	getCapabilities().sendln(modei.next().toString());
	    }
            break;
         case DV:
         case DEVOICE:
	    {
	    ArrayList modesToPush;
	    Iterator modei;

	    modesToPush = createHomogenousChannelModesList(gui.getQuery(), "-", "v", parametersArray);

	    if (modesToPush == null)
		    break;

	    modei = modesToPush.listIterator();

	    while (modei.hasNext())
            	getCapabilities().sendln(modei.next().toString());
	    }
            break;
         case DH:
	 case DEHOP:
	    {
	    ArrayList modesToPush;
	    Iterator modei;

	    modesToPush = createHomogenousChannelModesList(gui.getQuery(), "-", "h", parametersArray);

	    if (modesToPush == null)
		    break;

	    modei = modesToPush.listIterator();

	    while (modei.hasNext())
            	getCapabilities().sendln(modei.next().toString());
	    }
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
	 case HALFOP:
	    {
	    ArrayList modesToPush;
	    Iterator modei;

	    modesToPush = createHomogenousChannelModesList(gui.getQuery(), "+", "h", parametersArray);

	    if (modesToPush == null)
		    break;

	    modei = modesToPush.listIterator();

	    while (modei.hasNext())
            	getCapabilities().sendln(modei.next().toString());
	    }
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
            if (!ircData.isChannel(parms))
            {
               parms = "#" + parms;
            }
            if (ircData.getChannel(parms) != null && ircData.isOn(ircData.getMyUser(), ircData.getChannel(parms)))
            {
               gui.setQuery(parms);
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
               if (tokens.getToken(0).toLowerCase().equals("-gui")) {
                  gui.openListWindow();
               }
               else {
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
	 case NAMES:
	    if (channel == null) {
		    // Protect a user from himself; this will flood him off.
		    gui.printActive("/NAMES: if you really want to see a server-wide /NAMES, type /NAMES -yes");
	    } else if (parms.trim().toLowerCase().equals("-yes")) {
		    getCapabilities().sendln("NAMES");
	    } else {
		    getCapabilities().sendln("NAMES " + channel);
	    }

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
	    {
	    ArrayList modesToPush;
	    Iterator modei;

	    modesToPush = createHomogenousChannelModesList(gui.getQuery(), "+", "o", parametersArray);

	    if (modesToPush == null)
		    break;

	    modei = modesToPush.listIterator();

	    while (modei.hasNext())
            	getCapabilities().sendln(modei.next().toString());
	    }

            break;
         case PA:
            getCapabilities().sendln("JOIN 0");
            break;
         case L:
         case LEAVE:
         case PART:
            target = gui.getQuery();
            parms  = parms;
            if (parms.length() > 0 && ircData.isChannel(tokens.getToken(0)))
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
               parms = ClientState.getClientState().getString("message.quit", "jIRCii - http://www.oldschoolirc.com");
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
	 case UMODE:
	    if (parms.length() == 0)
	        getCapabilities().sendln("MODE " + ircData.getMyNick());
	    else
	    	getCapabilities().sendln("MODE " + ircData.getMyNick() + " " + tokens.getToken(0));

	    break;
         // Unsets topic in the given channel
         case UT:
         case UNTOP:
	    if (parms.length() == 0 && channel != null)
		    target = channel;
	    else if (parms.length() > 0 && ircData.isChannel(tokens.getToken(0)))
		    target = tokens.getToken(0);
	    else
	    	break;
 
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
	    {
	    boolean sSilent = false;

	    if (parms.length() == 0)
		    break;

	    if (tokens.getTotalTokens() > 1)
	    {
		    if (tokens.getToken(0).toLowerCase().equals("-s"))
		    {
			    sSilent = true; // Silent failure
			    parms = tokens.getTokenRange(1, tokens.getTotalTokens());
		    }
		    else
			    parms = tokens.getTokenRange(0, tokens.getTotalTokens());
	    }

            boolean unloadSuccess = ((ScriptManager)getCapabilities().getDataStructure(DataStructures.ScriptManager)).removeScript(parms);

	    if (!unloadSuccess && !sSilent)
		    gui.printStatus("Unable to unload script; if you're sure the script is loaded, try using it's absolute pathname.");
	    }

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
	    {
	    ArrayList modesToPush;
	    Iterator modei;

	    modesToPush = createHomogenousChannelModesList(gui.getQuery(), "+", "v", parametersArray);

	    if (modesToPush == null)
		    break;

	    modei = modesToPush.listIterator();

	    while (modei.hasNext())
            	getCapabilities().sendln(modei.next().toString());
	    }

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
            
            if (ircData.isChannel(parms))
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

   // Convert parameters to an array list; if eliminateSpace is true, then all "blank" parameters are nuked.
   public ArrayList parmsToArrayList(String parms, boolean eliminateSpace)
   {
	   if (parms == null || parms.isEmpty())
		   return null;

	   if (eliminateSpace)
		   parms = parms.trim();

	   ArrayList pList = new ArrayList(Arrays.asList(parms.split(" ")));
	   ArrayList retList = new ArrayList(pList.size());
	   Iterator pi = pList.listIterator();

	   while (pi.hasNext())
	   {
		   String pValue = pi.next().toString();

		   if (eliminateSpace && (pValue.trim().length() < 1) )
			   continue;

		   retList.add(pValue);
	   }

	   return retList;
   }

   // Create homogenous channel MODE line(s) (that is, one mode type e.g. +o), and return it as an array with one MODE line per array element.
   // Make sure the 'mode' passed in is always lower-case UNLESS the mode is, per some odd ircd, upper-case. Targets should be passed as an array.
   public ArrayList createHomogenousChannelModesList(String channel, String modifier, String mode, ArrayList targets)
   {
	   // Sanity checks
	   if (mode == null || modifier == null || targets == null || channel == null || targets.isEmpty()) {
		   // Invalid input parameters
		   return null;
	   }

	   if (channel.length() < 1) {
		   // Empty channel
		   return null;
	   }

	   // TODO: Check for valid channel prefix, based on the information provided by the IRC server (e.g. some servers have more than '&' and '#').
	   if (!modifier.equals("+") && !modifier.equals("-")) {
		   // Invalid MODE modifier
		   return null;
	   }
	   
	   if (!mode.equals("o") && !mode.equals("v") && !mode.equals("b") && !mode.equals("h")) {
		   // Invalid mode
		   return null;
	   }

	   ArrayList modeLines = new ArrayList(); // Defaults to 10 initially, per Java documentation. This probably won't ever be exceeded, but it'll grow if it needs to!
	   Iterator ti = targets.listIterator();
	   StringBuffer tempModeLine = new StringBuffer();
	   StringBuffer tempTargetLine = new StringBuffer();
	   int modeCounter = 0;
	   int maxModes = ircData.getMaxModes(); // Maximum number of modes, per line

	   while (ti.hasNext())
	   {
		   String target = ti.next().toString();

		   if (tempModeLine == null)
			   tempModeLine = new StringBuffer();
		   if (tempTargetLine == null)
			   tempTargetLine = new StringBuffer();

		   tempModeLine.append(mode);
		   if (modeCounter > 0)
		  	 tempTargetLine.append(" ");
		   
		   tempTargetLine.append(target);

		   modeCounter++;
		   
		   if ( ( (modeCounter % maxModes) == 0) || (!ti.hasNext()) ) {
			   // Add MODE line to array
			   StringBuffer finishedLine = new StringBuffer();
			   finishedLine.append("MODE ");
			   finishedLine.append(channel);
			   finishedLine.append(" ");
			   finishedLine.append(modifier);
			   finishedLine.append(tempModeLine.toString());
			   finishedLine.append(" ");
			   finishedLine.append(tempTargetLine.toString());

			   modeLines.add(finishedLine.toString());

			   modeCounter = 0;

			   // This should signal to the garbage collector to clean these up, right? God damn Java..
			   tempModeLine = null;
			   tempTargetLine = null;
		   }
	   }
	   
	   return modeLines;
   }

   /* TODO: Fix this server code */
   public void connectToServer(String parms)
   {
      boolean secure   = false;
      String  host     = null;
      int     port     = 6667;
      String  password = null;
      int     loopCnt  = 0;
      StringStack stack = new StringStack(parms);
      String temp = null;

      /* Convoluted ass code ...
      while (!stack.isEmpty())
      {
      	temp = stack.pop();
	      if (loopCnt < 2)
	      {
		      if (temp.toLowerCase().equals("-ssl") || temp.toLowerCase().equals("-s")) {
			      secure = true;
		      }
		      else if (temp.toLowerCase().equals("-pass") || temp.toLowerCase().equals("-p"))
		      {
			      if (stack.isEmpty())
			      {
				      // Needed a password, but didn't get one.
				      getCapabilities().getUserInterface().printStatus("/server error: no password specified; no server specified..");
				      return;
			      }
			      else
			      {
			      	password = stack.pop();
			      }
		      } else {
			      if (loopCnt == 0)
				      host = temp;
			      else if (loopCnt == 1)
			      {
				      if (secure == true || password != null) {
					      host = temp;
				      }
				      else {
				     	 port = Integer.parseInt(temp);
				      }
			      }
		      }
	      }
	      else
	      {
		      if (loopCnt == 2)
		      {
			      if (secure == true && password != null) {
				      host = temp;
			      } else if (secure == true && password == null) {
				      try {
				      	port = Integer.parseInt(temp);
				      } catch (NumberFormatException ex) {
					      getCapabilities().getUserInterface().printStatus("/server error: invalid port number specified.");
					      return;
				      }
			      }
		      }
		      else if (loopCnt == 3)
		      {
			      if (secure == true && password != null && host != null)
			      {
				      try {
				      	port = Integer.parseInt(temp);
				      } catch (NumberFormatException ex) {
					      getCapabilities().getUserInterface().printStatus("/server error: invalid port number specified.");
					      return;
				      }
			      }
		      }
	      }

	      loopCnt++;
      }

      if (host == null)
      {
	      getCapabilities().getUserInterface().printStatus("/server error: must specify a server name.");
	      return;
      }
   */

	if (stack.isEmpty()) {
	    getCapabilities().getUserInterface().printStatus("Usage: /server [-ssl] [-pass <password>] <hostname> [port #]");
	    return;
	}

      temp = stack.pop();
    
      if (temp.toLowerCase().equals("-ssl") || temp.toLowerCase().equals("-s")) {
         secure = true;
	 if (stack.isEmpty()) {
	    getCapabilities().getUserInterface().printStatus("Usage: /server [-ssl] [-pass <password>] <hostname> [port #]");
	    return;
	 } else {
            temp   = stack.pop();
	 }
      }
      
      if (temp.toLowerCase().equals("-pass") || temp.toLowerCase().equals("-p")) {
	 if (stack.isEmpty()) {
	    getCapabilities().getUserInterface().printStatus("Usage: /server [-ssl] [-pass <password>] <hostname> [port #]");
	    return;
	 } else {
         	password = stack.pop();
		if (stack.isEmpty()) {
	    		getCapabilities().getUserInterface().printStatus("Usage: /server [-ssl] [-pass <password>] <hostname> [port #]");
	   		return;
		} else {
			temp = stack.pop();
		}
	 }
      }

      if (host == null)
	      host = temp;

      if (!stack.isEmpty())
      {
	      String portTemp = stack.pop();
	      try {
        	  port = Integer.parseInt(portTemp);
	      } catch (NumberFormatException ex) {
	    	  getCapabilities().getUserInterface().printStatus("Port # specified (" + portTemp + ") is not a valid number.");
	   	  return;
	      }
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
                 getCapabilities().sendln("MODE " + channel + " -b " + data.getToken(1)); // TODO: What's this?
             }
         }

         return ChatListener.EVENT_HALT;
      }
   }
}
