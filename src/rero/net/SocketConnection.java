package rero.net;

import rero.net.SocketEvent;
import rero.net.interfaces.SocketDataListener;
import rero.net.interfaces.SocketStatusListener;

import java.io.*;
import java.net.*;
import java.util.LinkedList;
import java.util.ListIterator;

import javax.net.ssl.*;

import rero.util.*;

import rero.config.*;

public class SocketConnection implements Runnable, ClientStateListener
{
   protected Socket aSocket;
   protected Thread readThread;
   protected PrintStream    aSocketOutput;
   protected BufferedReader aSocketInput;
   protected long           delay;        // number of ms to wait before trying to connect.

   public void propertyChanged(String property, String value)
   {
      try
      {
         if (aSocketInput != null && aSocket.isConnected())
         {
            aSocketInput  = new BufferedReader(ClientState.getProperInputStream(aSocket.getInputStream()));
            aSocketOutput = ClientState.getProperPrintStream(aSocket.getOutputStream());
         }
      }
      catch (Exception ex)
      {
         System.out.println("Unable to switch encodings...");
         ex.printStackTrace();
      }
   }

   public void println(String message)
   {
      try 
      {
         aSocketOutput.println(message);
         aSocketOutput.flush();
      }
      catch (Exception ex)
      {
         ex.printStackTrace();
      }
   }

   public void disconnect()
   {
      if (aSocket != null && aSocket.isConnected())
      {
         try
         {
            if (!aSocket.isOutputShutdown()) aSocket.shutdownOutput();
            if (!aSocket.isInputShutdown()) aSocket.shutdownInput();
            aSocket.close();
         }
         catch (Exception ex)
         {
            // pretty safe to ignore, we may just make it so this exception goes no where eventually //
            ex.printStackTrace();
         }
         aSocket = null;
      }
   }

   public void connect(String host, int port)
   {
      connect(host, port, 0, null, false);
   }

   public void connect(String host, int port, long _delay, String password, boolean secure)
   {
      delay  = _delay;

      if (readThread != null)
      {
         try 
         {
            disconnect();

            readThread.interrupt();    // gotta stop the stupid thing somehow.

            readThread.join(1 * 1000); // wait for the disconnection to actually
                                       // happen.  This may kill the responsiveness
                                       // of the application.  Something to watch
                                       // out for.
         }
         catch (Exception ex)
         {
            ex.printStackTrace();
         }
      }

      getSocketInformation().hostname = host;
      getSocketInformation().port     = port;
      getSocketInformation().isSecure = secure;
      getSocketInformation().password = password;
      getSocketInformation().network  = "Unknown";

//      System.out.println(getSocketInformation().hostname + ", " + getSocketInformation().port + ", " + getSocketInformation().isSecure + ", " + getSocketInformation().password);

      readThread = new Thread(this);
      readThread.setName("Socket Read Thread for: " + host);
      readThread.start();
   }

