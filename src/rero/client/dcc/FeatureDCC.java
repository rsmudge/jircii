package rero.client.dcc;

import rero.dcc.*;

import rero.client.*;
import rero.client.user.*;

import rero.ircfw.*;
import rero.ircfw.interfaces.*;

import rero.util.*;

import java.io.*;
import java.util.*;

import rero.gui.*;
import rero.dialogs.dcc.*;

import rero.config.*;

import rero.dialogs.*;

public class FeatureDCC extends Feature implements ClientCommand, ChatListener, FrameworkConstants
{
   protected ChatFramework    ircfw;
   protected DataDCC          dccData;
   protected LocalInfo        localInfo;

   public FeatureDCC()
   {
      dccData = new DataDCC();
   }

   public void storeDataStructures(WeakHashMap data)
   {
      data.put("dcc", dccData);
   }

   public void init()
   {
      ircfw     = getCapabilities().getChatFramework();
      localInfo = (LocalInfo)getCapabilities().getDataStructure(DataStructures.LocalInfo);
   
      getCapabilities().addChatListener(this);
      getCapabilities().registerCommand("DCC", this); // register the dcc alias to this class      
      getCapabilities().registerCommand("SEND", this); // register the dcc alias to this class      
      getCapabilities().registerCommand("CHAT", this); // register the dcc alias to this class      
   }
 
   public void requestChat(String nickname)
   {
      Chat protocol  = new Chat(nickname);

      ListenDCC connect = new ListenDCC();
      connect.announceFramework(ircfw);
      connect.setImplementation(protocol);

      int port = connect.getListenerPort();
              
      if (port > -1)
      {
         String out = "PRIVMSG " + nickname + " :" + (char)1 + "DCC CHAT chat " + ClientUtils.longip(localInfo.localip()) + " " + port + "" + (char)1 + "";

         getCapabilities().getOutputCapabilities().fireSetConfused(ClientUtils.getEventHashMap(nickname, "CHAT " + port), nickname, "reply", "SEND_DCC");

         getCapabilities().sendln(out);

         dccData.addConnection(port+"", connect);
         connect.connect();          
      }
      else
      {
         connect.getImplementation().fireError("unable to establish a port");
         // else we had trouble listening for a dcc, fire an error set here?
      }
   }

   public void sendFile(String nickname, File file)
   {
      Send protocol  = new Send(nickname, file);

      ListenDCC connect = new ListenDCC();
      connect.announceFramework(ircfw);
      connect.setImplementation(protocol);

      int port = connect.getListenerPort();
              
      if (port > -1)
      {
         String fname = file.getName();
         if (ClientState.getClientState().isOption("dcc.fillspaces", ClientDefaults.dcc_fillspaces))
         {
            fname = fname.replace(' ', '_');
         }

         String out = "PRIVMSG " + nickname + " :" + (char)1 + "DCC SEND " + fname + " " + ClientUtils.longip(localInfo.localip()) + " " + port + " " + file.length() + (char)1 + "";

         getCapabilities().getOutputCapabilities().fireSetConfused(ClientUtils.getEventHashMap(nickname, "SEND " + port + " " + file.length() + " " + file.getAbsolutePath()), nickname, "reply", "SEND_DCC");

         getCapabilities().sendln(out);

         dccData.addConnection(port+"", connect);
         connect.connect();          
      }
      else
      {
         connect.getImplementation().fireError("unable to establish a port");
         // else we had trouble listening for a dcc
      }
   }

