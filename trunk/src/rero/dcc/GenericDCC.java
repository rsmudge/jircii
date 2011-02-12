package rero.dcc;

import java.io.*;
import java.net.*;

import rero.ircfw.*;

public abstract class GenericDCC implements Runnable
{
   protected ProtocolDCC       impl;
   protected ChatFramework       fw;

   public void announceFramework(ChatFramework f)
   {
       fw = f;
   }

   public void setImplementation(ProtocolDCC _impl)
   {
       impl = _impl;

       impl.announceFramework(fw);
   }

   public String getNickname()
   {
       return getImplementation().getNickname();
   }

   public int getTypeOfDCC() 
   {
       return getImplementation().getTypeOfDCC();
   }

   public int getState()
   {
       return getImplementation().getState();
   }

   public ProtocolDCC getImplementation()
   {
       return impl;
   }

   public void connect()
   {
       Thread fred = new Thread(this);
       fred.start();
   }

   public abstract Socket establishConnection();

   public void run()
   {
          Socket sock = establishConnection();
 
          try { sock.setKeepAlive(true); } catch (Exception ex) { }

          impl.setDCCSocket( sock );
          impl.run();
          if (impl.isConnected())
             impl.close(); 
   }
}