   public void run()
   {
      aSocketInput  = null;
      aSocketOutput = null;

      try
      {
         if (delay > 0) Thread.sleep(delay);
      }
      catch (Exception ex)
      {
         ex.printStackTrace();
         return;
      } 

      //
      // Attempt to connect...
      //
      try
      {
         // once config system is in place have an option to jack up socket time outs.

         if (getSocketInformation().isSecure) /* ratdog - you owe me one you bastard! */
         {
            aSocket = (new SecureSocket(getSocketInformation().hostname, getSocketInformation().port)).getSocket();
         }
         else
         {
            aSocket = new Socket(getSocketInformation().hostname, getSocketInformation().port);
         }

         aSocket.setSoLinger(true, 5); // 5 second socket disconnect linger time to flush data.
         aSocket.setTcpNoDelay(true);  // we'll try this out, it might suck?
         aSocket.setSoTimeout(5 * 60 * 1000); // timeout after half an hour, why not eh?

         /* setup our streams for input and output */

         aSocketOutput = ClientState.getProperPrintStream(aSocket.getOutputStream());
         aSocketInput  = new BufferedReader(ClientState.getProperInputStream(aSocket.getInputStream()));
      }
      catch (UnknownHostException ex1)
      {
         fireStatusEvent("unable to resolve hostname", false);
         return;
      }
      catch (Exception ex2)
      {
         if (Thread.currentThread() != readThread)  // check to make sure this isn't a left over connection attempt.
         {
            return;  
         }

         fireStatusEvent(ex2.getMessage(), false);
         return;
      }
    
      if (aSocket != null)
      {
         fireStatusEvent("success", true);
      }
          
      /* we are connected, let's loop, read messages, and fire events... the good life */

      String data = null;

      do
      {
         try
         {
            data = aSocketInput.readLine();

            if (data != null)
               fireReadEvent(data);             
         }
         catch (SocketException ex1)
         {
            fireStatusEvent(ex1.getMessage(), false);
            shutdownSocket();
            return;
         }
         catch (IOException ex2)
         {
            fireStatusEvent(ex2.getMessage(), false);
            shutdownSocket();
            return;
         }
         catch (Exception ex3)
         {
            if (aSocket == null || !aSocket.isConnected())
            {
               fireStatusEvent(ex3.getMessage(), false);
               shutdownSocket();
               return;
            }

            ex3.printStackTrace();
         }
      } 
      while (aSocket != null && aSocket.isConnected() && data != null);

      fireStatusEvent("disconnected.", false);
      shutdownSocket();      
   }

   private void shutdownSocket()
   {
      try
      {
         if (aSocketInput != null) { aSocketInput.close(); }
         if (aSocketOutput != null) { aSocketOutput.close(); }
         if (aSocket != null) { aSocket.close(); }
      }
      catch (Exception ex) { ex.printStackTrace(); }         
   }

   //
   // data structure
   //
   protected SocketInformation connectionInformation;

   public SocketInformation getSocketInformation()
   {
      return connectionInformation;
   }

   private static class StripCodesListener implements ClientStateListener
   {
      public void propertyChanged(String key, String value)
      {
         stripcodes = ClientState.getClientState().isOption("client.stripcodes", ClientDefaults.client_stripcodes);
      }
   }

   public SocketConnection()
   {
      connectionInformation = new SocketInformation();      

      event.socket = this;
      event.data   = connectionInformation;

      ClientState.getClientState().addClientStateListener("client.encoding", this);

      if (listener2 == null)
      {
         listener2 = new StripCodesListener();
         listener2.propertyChanged(null, null);
         ClientState.getClientState().addClientStateListener("client.stripcodes", listener2);
      }
   }

   private static boolean stripcodes;
   private static ClientStateListener listener2 = null;

   LinkedList connectDisconnectListeners = new LinkedList();
   LinkedList messageReadListeners       = new LinkedList();

   SocketEvent event                 = new SocketEvent();

   public void removeSocketStatusListener(SocketStatusListener l)
   {
      connectDisconnectListeners.remove(l);
   }

   public void removeSocketDataListener(SocketDataListener l)
   {
      messageReadListeners.remove(l);
   }

   public void addSocketStatusListener(SocketStatusListener l)
   {
      connectDisconnectListeners.addFirst(l);
   }

   public void addSocketDataListener(SocketDataListener l)
   {
      messageReadListeners.addFirst(l);
   }

   public void fireStatusEvent(String message, boolean isConnected)
   {
      getSocketInformation().isConnected = isConnected;

      event.message = message;
      event.valid   = true;

      ListIterator en = connectDisconnectListeners.listIterator(); 
      while (en.hasNext() && event.valid)
      {
         SocketStatusListener temp = (SocketStatusListener)en.next();
         temp.socketStatusChanged(event);
      }
   }

   public void fireReadEvent(String message)
   {
      if (stripcodes)
      {
         message = ClientUtils.strip(message);
      }

      event.message = message;
      event.valid   = true;

      ListIterator en = messageReadListeners.listIterator(); 
      while (en.hasNext() && event.valid)
      {
         SocketDataListener temp = (SocketDataListener)en.next();
         temp.socketDataRead(event);
      }
   }
}
