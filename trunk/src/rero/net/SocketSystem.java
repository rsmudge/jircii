/* 
       .;:: Socket System ::;. 
 
    Exports Structures, capabilities, and event wiring for the socket 
    subsystem.  Fun, eh?

*/                   

package rero.net;

import java.util.WeakHashMap;

import rero.net.interfaces.SocketDataListener;
import rero.net.interfaces.SocketStatusListener;

public class SocketSystem
{
   protected SocketConnection aConnection;

   public SocketSystem()
   {
      aConnection = new SocketConnection();
   }
   
   public SocketConnection getSocket()
   {
      return aConnection;      
   }

   // === Export Data Structures ========================================================================================

   public void storeDataStructures(WeakHashMap centralDataRepository)
   {
      centralDataRepository.put("socketInformation", aConnection.getSocketInformation());
   }

   // === Export Capabilities ============================================================================================

         // (note: capabilities are exported in this manner solely to
         //  facilitate a quick and easy way to access them.  For stable
         //  API's capability proxies should just access the reference
         //  with the API directly to avoid the extra function call overhead).

   public void println (String message)
   {
      getSocket().println(message);
   }

   public void connect (String host, int port)
   {
      getSocket().connect(host, port);
   }

   public void disconnect()
   {
      getSocket().disconnect();
   }

   // === Export Events ==================================================================================================
   
   public void addSocketDataListener(SocketDataListener l)
   {
      getSocket().addSocketDataListener(l);
   }

   public void addSocketStatusListener(SocketStatusListener l)
   {
      getSocket().addSocketStatusListener(l);
   }
}
