package rero.dcc;

import rero.ircfw.*;
import rero.ircfw.interfaces.*;

import java.io.*;
import java.net.*;
import java.util.*;

public abstract class ProtocolDCC
{
   public static final int DCC_SEND    = 001;
   public static final int DCC_RECEIVE = 002;
   public static final int DCC_CHAT    = 003;

   public static final int STATE_WAIT   = 401;
   public static final int STATE_OPEN   = 402; // 3 different states of a dcc connection.  
   public static final int STATE_CLOSED = 403;

   public static final int DCC_TIMEOUT  = 60 * 1000 * 2; // 2 minutes

   protected String             nickname;
   protected Socket             socket;

   protected long               idleTime;
   protected long               startTime;

   protected HashMap            eventData  = new HashMap();
   protected ProtocolDispatcher dispatcher;

   protected int                state = STATE_WAIT; // by default we are in a waiting state.

   /** return the type of DCC based on a constant */
   public abstract int getTypeOfDCC();

   public void close()
   {
      try
      {
         socket.close();
         state = STATE_CLOSED;
      }
      catch (Exception ex)
      {
         ex.printStackTrace();
      }
   }

   public int getState()
   {
      if (state == STATE_OPEN && !isConnected())
      {
         state = STATE_CLOSED;
      }
      return state;
   }

   public String getRemoteAddress()
   {
      return socket.getInetAddress().getHostAddress();
   }

   public int getLocalPort()
   {
      return socket.getLocalPort();
   }

   public int getPort()
   {
      return socket.getPort();
   }

   /** tells the protocol implementation class that we are ready to rock and roll, Socket is assumed to be connected. */
   public void announceFramework(ChatFramework f)
   {
      dispatcher = f.getProtocolDispatcher();
   }

   public abstract void run();

   /** returns wether or not the socket is connected */
   public boolean isConnected()
   {
       return socket != null && socket.isConnected();
   }

   /** returns the nickname of who we are having a *chat* with */
   public String getNickname()
   {
       return nickname;
   }

   /** returns the number of milliseconds since this chat has been active */
   public long getIdleTime() 
   {
      return (System.currentTimeMillis() - idleTime);
   }

   /** return time that this chat started */
   public long getStartTime()
   {
      return startTime;
   }
 
   /** return total amount of time this chat has been active (in milliseconds) */
   public long getTotalTime()
   {
      return System.currentTimeMillis() - startTime;
   }

   public void setDCCSocket(Socket _socket)
   {
      socket = _socket;

      startTime = System.currentTimeMillis();
      idleTime  = System.currentTimeMillis();

      state     = STATE_OPEN;
   }

   public abstract void fireError(String text);
  
}