   public void runAlias(String command, String parameters)
   {
      command = command.toLowerCase();

      if (command.equals("send") || command.equals("chat"))
      {
         runAlias("dcc", command + " " + parameters);
         return;
      }

      if (!command.equals("dcc"))  // eh? wtf?!? I only registered as the /dcc command.  bastards
      {
         return;
      }

      TokenizedString temps = new TokenizedString(parameters);
      temps.tokenize(" ");
     
      if (temps.getToken(0).equals("stats"))
      {
         getCapabilities().getUserInterface().openDCCWindow();
      }
      else if (temps.getToken(0).equals("chat"))
      {
         requestChat(temps.getToken(1));        
      }
      else if (temps.getToken(0).equals("close"))
      {
          if (temps.getToken(1).charAt(0) == '=')
          {
             dccData.closeChat(temps.getToken(1).substring(1, temps.getToken(1).length()));
          }
          else if (temps.getTotalTokens() == 3)
          {
             int type = rero.client.functions.DCCOperators.getType(temps.getToken(1));
             dccData.closeConnection(temps.getToken(2), type);
          }
          else
          {
             dccData.closeConnection(temps.getToken(1));
          }
      }
      else if (temps.getToken(0).equals("accept"))
      {
         ConnectDCC temp = (ConnectDCC)dccData.getConnectionToAccept(temps.getToken(1));
         
         if (temps.getTotalTokens() >= 3 && temp.getImplementation() instanceof Receive)
         {
            ((Receive)(temp.getImplementation())).setFile(new File(temps.getToken(2)));
         }

         if (temp != null)
            temp.connect();
      }
      else if (temps.getToken(0).equals("send"))
      {
         File sendme;

         if (temps.getTotalTokens() == 2 || !(new File(temps.getToken(2))).exists())
         {
            sendme = DialogUtilities.showFileDialog("Send File", "Accept", null);
         }
         else
         {
            sendme = ClientUtils.getFile(temps.getToken(2));
         }

         if (sendme != null && sendme.exists())
         {
            sendFile(temps.getToken(1), sendme);
         }
      }
   }
    
   public boolean isChatEvent(String event, HashMap eventDescription)
   {
      String parms = (String)eventDescription.get($PARMS$);

      return event.equals("REQUEST") && parms.substring(0, 3).equals("DCC");
   }

