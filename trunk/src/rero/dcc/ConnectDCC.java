package rero.dcc;

import java.net.*;

public class ConnectDCC extends GenericDCC
{
   protected String server;
   protected int    port;

   public ConnectDCC(String _server, int _port)
   {
       server = _server;
       port   = _port;
   }

   public int getPort()
   {
       return port;
   }

   public String getHost()
   {
       return server;
   }

   public Socket establishConnection()
   {
       try
       { 
          Socket sock = new Socket(server, port);
          return sock;
       }
       catch (ConnectException cex)
       {
          getImplementation().fireError(cex.getMessage());
       }
       catch (Exception ex)
       {
          ex.printStackTrace();
          getImplementation().fireError(ex.getMessage());
       }

       return null;
   }
}
