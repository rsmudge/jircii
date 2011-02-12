package rero.client.server;

import rero.ircfw.*;
import rero.ircfw.interfaces.*;
import rero.ircfw.data.*;

import rero.util.*;
import java.util.*;

import rero.client.*;
import rero.client.output.*;

import rero.config.*;

public class ProcessEvents extends Feature implements FrameworkConstants, ChatListener
{
   protected InternalDataList ircData;
   protected OutputCapabilities output;
   protected SyncCheck          syncs   = new SyncCheck();

   public void init()
   {
      output       = getCapabilities().getOutputCapabilities();
    
      ircData = (InternalDataList)getCapabilities().getDataStructure("clientInformation");

      getCapabilities().addChatListener(this);
   }

   public int fireChatEvent (HashMap eventDescription)
   {
      String target  = (String) eventDescription.get($TARGET$);
      String nick    = (String) eventDescription.get($NICK$);
      String channel = (String) eventDescription.get($TARGET$);
      String event   = (String) eventDescription.get($EVENT$);
 
      if (event.equals("PRIVMSG"))
      {
         if (ClientUtils.isChannel(target))
         {
/*             if (!getCapabilities().getUserInterface().isActive(channel))
             {
                 rero.util.ClientUtils.dump(eventDescription);
             } */

             output.fireSetTarget(eventDescription, channel, output.chooseSet(channel, "CHANNEL_TEXT", "CHANNEL_TEXT_INACTIVE"));
             touchUser(nick, target);
         }
         else
         {
             if (ClientState.getClientState().isOption("auto.query", ClientDefaults.auto_option))
             {
                boolean isSelected = ! ClientState.getClientState().isOption("auto.hide", ClientDefaults.auto_option);
                getCapabilities().getUserInterface().openQueryWindow(nick, isSelected);
             }
             output.fireSetQuery(eventDescription, nick, target, "PRIVMSG");
         }
      }
      else if (event.equals("MODE"))
      {
         if (ClientUtils.isChannel(target))
         {
             output.fireSetTarget(eventDescription, channel, "CHANNEL_MODE");
             getCapabilities().getUserInterface().notifyWindow(channel);
         }
         else
         {
             output.fireSetTarget(eventDescription, nick, "USER_MODE"); 
             getCapabilities().getUserInterface().notifyWindow(nick);
         }
//         rero.util.ClientUtils.dump(eventDescription);
      }
      else if (event.equals("NOTICE"))
      {
         if (nick == null || nick.length() == 0)
         {
            output.fireSetStatus(eventDescription, "NOTICE");
         }
         else if (ClientUtils.isChannel(target) || ClientState.getClientState().isOption("active.notice", ClientDefaults.active_option)) 
         {
            output.fireSetConfused(eventDescription, target, "notice", "NOTICE");
         }
         else
         {
            output.fireSetAllTarget2(eventDescription, nick, "NOTICE");
         }

//         rero.util.ClientUtils.dump(eventDescription);
      }
      else if (event.equals("ACTION"))
      {
         if (ClientUtils.isChannel(target))
         {
             output.fireSetTarget(eventDescription, channel, output.chooseSet(channel, "ACTION", "ACTION_INACTIVE"));
             touchUser(nick, target);
         }
         else
         {
             if (ClientState.getClientState().isOption("auto.query", ClientDefaults.auto_option))
             {
                boolean isSelected = ! ClientState.getClientState().isOption("auto.hide", ClientDefaults.auto_option);
                getCapabilities().getUserInterface().openQueryWindow(nick, isSelected);
             }

             output.fireSetTarget(eventDescription, nick, "PRIVACTION"); 
         }
      }
      else if (event.equals("JOIN"))
      {
         if (eventDescription.get("$nick").equals(ircData.getMyNick()))
         {
            syncs.addChannel(channel.toString().toLowerCase());
            getCapabilities().sendln("MODE " + channel.toString()); 

            //
            // make this an option later
            //

            if (ClientState.getClientState().isOption("update.ial", ClientDefaults.update_ial))
            {
               UpdateIAL checkIAL = new UpdateIAL();
               getCapabilities().addTemporaryListener(checkIAL);
               getCapabilities().sendln("WHO " + channel.toString());
            }

            //
            // auto /window on join
            //
            if (ClientState.getClientState().isOption("auto.join", ClientDefaults.auto_option))
            {
               getCapabilities().getUserInterface().openChannelWindow(ircData.getChannel(channel));
            }
            else
            {
               getCapabilities().getUserInterface().setQuery(channel.toString());
            }
         }

         output.fireSetTarget(eventDescription, channel, "CHANNEL_JOIN");
      }
      else if (event.equals("KICK"))
      {
         if (eventDescription.get("$nick").equals(ircData.getMyNick()))
         {
            output.cycleQuery();
         }
         output.fireSetTarget(eventDescription, channel, "CHANNEL_KICK");
      }
      else if (event.equals("PART"))
      {
         if (eventDescription.get("$nick").equals(ircData.getMyNick()))
         {
            output.cycleQuery();
         }

         if (ClientState.getClientState().isOption("auto.part", ClientDefaults.auto_option) && ClientState.getClientState().isOption("auto.join", ClientDefaults.auto_option) && ! getCapabilities().getUserInterface().isWindow(channel))
         {
            // do nothing if there is no window open and we have the option set to part the channel on closing the window.
         }
         else
         {
            output.fireSetTarget(eventDescription, channel, "CHANNEL_PART");
         }
      }
      else if (event.equals("QUIT"))
      {
         output.fireSetAllDeadTarget(eventDescription, nick, "USER_QUIT");
      }
      else if (event.equals("NICK"))
      {
         getCapabilities().getUserInterface().notifyActiveWindow();

         if (eventDescription.containsKey("$parms"))
            output.fireSetAllTarget(eventDescription, eventDescription.get("$parms").toString(), "USER_NICK");

         if (getCapabilities().getUserInterface().isWindow(nick))
         {
            getCapabilities().getUserInterface().renameWindow(nick, eventDescription.get("$parms").toString());
         }
      }
      else if (event.equals("TOPIC"))
      {
         output.fireSetTarget(eventDescription, channel, "CHANNEL_TOPIC_CHANGED");
         getCapabilities().getUserInterface().notifyWindow(channel);
         touchUser(nick, target);
      }
      else if (event.equals("SIGNON"))
      {
         output.fireSetConfused(eventDescription, null, "notify", "NOTIFY_SIGNON");
      }
      else if (event.equals("SIGNOFF"))
      {
         output.fireSetConfused(eventDescription, null, "notify", "NOTIFY_SIGNOFF");
      }
      else if (event.equals("REQUEST"))
      {
         String type = (String)eventDescription.get("$type");
         if (type.equals("VERSION"))
         {
            getCapabilities().sendln("NOTICE " + nick + " :" + (char)1 + "VERSION " + ClientUtils.ShowVersion() + (char)1);
         }
         if (type.equals("PING"))
         {
            getCapabilities().sendln("NOTICE " + nick + " :" + (char)1 + eventDescription.get("$parms") + (char)1);
         }
         if (type.equals("CLIENTINFO"))
         {
            getCapabilities().sendln("NOTICE " + nick + " :" + (char)1 + "CLIENTINFO ACTION CLIENTINFO DCC PING SOURCE TIME VERSION" + (char)1);
         }
         if (type.equals("SOURCE"))
         {
            getCapabilities().sendln("NOTICE " + nick + " :" + (char)1 + "SOURCE http://jircii.dashnine.org" + (char)1);
         }
         if (type.equals("TIME"))
         {
            getCapabilities().sendln("NOTICE " + nick + " :" + (char)1 + "TIME " + ClientUtils.TimeDateStamp(System.currentTimeMillis() / 1000) + (char)1);
         }

         if (type.equals("DCC"))
         {
            output.fireSetConfused(eventDescription, target, "ctcp", "DCC_REQUEST");
         }
         else
         {
            output.fireSetConfused(eventDescription, target, "ctcp", "CTCP_REQUEST");
         }
      }
      else if (event.equals("CHAT_OPEN"))
      {
         nick = "=" + eventDescription.get("$nick");

         if (ClientState.getClientState().isOption("auto.chat", ClientDefaults.auto_option))
         {
            boolean isSelected = ! ClientState.getClientState().isOption("auto.hide", ClientDefaults.auto_option);
            getCapabilities().getUserInterface().openQueryWindow(nick, isSelected);
         }

         output.fireSetTarget(eventDescription, nick, "CHAT_OPEN");
      }
      else if (event.equals("CHAT"))
      {
         nick = "=" + eventDescription.get("$nick");

         output.fireSetTarget(eventDescription, nick, "CHATMSG");
      }
      else if (event.equals("ERROR"))
      {
         output.fireSetAll(eventDescription, "SERVER_ERROR");
      }
      else if (event.equals("CHAT_CLOSE"))
      {
         nick = "=" + eventDescription.get("$nick");

         if (ClientState.getClientState().isOption("auto.chatclose", ClientDefaults.auto_option) && ! getCapabilities().getUserInterface().isWindow(nick))
         {
            // we don't have a window anymore, and we have something enabled to close the chat on closing the window as such
            // we'll do nothing now.
         }
         else
         {
            output.fireSetTarget(eventDescription, nick, "CHAT_CLOSE");
         }
      }
      else if (event.equals("SEND_FAILED") || event.equals("RECEIVE_FAILED") || event.equals("SEND_COMPLETE") || event.equals("RECEIVE_COMPLETE") || event.equals("SEND_START") || event.equals("RECEIVE_START"))
      {
         output.fireSetConfused(eventDescription, nick, "ctcp", event);
         getCapabilities().getUserInterface().notifyActiveWindow();
      }
      else if (event.equals("REPLY"))
      {
         TokenizedString data = new TokenizedString((String)eventDescription.get($PARMS$));
         data.tokenize(" ");

         if (data.getToken(0).equals("PING") && data.getTotalTokens() > 1)
         {
            try
            {
               long temp = Long.parseLong(data.getToken(1));
               eventDescription.put("$pt", ClientUtils.formatLongAsDecimal(System.currentTimeMillis() - temp) + "s");
            } catch (Exception ex) { }
         }

         output.fireSetConfused(eventDescription, (String)eventDescription.get($TARGET$), "reply", "CTCP_REPLY");
      }
      else if (event.equals("INVITE"))
      {
         output.fireSetActive(eventDescription, "INVITE");
      }
      else if (event.equals("329") && output.isSet("CHANNEL_CREATED"))
      {
         TokenizedString data = new TokenizedString((String)eventDescription.get($PARMS$));
         data.tokenize(" ");

         eventDescription.put("$created", ClientUtils.TimeDateStamp(Long.parseLong(data.getToken(1))));

         output.fireSetTarget(eventDescription, data.getToken(0), "CHANNEL_CREATED");
      }
      else if (event.equals("324"))
      {
         TokenizedString data = new TokenizedString((String)eventDescription.get($PARMS$));
         data.tokenize(" ");

         channel = data.getToken(0);

         if (syncs.isSyncing(channel.toLowerCase()))
         {
            eventDescription.put("$sync", ClientUtils.formatLongAsDecimal(syncs.getSyncTime(channel.toLowerCase())));
            output.fireSetTarget(eventDescription, channel, "JOIN_SYNC");
         }
         else if (output.isSet("CHANNEL_MODE_IS"))
         {
            eventDescription.put("$parms", data.getTokenFrom(1) );
            eventDescription.put("$data", data.getTokenFrom(0) );
       
            output.fireSetTarget(eventDescription, data.getToken(0), "CHANNEL_MODE_IS");
         }
         else
         {
            processNumeric("324", eventDescription);
         }
  
         getCapabilities().getUserInterface().notifyWindow(channel);
      }
      else if ((event.equals("332") || event.equals("331")) && output.isSet("CHANNEL_TOPIC_IS"))
      { 
         TokenizedString data = new TokenizedString((String)eventDescription.get($PARMS$));
         data.tokenize(" ");

         eventDescription.put("$parms", data.getTokenFrom(1) );
         eventDescription.put("$data", data.getTokenFrom(0) );
       
         output.fireSetTarget(eventDescription, data.getToken(0), "CHANNEL_TOPIC_IS");
      }
      else if (event.equals("333") && output.isSet("CHANNEL_TOPIC_SETBY"))
      {
         TokenizedString data = new TokenizedString((String)eventDescription.get($PARMS$));
         data.tokenize(" ");

         eventDescription.put("$seton", ClientUtils.TimeDateStamp(Long.parseLong(data.getToken(2))));
         
         nick = data.getToken(1);
         if (nick.indexOf('!') > -1)
         {
            nick = nick.substring(0, nick.indexOf('!'));
         }

         eventDescription.put("$nick", nick);

         output.fireSetTarget(eventDescription, data.getToken(0), "CHANNEL_TOPIC_SETBY");
      }
      else if (event.equals("353") && output.isSet("CHANNEL_NAMES"))
      {
         TokenizedString data = new TokenizedString((String)eventDescription.get($PARMS$));
         data.tokenize(" ");

         output.fireSetTarget(eventDescription, data.getToken(1), "CHANNEL_NAMES");
      }
      else if (event.equals("353") && output.isSet("FORMATTED_NAMES"))
      {
         TokenizedString data = new TokenizedString((String)eventDescription.get($PARMS$));
         data.tokenize(" ");

         eventDescription.put("$total", (data.getTotalTokens() - 2) + "");

         output.fireSetTarget(eventDescription, data.getToken(1), "FORMATTED_NAMES_HEADER");

         if (data.getTotalTokens() < 3 || data.getToken(2).equals(":"))
         {
            return 0;
         }

         String[] names = new String[data.getTotalTokens() - 2];

         for (int x = 0; x < names.length; x++)
         {
            char n = data.getToken(x + 2).charAt(0);
            if (ircData.getPrefixInfo().isPrefixChar(n))
            {
               eventDescription.put("$nick", data.getToken(x + 2).substring(1, data.getToken(x + 2).length()));
            }
            else
            {
               eventDescription.put("$nick", data.getToken(x + 2));
            }

            names[x] = output.parseSet(eventDescription, "FORMATTED_NAMES");
         }         
         output.echo(data.getToken(1), names, .85);
      }
      else if (event.equals("367") && output.isSet("CHANNEL_BANLIST"))
      {
         TokenizedString data = new TokenizedString((String)eventDescription.get($PARMS$));
         data.tokenize(" ");

         output.fireSetTarget(eventDescription, data.getToken(0), "CHANNEL_BANLIST");
      }
      else if (event.equals("368") && output.isSet("CHANNEL_BANLIST_END"))
      {
         TokenizedString data = new TokenizedString((String)eventDescription.get($PARMS$));
         data.tokenize(" ");

         output.fireSetTarget(eventDescription, data.getToken(0), "CHANNEL_BANLIST_END");
      }
      else if ((event.equals("375") || event.equals("376") || event.equals("372")) && !ClientState.getClientState().isOption("option.showmotd", ClientDefaults.option_showmotd))
      {
         // do nothing as 372 is the MOTD reply
      }
      else if (eventDescription.get($NUMERIC$) != null)
      {
         String numeric = (String)eventDescription.get($NUMERIC$);
         processNumeric(numeric, eventDescription);
      }
      else
      {
         output.fireSetStatus(eventDescription, event);
//         rero.util.ClientUtils.dump(eventDescription);
      }
 
      return 0;
   }

