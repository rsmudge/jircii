/* 
    jerk.irc.dcc.ChatClient [ created - 1/2/02 ]

    Author: Raphael Mudge (rsmudge@mtu.edu)
    
    Description: 
    handles the details of talking in a DCC connection,  nothing to do
    with actually establishing the connection.  Talks to a JerkEngine
    reference.

    Documentation: 
    n/a

    Changelog:
       
*/

package rero.dcc;

import java.util.*;

import java.net.*;
import java.io.*;

import rero.ircfw.*;
import rero.ircfw.interfaces.*;

import rero.config.*;

public class Chat extends ProtocolDCC implements ClientStateListener
{   
    protected BufferedReader     input;
    protected PrintStream        output;

    public Chat(String _nickname)
    {
       nickname = _nickname;
       ClientState.getClientState().addClientStateListener("client.encoding", this);
   }

    public void propertyChanged(String property, String value)
    {
      try
      {
         if (input != null && socket.isConnected())
         {
            input  = new BufferedReader(   ClientState.getClientState().getProperInputStream( socket.getInputStream() )   );
            output = ClientState.getClientState().getProperPrintStream( socket.getOutputStream() );
         }
      }
      catch (Exception ex)
      {
         System.out.println("Unable to switch encodings...");
         ex.printStackTrace();
      }
    }
  
    /** returns the nickname of who we are having a *chat* with */
    public String getNickname()
    {
       return nickname;
    }

    /** sends a message to the chat */
    public void sendln(String text)
    {
       output.println(text);
    }
    
    public int getTypeOfDCC()
    {
       return DCC_CHAT;
    }

    public void run()
    {
       if (socket == null || !socket.isConnected())
       {
          return;
       }

       try
       {
          socket.setKeepAlive(true);
       }
       catch (Exception ex)
       {
          ex.printStackTrace();
       }

       fireEvent("CHAT_OPEN", null);

       String text;

       try
       {
          output = ClientState.getClientState().getProperPrintStream( socket.getOutputStream() );
          input  = new BufferedReader(   ClientState.getClientState().getProperInputStream( socket.getInputStream() )   );

          while (socket.isConnected())
          {
             text = input.readLine();
             if (text == null)
             {
                fireEvent("CHAT_CLOSE", "closed");
                return;
             } 

             idleTime = System.currentTimeMillis();

             fireEvent("CHAT", text);
          } 
         
          fireEvent("CHAT_CLOSE", "closed");
       }
       catch (Exception ex)
       {
          ex.printStackTrace();

          fireError(ex.getMessage());
       }
    }

    public void fireEvent(String event, String description)
    {
       eventData.clear();

       eventData.put(FrameworkConstants.$NICK$,   getNickname());
       eventData.put(FrameworkConstants.$EVENT$,  event);
       eventData.put("$this", this.toString());

 
       if (description != null)
       {
          eventData.put(FrameworkConstants.$DATA$,  getNickname() + " " + description);
          eventData.put(FrameworkConstants.$PARMS$, description); 
       }

       dispatcher.dispatchEvent(eventData);
    }

    public void fireError(String description)
    {
       eventData.put(FrameworkConstants.$NICK$,   getNickname());
       eventData.put(FrameworkConstants.$EVENT$, "CHAT_CLOSE");
       eventData.put(FrameworkConstants.$DATA$,  getNickname() + " " + description);
       eventData.put(FrameworkConstants.$PARMS$, description);
       eventData.put("$this", this.toString());

       dispatcher.dispatchEvent(eventData);
    }
}
