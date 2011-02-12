package rero.ident;

import java.net.*;
import java.io.*;
import java.util.*;

import rero.config.*;

import java.lang.ref.WeakReference;

public class IdentDaemon implements Runnable, ClientStateListener
{
    private static IdentDaemon identd;

    protected LinkedList listeners  = new LinkedList();

    public void addIdentListener(IdentListener l)
    {
       listeners.add(new WeakReference(l));
    }

    public void fireEvent(String host, String text)
    {
       Iterator i = listeners.iterator();
       while (i.hasNext())
       {
          WeakReference temp = (WeakReference)i.next();

          if (temp.get() != null)
             ((IdentListener)temp.get()).identRequest(host, text);
       }
    }

    public static void initialize()
    {
       identd = new IdentDaemon();
    }

    public static IdentDaemon getIdentDaemon()
    {
       return identd;
    }

    public void propertyChanged(String key, String value)
    {
       setup();
    }

    public IdentDaemon()
    {
       ClientState.getClientState().addClientStateListener("ident.enabled", this);
       setup();
    }

    public void setup()
    {
       if (ClientState.getClientState().isOption("ident.enabled", ClientDefaults.ident_enabled) && serverThread == null)
       {
          serverThread = new Thread(this);
          serverThread.setPriority(Thread.MIN_PRIORITY);
          serverThread.setName("jIRCii Ident Daemon");
          serverThread.start();
       }
       else if (ClientState.getClientState().isOption("ident.enabled", ClientDefaults.ident_enabled) == false && serverThread != null)
       {
          close();
       }
    } 

    public void close()
    {
       if (serverThread != null)
       {
          if (listener != null && ! listener.isClosed())
             try { listener.close(); } catch (Exception ex) { }

          if (activeClient != null && ! activeClient.isClosed())
             try { activeClient.close(); } catch (Exception ex) { }

          serverThread.interrupt();
          serverThread = null;
       }
    }

    private Thread  serverThread;

    private Socket         activeClient;
    private ServerSocket   listener;

    private BufferedReader socketInput; 
    private PrintWriter    socketOutput;

    public void run() 
    { 
       try
       {
          listener = new ServerSocket(ClientState.getClientState().getInteger("ident.port", ClientDefaults.ident_port));
       } 
       catch (Exception ex2)
       {
          ex2.printStackTrace();
          close();
          return;
       }

       while (Thread.currentThread() == serverThread)
       {
          try
          {
             activeClient = listener.accept();

             if (activeClient != null && activeClient.isConnected())
             {
                socketInput  = new BufferedReader(new InputStreamReader(activeClient.getInputStream()));
                socketOutput = new PrintWriter(activeClient.getOutputStream(),true);
  
                String text = socketInput.readLine();
                fireEvent(activeClient.getInetAddress().getHostAddress(), text);

                socketOutput.print(text + " : USERID : "+ClientState.getClientState().getString("ident.system", ClientDefaults.ident_system)+" : "+ ClientState.getClientState().getString("ident.userid", ClientDefaults.ident_userid));
                socketOutput.flush();

                socketInput.close();
                socketOutput.close();
                activeClient.close();
             }
          }
          catch (Exception ex)
          {
             ex.printStackTrace();
             try
             {
                Thread.sleep(10 * 1000); // sleep for 10 seconds if something nasty happens, then *shrug* try again?

                if (listener == null || listener.isClosed())
                   listener = new ServerSocket(ClientState.getClientState().getInteger("ident.port", ClientDefaults.ident_port));
             }
             catch (Exception ex1) 
             { 
                ex1.printStackTrace();
             }
          }
       }
   }
}

