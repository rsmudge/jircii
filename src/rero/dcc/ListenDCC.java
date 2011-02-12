package rero.dcc;

import java.net.*;
import rero.config.*;

public class ListenDCC extends GenericDCC
{
   protected ServerSocket server = null;

   protected static int offset     = 0;
   protected static int timeout    = 60 * 1000;

   public static int getNextPort()
   {
       int rangeStart = ClientState.getClientState().getInteger("dcc.low", ClientDefaults.dcc_low);
       int rangeStop  = ClientState.getClientState().getInteger("dcc.high", ClientDefaults.dcc_high);

       offset += 1;
       offset = offset % (rangeStop - rangeStart);

       return rangeStart + offset;
   }

   /** instructs the class to listen for a connection on some port, returns the port as an integer.  A return value of -1 
       indicates there was a problem binding to the port.  Doh!@ */
   public int getListenerPort()
   {
       try
       {
          server = new ServerSocket(getNextPort());
          return server.getLocalPort();
       }
       catch (Exception ex)
       {
          ex.printStackTrace();
       }

       return -1;
   }

   public Socket establishConnection()
   {
       try
       {
          server.setSoTimeout(timeout);
          return server.accept();
       }
       catch (SocketTimeoutException stex)
       {
          getImplementation().fireError("timed out waiting for connection");
       }
       catch (Exception ex)
       {
          ex.printStackTrace();

          getImplementation().fireError(ex.getMessage());
       }

       return null;
   }
}