   public int fireChatEvent(HashMap description)
   {
      TokenizedString temp = new TokenizedString(description.get("$parms").toString());
      temp.tokenize(" ");

      if (temp.getToken(1).equals("CHAT"))
      {
          int    port    = Integer.parseInt(temp.getToken(4));
          String server  = ClientUtils.longip(temp.getToken(3));

          if (port <= 0 || port == 19)
          {
             return EVENT_DONE;
          }

          Chat protocol  = new Chat( description.get($NICK$).toString() );

          ConnectDCC connect = new ConnectDCC(server, port);
          connect.announceFramework(ircfw);
          connect.setImplementation(protocol);

          dccData.addConnection(port+"", connect);

          boolean checkDialog = ClientState.getClientState().getInteger("dcc.onchat", ClientDefaults.dcc_accept) == 0 && ChatRequest.showDialog(getCapabilities().getGlobalCapabilities().getFrame(), connect);
          boolean checkAutoAccept = ClientState.getClientState().getInteger("dcc.onchat", ClientDefaults.dcc_accept) == 1;

          if (checkDialog || checkAutoAccept)
             connect.connect();          
       }
       else if (temp.getToken(1).equals("CLOSE"))
       {
          dccData.closeChat(temp.getToken(2).substring(1, temp.getToken(2).length()));
       }
       else if (temp.getToken(1).equals("ACCEPT"))
       {
          ConnectDCC tempc = (ConnectDCC)dccData.getConnection(temp.getToken(3));

          if (tempc != null)
          {
             getCapabilities().getOutputCapabilities().fireSetStatus(description, "RESUME_SUCCEEDED");
             tempc.connect();
          }
          else
          {
             getCapabilities().getOutputCapabilities().fireSetStatus(description, "RESUME_FAILED");
          }
       }
       else if (temp.getToken(1).equals("RESUME"))
       {
          // DCC RESUME "" 4099 595468
          GenericDCC tempc = dccData.getConnection(temp.getToken(3));
        
          if (tempc != null && tempc.getImplementation().getTypeOfDCC() == ProtocolDCC.DCC_SEND)
          {
             Send send = (Send)tempc.getImplementation();

             if (send.resume(Long.parseLong(temp.getToken(4))))
             {
                 getCapabilities().getOutputCapabilities().fireSetStatus(description, "RESUME_REQUEST");

                 String output = "PRIVMSG " + description.get($NICK$) + " :" + (char)1 + "DCC ACCEPT " + temp.getTokenFrom(2) + (char)1;

                 getCapabilities().sendln(output);

                 tempc.connect();
             }
             else
             {
                 getCapabilities().getOutputCapabilities().fireSetStatus(description, "RESUME_REQUEST_ERROR");
             }
          }
          else
          {
             // requested resume failed... eh
          }
       }
       else if (temp.getToken(1).equals("SEND"))
       {
          //  0   1     2       3         4     5
          // DCC SEND ror.r02 3232235801 41005 2862333

          int offset = temp.getTotalTokens() - 6;          

          int    port    = Integer.parseInt(temp.getToken(4 + offset));
          String server  = ClientUtils.longip(temp.getToken(3 + offset));

          if (port < 1024)
          {
             return EVENT_DONE;
          }

          String fstring = temp.getToken(2);
     
          if (offset > 0)
            fstring = temp.getTokenRange(2, 2 + offset);

          fstring = (new File(fstring)).getName(); // strip off any path information

          File   output  = new File(ClientState.getClientState().getString("dcc.saveto", ClientDefaults.dcc_saveto), fstring);

          Receive protocol = new Receive( description.get($NICK$).toString(), output, Long.parseLong(temp.getToken(5 + offset)));

          ConnectDCC connect = new ConnectDCC(server, port);
          connect.announceFramework(ircfw);
          connect.setImplementation(protocol);

          dccData.addConnection(port+"", connect);

          boolean checkDialog = ClientState.getClientState().getInteger("dcc.onsend", ClientDefaults.dcc_accept) == 0 && SendRequest.showDialog(getCapabilities().getGlobalCapabilities().getFrame(), connect);
          boolean checkAutoAccept = ClientState.getClientState().getInteger("dcc.onsend", ClientDefaults.dcc_accept) == 1;

          if (checkDialog || checkAutoAccept)
          {
              handleReceive(protocol, connect, description, temp);
          }
       }

       return EVENT_DONE;
   }

   private void handleReceive(Receive protocol, ConnectDCC connect, HashMap description, TokenizedString temp)
   {
       File output = protocol.getFile();

       // check if a resume is necessary //
       if (output.exists())
       {
          int resumeOption = DCCUtilities.DetermineResumeOption(getCapabilities(), connect);

          switch (resumeOption)
          {
             case DCCUtilities.RESUME_OPTION_SELECTED:
               if (output.length() < protocol.getExpectedSize())
               {
                  protocol.pleaseResume();  // tells the dcc object that we want a resume so it sets up all the offsets and such.
   
                  String outputz = "PRIVMSG " + description.get($NICK$) + " :" + ((char)1) + "DCC RESUME " + temp.getToken(2) + " " + temp.getToken(4) + " " + output.length() + ((char)1);

                  getCapabilities().sendln(outputz);
                  getCapabilities().getOutputCapabilities().fireSetStatus(description, "SEND_RESUME_REQUEST");
               }
               else
               {
                  protocol.fireError("file already completed, no need to resume");
               }
               break;
             case DCCUtilities.RENAME_OPTION_SELECTED:
               boolean option = SendRequest.showDialog(getCapabilities().getGlobalCapabilities().getFrame(), connect);

               if (option)
               {
                  handleReceive(protocol, connect, description, temp);
               }
               break;

             case DCCUtilities.OVERWRITE_OPTION_SELECTED:
               connect.connect();
               break;
          }
       }
       else
       {
          connect.connect();          
       }
   }
}