   private ChatListener allowAway = new AllowAwayReply(); // kind of a hack but bare with me...

   public void processNumeric(String numeric, HashMap eventDescription)
   {
      int temp = Integer.parseInt(numeric);

      String type = "NUMERIC";

      if (output.isSet("REPL_" + numeric))
      {
         type = "REPL_" + numeric;
      }

      if (isWhoisNumeric(temp)) /* looking for /whois related numerics */
      {
          if (!(allowAway.isChatEvent(numeric, eventDescription) && allowAway.fireChatEvent(eventDescription) == ChatListener.EVENT_HALT))
          {
             output.fireSetConfused(eventDescription, null, "whois", type);
          }
          else
          {
             // halting numeric... its a numeric we don't want to display
          }
      }
      else
      {
          output.fireSetStatus(eventDescription, type);
      }
   }
 
   public boolean isChatEvent(String eventId, HashMap eventDescription)
   {
      return true;
   }

   private static boolean isWhoisNumeric(int x)
   {
      return ((x >= 311 && x < 321) || x == 338 || x == 369 || x == 301 || x == 302 || x == 307 || x == 308 || x == 330 || x == 401 || x == 406 || x == 378 || x == 379 || x == 310 || x== 671 || x == 703);
   }

   private static class AllowAwayReply implements ChatListener
   {
      private boolean awayIsGood = false;
      
      public boolean isChatEvent(String eventId, HashMap eventDescription)
      {
         return (eventId.equals("301") || eventId.equals("311") || eventId.equals("318"));
      }

      public int fireChatEvent(HashMap desc)
      {
         if ( "311".equals(desc.get($EVENT$)) )
             awayIsGood = true;

         if ( "318".equals(desc.get($EVENT$)) )
             awayIsGood = false;

         if ( "301".equals(desc.get($EVENT$)) && !awayIsGood )
             return ChatListener.EVENT_HALT;

         return ChatListener.EVENT_DONE;
      }
   }

    private void touchUser(String nick, String channel)
    {
        User user = ircData.getUser(nick);

        boolean wasIdle = user.isIdle();

        user.touch();

        if (wasIdle && ircData.getChannel(channel) != null) {
            ChannelDataWatch temp = ircData.getChannelDataWatch(ircData.getChannel(channel));
            if (temp != null)
                temp.userChanged();
        }
    }
}
